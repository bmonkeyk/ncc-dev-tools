package com.yingling.devconfig.action.button;

import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;
import org.apache.commons.lang.StringUtils;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * 选择tables路径
 */
public class SelTablePathAction extends AbstractButtonAction {
    public SelTablePathAction(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void doAction(ActionEvent event) {
        String tablePath = System.getenv("NCC_ENV_TABLES");
        JFileChooser chooser = null;
        if (StringUtils.isBlank(tablePath)) {
            tablePath = getDialog().getComponent(JTextField.class, "tablesText").getText();
        }
        if (StringUtils.isNotBlank(tablePath)) {
            tablePath = new File(tablePath).getParent();
        }
        chooser = new JFileChooser(tablePath);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = chooser.showOpenDialog(getDialog());
        if (JFileChooser.APPROVE_OPTION != ret) {
            return;
        }
        tablePath = chooser.getSelectedFile().getAbsolutePath();
        getDialog().getComponent(JTextField.class, "tablesText").setText(tablePath);
    }
}
