package com.yingling.devconfig.util;

import com.intellij.openapi.ui.Messages;
import com.yingling.base.NccEnvSettingService;
import com.yingling.devconfig.DevConfigDialog;
import com.yingling.dictionary.util.SearchTableUtil;
import com.yingling.script.studio.connection.ConnectionService;
import com.yingling.script.studio.connection.PoolFacade;
import com.yingling.script.studio.connection.ierp.IerpDataSourceProvider;
import com.yingling.script.studio.ui.preference.dbdriver.DatabaseDriverInfo;
import com.yingling.script.studio.ui.preference.dbdriver.DriverInfo;
import com.yingling.script.studio.ui.preference.prop.DataSourceMeta;
import com.yingling.script.studio.ui.preference.prop.ToolUtils;
import com.yingling.script.studio.ui.preference.xml.PropXml;
import org.apache.commons.lang.StringUtils;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.io.File;
import java.text.MessageFormat;

/**
 * 数据源初始化工具类
 */
public class DataSourceUtil {

    public static void initDataSource(DevConfigDialog dialog) {

        String homePath = dialog.getComponent(JTextField.class, "homeText").getText();

        if (StringUtils.isBlank(homePath)) {
            return;
        }

        DatabaseDriverInfo[] driverinfos = null;
        PropXml propXml = new PropXml();
        try {
            //数据库类型
            driverinfos = propXml.getDriverSet(homePath).getDatabase();
            fillCombo(dialog.getComponent(JComboBox.class, "dbTypeBox"), driverinfos, dialog);

            //数据源列表
            String filename = homePath + "/ierp/bin/prop.xml";
            File file = new File(filename);
            if (file.exists()) {
                DataSourceMeta[] sourceMetas = propXml.getDSMetaWithDesign(filename);
                fillCombo(dialog.getComponent(JComboBox.class, "dbBox"), sourceMetas, dialog);
            }
            //做一次值切换，触发监听显示数据源详情
            dialog.getComponent(JComboBox.class, "dbBox").setSelectedIndex(-1);
            dialog.getComponent(JComboBox.class, "dbBox").setSelectedIndex(0);
        } catch (Exception e) {
            return;
        }
    }

    public static void fillCombo(JComboBox combo, Object[] objects, DevConfigDialog dlg) {

        String[] items = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            items[i] = obj.toString();
            if (combo == dlg.getComponent(JComboBox.class, "dbBox")) {
                dlg.getDataSourceMetaMap().put(items[i], (DataSourceMeta) obj);
            } else if (combo == dlg.getComponent(JComboBox.class, "dbTypeBox")) {
                dlg.getDatabaseDriverInfoMap().put(items[i], (DatabaseDriverInfo) obj);
            } else if (combo == dlg.getComponent(JComboBox.class, "driverBox")) {
                dlg.getDriverInfoMap().put(items[i], (DriverInfo) obj);
            }
        }
        combo.setModel(new DefaultComboBoxModel(items));
    }

    /**
     * 更新当前选中数据源
     *
     * @param dlg
     */
    public static void syncCurrDataSourceValue(DevConfigDialog dlg) {
        String driverName = (String) dlg.getComponent(JComboBox.class, "driverBox").getSelectedItem();
        DriverInfo info = dlg.getDriverInfoMap().get(driverName);
        String exampleUrl = info.getDriverUrl();
        String host = dlg.getComponent(JTextField.class, "hostText").getText();
        String port = dlg.getComponent(JTextField.class, "portText").getText();
        String oid = dlg.getComponent(JTextField.class, "oidText").getText();
        String userName = dlg.getComponent(JTextField.class, "userText").getText();
        String pwd = dlg.getComponent(JTextField.class, "pwdText").getText();
        String dbName = dlg.getComponent(JTextField.class, "dbNameText").getText();

        if (ToolUtils.isJDBCUrl(exampleUrl)) {
            dlg.getCurrMeta().setDatabaseUrl(ToolUtils.getJDBCUrl(exampleUrl, dbName,
                    host, port));
        } else {
            dlg.getCurrMeta().setDatabaseUrl(ToolUtils.getODBCUrl(exampleUrl, dbName));
        }
        dlg.getCurrMeta().setUser(userName);
        dlg.getCurrMeta().setPassword(pwd);
        dlg.getCurrMeta().setDriverClassName(info.getDriverClass());
        dlg.getCurrMeta().setDatabaseType((String) dlg.getComponent(JComboBox.class, "dbTypeBox").getSelectedItem());
        dlg.getCurrMeta().setOidMark(oid);
        dlg.getDataSourceMetaMap().put(dlg.getCurrMeta().getDataSourceName(), dlg.getCurrMeta());
    }

    private static String findDriverType(String driverClass, DriverInfo[] infos) {
        for (int i = 0; i < infos.length; i++) {
            DriverInfo info = infos[i];
            if (info.getDriverClass().equals(driverClass))
                return info.getDriverType();
        }
        return "";
    }

    /**
     * 填充数据源信息到界面
     *
     * @param dlg
     */
    public static void fillDataSourceMeta(DevConfigDialog dlg) {
        DataSourceMeta meta = dlg.getCurrMeta();
        if (meta != null) {
            String dbtye = meta.getDatabaseType();
            if (dbtye != null) {
                String dt = dbtye.split("-")[0];
                dlg.getComponent(JComboBox.class, "dbTypeBox").setSelectedItem(dt);
                DatabaseDriverInfo data = dlg.getDatabaseDriverInfoMap().get(dt);
                if (data == null) {
                    Messages.showMessageDialog(MessageFormat.format("Can't find the specified type of datasource{0}", new Object[]{dt}), "提示", Messages.getInformationIcon());
                } else {
                    DriverInfo[] infos = data.getDatabase();
                    dlg.getComponent(JComboBox.class, "driverBox").setSelectedItem(findDriverType(meta.getDriverClassName(), infos));
                }
            }
            fillDBConnUrl(dlg, meta.getDatabaseUrl());
            dlg.getComponent(JTextField.class, "oidText").setText((meta.getOidMark() != null) ? meta.getOidMark() : "XX");
            dlg.getComponent(JTextField.class, "userText").setText((meta.getUser() != null) ? meta.getUser() : "sa");
            dlg.getComponent(JTextField.class, "pwdText").setText((meta.getPassword() != null) ? meta.getPassword() : "sa");
            dlg.getComponent(JCheckBox.class, "baseChx").setSelected(meta.isBase());
            dlg.getComponent(JCheckBox.class, "devChx").setSelected(meta.isDesign());
        }

    }

    /**
     * 填充数据库地址信息
     *
     * @param dlg
     * @param url
     */
    private static void fillDBConnUrl(DevConfigDialog dlg, String url) {
        if (ToolUtils.isJDBCUrl(url)) {
            String[] jdbc = ToolUtils.getJDBCInfo(url);
            dlg.getComponent(JTextField.class, "hostText").setText(jdbc[0]);
            dlg.getComponent(JTextField.class, "portText").setText(jdbc[1]);
            dlg.getComponent(JTextField.class, "dbNameText").setText(jdbc[2]);

        } else {
            dlg.getComponent(JTextField.class, "hostText").setText("");
            dlg.getComponent(JTextField.class, "portText").setText("");
            dlg.getComponent(JTextField.class, "dbNameText").setText("");
        }

    }

    public static void fillDBConnByInfo(DevConfigDialog dialog, String driverUrl) {
        if (ToolUtils.isJDBCUrl(driverUrl)) {
            String[] jdbc = ToolUtils.getJDBCInfo(driverUrl);
            dialog.getComponent(JTextField.class, "portText").setText(jdbc[1]);
        } else {
            dialog.getComponent(JTextField.class, "portText").setText("");
        }
    }


    /**
     * 数据源保存
     *
     * @param dlg
     */
    public static void saveDesignDataSourceMeta(DevConfigDialog dlg) {
        try {
            NccEnvSettingService service = NccEnvSettingService.getInstance();
            syncCurrDataSourceValue(dlg);
            String nchome = service.getNcHomePath();
            String filename = nchome + "/ierp/bin/prop.xml";
            File file = new File(filename);
            if (file.exists()) {
                int count = dlg.getDataSourceMetaMap().size();
                DataSourceMeta[] metas = new DataSourceMeta[count];
                for (int i = 0; i < metas.length; i++) {
                    metas[i] = dlg.getDataSourceMetaMap().get(dlg.getComponent(JComboBox.class, "dbBox").getItemAt(i));
                    metas[i].setMaxCon(50);
                    metas[i].setMinCon(1);
                }
                new PropXml().saveMeta(filename, metas);
                //更新数据字典数据源列表
                ConnectionService.getPoolFacade().getProvider().reset();
                SearchTableUtil.getInstance().reLoad();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
