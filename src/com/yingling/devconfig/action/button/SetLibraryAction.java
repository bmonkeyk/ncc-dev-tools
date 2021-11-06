package com.yingling.devconfig.action.button;

import com.intellij.openapi.ui.Messages;
import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;
import com.yingling.base.BusinessException;
import com.yingling.library.util.LibrariesUtil;

import javax.swing.JTextField;
import java.awt.event.ActionEvent;

public class SetLibraryAction extends AbstractButtonAction {
    public SetLibraryAction(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void doAction(ActionEvent event) throws BusinessException {
        String homePath = getDialog().getComponent(JTextField.class, "homeText").getText();
        LibrariesUtil.setLibraries(homePath);
        Messages.showInfoMessage("设置完成！","提示");
    }
}
