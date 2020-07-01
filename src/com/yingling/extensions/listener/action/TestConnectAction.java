package com.yingling.extensions.listener.action;

import com.intellij.openapi.ui.Messages;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;
import com.yonyou.uap.studio.connection.ConnectionService;
import com.yonyou.uap.studio.connection.model.DataSourceMetaInfo;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;

/**
 * 数据源测试按钮
 */
public class TestConnectAction extends AbstractButtonAction {
    public TestConnectAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        DataSourceMeta currMeta = dlg.getCurrmeta();
        if (currMeta == null)
            return;
        DataSourceMetaInfo meta = new DataSourceMetaInfo();
        meta.setUser(currMeta.getUser());
        meta.setPwd(currMeta.getPassword());
        meta.setUrl(currMeta.getDatabaseUrl());
        meta.setDriver(currMeta.getDriverClassName());
        boolean flag;
        try {
            flag = ConnectionService.testConnection(meta);
        } catch (Exception e1) {
            String msg = MessageFormat.format("connection failed.(jdbcurl:{0}; user:{1}; pwd:{2}; driver:{3})", new Object[]{currMeta.getDatabaseUrl(), currMeta.getUser(),
                    currMeta.getPassword(), currMeta.getDriverClassName()});
            Messages.showMessageDialog("Test failed,Please check input or cfg!" + '\n' + msg, "tips", Messages.getInformationIcon());
            return;
        }
        if (flag) {
            Messages.showMessageDialog("Test Succeed!", "tips", Messages.getInformationIcon());
        }

    }
}
