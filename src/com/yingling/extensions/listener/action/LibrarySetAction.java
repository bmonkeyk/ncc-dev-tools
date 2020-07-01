package com.yingling.extensions.listener.action;

import com.intellij.openapi.ui.Messages;
import com.pub.exception.BusinessException;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;
import com.yingling.libraries.listener.LibrariesJarSetListener;

import java.awt.event.ActionEvent;

/**
 * 设置类路径按钮
 */
public class LibrarySetAction extends AbstractButtonAction {

    public LibrarySetAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {

        try {
            LibrariesJarSetListener.setLibraries(dlg.getHomeText().getText());
            Messages.showInfoMessage("success", "tips");
        } catch (BusinessException e) {
            e.printStackTrace();
            Messages.showInfoMessage(e.getMessage(), "tips");
        }
    }

}
