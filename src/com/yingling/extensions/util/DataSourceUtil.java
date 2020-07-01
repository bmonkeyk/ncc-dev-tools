package com.yingling.extensions.util;

import com.intellij.openapi.ui.Messages;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.service.NccEnvSettingService;
import nc.uap.plugin.studio.ui.preference.dbdriver.DatabaseDriverInfo;
import nc.uap.plugin.studio.ui.preference.dbdriver.DriverInfo;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;
import nc.uap.plugin.studio.ui.preference.prop.ToolUtils;
import nc.uap.plugin.studio.ui.preference.xml.PropXml;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.io.File;
import java.text.MessageFormat;

public class DataSourceUtil {
    public static void initDataSourceComposite(NccDevSettingDlg dlg) {

        dlg.getDataSourceMetaBox().setSelectedItem("");
        dlg.getDatabaseDriverInfoBox().setSelectedItem("");
        dlg.getDriverInfoBox().setSelectedItem("");

        NccEnvSettingService service = NccEnvSettingService.getInstance();
        String homePath = service.getNcHomePath();
        if (StringUtils.isBlank(homePath)) {
            homePath = dlg.getHomeText().getText();
        }

        if (StringUtils.isBlank(homePath)) {
            Messages.showMessageDialog("Please set nchome first ", "error", Messages.getInformationIcon());
            dlg.getNccSetTab().setSelectedIndex(0);
            return;
        }

        DatabaseDriverInfo[] driverinfos = null;
        PropXml propXml = new PropXml();
        try {
            //数据库类型
            driverinfos = propXml.getDriverSet(homePath).getDatabase();
            fillCombo(dlg.getDatabaseDriverInfoBox(), driverinfos, dlg);

            //数据源列表
            String filename = homePath + "/ierp/bin/prop.xml";
            File file = new File(filename);
            if (file.exists()) {
                fillCombo(dlg.getDataSourceMetaBox(), propXml.getDSMetaWithDesign(filename), dlg);
            }
        } catch (Exception e) {
            dlg.getNccSetTab().setSelectedIndex(0);
            Messages.showMessageDialog("Please check the nchome\n" + e.getMessage(), "error", Messages.getInformationIcon());
            return;
        }

        syncCurrDataSourceValue(dlg);
    }

    /**
     * 设置下拉选项
     *
     * @param combo
     * @param objects
     * @param dlg
     */
    public static void fillCombo(JComboBox combo, Object[] objects, NccDevSettingDlg dlg) {

        String[] items = new String[0];
        if (objects == null || objects.length == 0) {
            dlg.setCurrmeta(null);
            dlg.getDataSourceMetaMap().clear();
            dlg.getDatabaseDriverInfoMap().clear();
            dlg.getDriverInfoMap().clear();
            combo.setModel(new DefaultComboBoxModel(items));
            combo.setSelectedIndex(-1);
            combo.setSelectedIndex(0);
            return;
        }

        if (combo == dlg.getDataSourceMetaBox()) {
            dlg.setCurrmeta(null);
            dlg.getDataSourceMetaMap().clear();
        } else if (combo == dlg.getDatabaseDriverInfoBox()) {
            dlg.getDatabaseDriverInfoMap().clear();
        } else if (combo == dlg.getDriverInfoBox()) {
            dlg.getDriverInfoMap().clear();
        }

        items = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            items[i] = obj.toString();
            if (combo == dlg.getDataSourceMetaBox()) {
                dlg.getDataSourceMetaMap().put(items[i], (DataSourceMeta) obj);
            } else if (combo == dlg.getDatabaseDriverInfoBox()) {
                dlg.getDatabaseDriverInfoMap().put(items[i], (DatabaseDriverInfo) obj);
            } else if (combo == dlg.getDriverInfoBox()) {
                dlg.getDriverInfoMap().put(items[i], (DriverInfo) obj);
            }
        }
        combo.setModel(new DefaultComboBoxModel(items));
        combo.setSelectedIndex(-1);
        combo.setSelectedIndex(0);
    }

    public static void syncCurrDataSourceMeta(NccDevSettingDlg dlg) {
        if (dlg.getCurrmeta() == null) {
            return;
        }
        DriverInfo info = dlg.getDriverInfoMap().get(dlg.getDriverInfoBox().getSelectedItem());
        String exampleurl = info.getDriverUrl();
        dlg.getCurrmeta().setOidMark(dlg.getConnOID());
        String host = dlg.getConnHost();
        String port = dlg.getConnPort();
        String dbname = dlg.getConnName();
        if (ToolUtils.isJDBCUrl(exampleurl)) {
            dlg.getCurrmeta().setDatabaseUrl(ToolUtils.getJDBCUrl(exampleurl, dbname,
                    host, port));
        } else {
            dlg.getCurrmeta().setDatabaseUrl(ToolUtils.getODBCUrl(exampleurl, dbname));
        }
        dlg.getCurrmeta().setUser(dlg.getConnUser());
        dlg.getCurrmeta().setPassword(dlg.getConnPasswd());
        dlg.getCurrmeta().setDriverClassName(info.getDriverClass());
        dlg.getCurrmeta().setDatabaseType((String) dlg.getDatabaseDriverInfoBox().getSelectedItem());
        dlg.getDataSourceMetaMap().put(dlg.getCurrmeta().getDataSourceName(), dlg.getCurrmeta());
    }

    public static void fillDBConnByInfo(NccDevSettingDlg dlg, String url) {
        if (ToolUtils.isJDBCUrl(url)) {
            String[] jdbc = ToolUtils.getJDBCInfo(url);
            dlg.getPortText().setText(jdbc[1]);
        } else {
            dlg.getPortText().setText("");
        }
    }

    public static void fillDataSourceMeta(NccDevSettingDlg dlg) {
        DataSourceMeta meta = dlg.getCurrmeta();
        if (meta != null) {
            String dbtye = meta.getDatabaseType();
            if (dbtye != null) {
                String dt = dbtye.split("-")[0];
                dlg.getDatabaseDriverInfoBox().setSelectedItem(dt);
                DatabaseDriverInfo data = dlg.getDatabaseDriverInfoMap().get(dt);
                if (data == null) {
                    Messages.showMessageDialog(MessageFormat.format("Can't find the specified type of datasource{0}", new Object[]{dt}), "提示", Messages.getInformationIcon());
                } else {
                    DriverInfo[] infos = data.getDatabase();
                    dlg.getDriverInfoBox().setSelectedItem(findDriverType(meta.getDriverClassName(), infos));
                }
            }
            fillDBConnUrl(dlg, meta.getDatabaseUrl());
            dlg.getOidText().setText((meta.getOidMark() != null) ? meta.getOidMark() : "XX");
            dlg.setConnOID(dlg.getOidText().getText());
            dlg.getUserNameTest().setText((meta.getUser() != null) ? meta.getUser() : "sa");
            dlg.setConnUser(dlg.getUserNameTest().getText());
            dlg.getPwdText().setText((meta.getPassword() != null) ? meta.getPassword() : "sa");
            dlg.setConnPasswd(String.valueOf(dlg.getPwdText().getPassword()));
            dlg.getBaseCheck().setSelected(meta.isBase());
            dlg.getDevCheck().setSelected(meta.isDesign());
        }
    }

    private static String findDriverType(String driverClass, DriverInfo[] infos) {
        for (int i = 0; i < infos.length; i++) {
            DriverInfo info = infos[i];
            if (info.getDriverClass().equals(driverClass))
                return info.getDriverType();
        }
        return "";
    }

    private static void fillDBConnUrl(NccDevSettingDlg dlg, String url) {
        if (ToolUtils.isJDBCUrl(url)) {
            String[] jdbc = ToolUtils.getJDBCInfo(url);
            dlg.getHostText().setText(jdbc[0]);
            dlg.setConnHost(jdbc[0]);
            dlg.getPortText().setText(jdbc[1]);
            dlg.setConnPort(jdbc[1]);
            dlg.getDbNameText().setText(jdbc[2]);
            dlg.setConnName(jdbc[2]);

        } else {
            dlg.getHostText().setText("");
            dlg.setConnHost("");
            dlg.getPortText().setText("");
            dlg.setConnPort("");
            dlg.getDbNameText().setText("");
            dlg.setConnName("");
        }
    }

    public static void syncCurrDataSourceValue(NccDevSettingDlg dlg) {
        dlg.setConnHost(dlg.getHostText().getText());
        dlg.setConnName(dlg.getDbNameText().getText());
        dlg.setConnOID(dlg.getOidText().getText());
        dlg.setConnPasswd(String.valueOf(dlg.getPwdText().getPassword()));
        dlg.setConnPort(dlg.getPortText().getText());
        dlg.setConnUser(dlg.getUserNameTest().getText());
    }

    /**
     * 数据源保存
     *
     * @param dlg
     */
    public static void saveDesignDataSourceMeta(NccDevSettingDlg dlg) {
        try {
            NccEnvSettingService service = NccEnvSettingService.getInstance();
            syncCurrDataSourceValue(dlg);
            syncCurrDataSourceMeta(dlg);
            String nchome = service.getNcHomePath();
            String filename = nchome + "/ierp/bin/prop.xml";
            File file = new File(filename);
            if (file.exists()) {
                int count = dlg.getDataSourceMetaBox().getItemCount();
                DataSourceMeta[] metas = new DataSourceMeta[count];
                for (int i = 0; i < metas.length; i++) {
                    metas[i] = dlg.getDataSourceMetaMap().get(dlg.getDataSourceMetaBox().getItemAt(i));
                    metas[i].setMaxCon(50);
                    metas[i].setMinCon(1);
                }
                new PropXml().saveMeta(filename, metas);
                dlg.setDirty(false);
            }
        } catch (Exception e) {
        }
    }
}
