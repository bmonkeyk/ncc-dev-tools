package com.yingling.extensions.listener.action;

import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;
import com.yingling.extensions.service.NccEnvSettingService;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * home选择按钮
 */
public class HomeSelAction extends AbstractButtonAction {

    public HomeSelAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        {
            String ncHome = NccEnvSettingService.getInstance().getNcHomePath();
            JFileChooser chooser = null;
            if (StringUtils.isBlank(ncHome)) {
                ncHome = dlg.getHomeText().getText();
            }
            if (StringUtils.isNotBlank(ncHome)) {
                ncHome = new File(ncHome).getParent();
            }
            chooser = new JFileChooser(ncHome);
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = chooser.showOpenDialog(dlg);
            if (JFileChooser.APPROVE_OPTION != ret) {
                return;
            }
            dlg.getHomeText().setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
}
