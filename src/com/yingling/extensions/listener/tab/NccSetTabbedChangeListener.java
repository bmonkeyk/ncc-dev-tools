package com.yingling.extensions.listener.tab;

import com.intellij.openapi.ui.Messages;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractTabListener;
import com.yingling.extensions.service.NccEnvSettingService;
import com.yingling.extensions.util.DataSourceUtil;
import com.yonyou.uap.studio.connection.ConnectionService;
import com.yonyou.uap.studio.connection.model.DataSourceMetaInfo;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;
import org.apache.commons.lang.StringUtils;

import javax.swing.event.ChangeEvent;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;

public class NccSetTabbedChangeListener extends AbstractTabListener {
    public NccSetTabbedChangeListener(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void afterChange(ChangeEvent event, NccDevSettingDlg dlg) {
        int index = dlg.getNccSetTab().getSelectedIndex();
        if (index == 1
                && StringUtils.isBlank(dlg.getHomeText().getText())
                && StringUtils.isBlank(NccEnvSettingService.getInstance().getNcHomePath())) {
            dlg.getNccSetTab().setSelectedIndex(0);
            Messages.showMessageDialog("Please set nchome first", "tips", Messages.getInformationIcon());
            return;
        }

        if (index == 1) {//数据源页签
            DataSourceUtil.initDataSourceComposite(dlg);
        }

        if (index == 2) {
            String message = "please set datasource first";
            DataSourceMeta currMeta = dlg.getCurrmeta();
            if (currMeta == null) {
                Messages.showMessageDialog(message, "tips", Messages.getInformationIcon());
                return;
            }
            String connectedDataSource = NccEnvSettingService.getInstance().getConnected_data_source();
            String currMetaStr = currMeta.getDatabaseUrl() + currMeta.getDataSourceName() + currMeta.getUser();
            if (StringUtils.isBlank(connectedDataSource) || !connectedDataSource.equals(currMetaStr)) {
                //测试数据源
                DataSourceMetaInfo meta = new DataSourceMetaInfo();
                meta.setUser(currMeta.getUser());
                meta.setPwd(currMeta.getPassword());
                meta.setUrl(currMeta.getDatabaseUrl());
                meta.setDriver(currMeta.getDriverClassName());
                try {
                    ConnectionService.testConnection(meta);
                    NccEnvSettingService.getInstance().setConnected_data_source(currMetaStr);
                } catch (Exception e1) {
                    String msg = MessageFormat.format("connection failed.(jdbcurl:{0}; user:{1}; pwd:{2}; driver:{3})", new Object[]{currMeta.getDatabaseUrl(), currMeta.getUser(),
                            currMeta.getPassword(), currMeta.getDriverClassName()});
                    Messages.showMessageDialog("Test failed,Please check input or cfg!" + '\n' + msg, "tips", Messages.getInformationIcon());
                    return;
                }
            }
        }
        if (index == dlg.getNccSetTab().getTabCount() - 1) {//最后一个，关于页签
            dlg.getAboutTab().setSelectedIndex(0);
            new AboutTabbedChangeListener(dlg).afterChange(null, dlg);
        }

    }

    @Override
    protected void click(MouseEvent event, NccDevSettingDlg dlg) {

    }
}
