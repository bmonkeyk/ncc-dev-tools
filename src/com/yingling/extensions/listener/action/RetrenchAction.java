package com.yingling.extensions.listener.action;

import com.intellij.openapi.ui.Messages;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;
import com.yingling.extensions.service.NccEnvSettingService;
import com.yingling.libraries.listener.RetrenchModuleListener;
import org.apache.commons.lang.StringUtils;

import java.awt.event.ActionEvent;

/**
 * 精简nc home
 */
public class RetrenchAction extends AbstractButtonAction {


    public RetrenchAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        if (StringUtils.isBlank(dlg.getHomeText().getText())
                && StringUtils.isBlank(NccEnvSettingService.getInstance().getNcHomePath())) {
            dlg.getNccSetTab().setSelectedIndex(0);
            Messages.showMessageDialog("Please set nchome first", "tips", Messages.getInformationIcon());
            return;
        }
        RetrenchModuleListener.retrench(dlg.getHomeText().getText());
    }
}
