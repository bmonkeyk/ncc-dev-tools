package com.yingling.devconfig.action.button;

import com.yingling.abs.AbstractButtonAction;
import com.yingling.base.NccEnvSettingService;
import com.yingling.devconfig.DevConfigDialog;
import com.yingling.devconfig.util.DataSourceUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * 选择home
 */
public class SelHomePathAction extends AbstractButtonAction {
    public SelHomePathAction(DevConfigDialog dialog) {
        super(dialog);
    }

    @Override
    public void doAction(ActionEvent event) {
        String ncHome = NccEnvSettingService.getInstance().getNcHomePath();
        JFileChooser chooser;
        if (StringUtils.isBlank(ncHome)) {
            ncHome = getDialog().getComponent(JTextField.class, "homeText").getText();
        }
        if (StringUtils.isNotBlank(ncHome)) {
            ncHome = new File(ncHome).getParent();
        }
        chooser = new JFileChooser(ncHome);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = chooser.showOpenDialog(getDialog());
        if (JFileChooser.APPROVE_OPTION != ret) {
            return;
        }

        //设置文本框显示
        ncHome = chooser.getSelectedFile().getAbsolutePath();
        getDialog().getComponent(JTextField.class, "homeText").setText(ncHome);

        //根据最新路径重新加载数据源
        DataSourceUtil.initDataSource((DevConfigDialog) getDialog());
    }
}
