package com.yingling.module.dialog;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.pub.exception.BusinessException;
import com.pub.util.ProjectManager;
import com.yingling.util.ConfigureFileUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.text.MessageFormat;

public class NewModuleDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField location;
    private JButton locationFileChooseBtn;
    private JTextField moduleNameField;
    private JTextField ncModuleNameField;
    private AnActionEvent event;
    private String modulePath;

    public NewModuleDialog(final AnActionEvent event) {
        this.event = event;
        if (event.getData(CommonDataKeys.VIRTUAL_FILE) == null) {
            modulePath = event.getProject().getBasePath();
        } else {
            modulePath = event.getData(CommonDataKeys.VIRTUAL_FILE).getPath();
        }

        location.setText(modulePath);
        setTitle("creat new nc module...");

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // 保存路径按钮事件
        locationFileChooseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(modulePath);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int flag = fileChooser.showOpenDialog(null);
                if (flag == JFileChooser.APPROVE_OPTION) {
                    location.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
    }

    private void onOK() {

        String moduleNameText = moduleNameField.getText();
        String ncModuleNameText = ncModuleNameField.getText();
        if (StringUtils.isBlank(moduleNameText)) {
            Messages.showErrorDialog(this, "Please set module name!", "Error");
            return;
        }

        if (StringUtils.isBlank(ncModuleNameText)) {
            Messages.showErrorDialog(this, "Please set NC Module name!", "Error");
            return;
        }
        String locationText = location.getText();
        if (StringUtils.isBlank(locationText)) {
            Messages.showErrorDialog(this, "Please set Module file location !", "Error");
            return;
        }

        Project project = event.getProject();

        try {
            //创建module
            JavaModuleBuilder builder = new JavaModuleType().createModuleBuilder();
            String modulePath = locationText + File.separator + moduleNameText;
            builder.setModuleFilePath(modulePath + File.separator + moduleNameText + ".iml");
            builder.setContentEntryPath(modulePath);
            builder.setName(moduleNameText);
            Module module = builder.commitModule(project, null);

            //输出配置文件
            ConfigureFileUtil util = new ConfigureFileUtil();
            String meatPath = modulePath + File.separator + "META-INF";
            new File(meatPath).mkdirs();
            File file = new File(meatPath + File.separator + "module.xml");
            String template = util.readTemplate("module.xml");
            String content = MessageFormat.format(template, ncModuleNameText);
            util.outFile(file, content, "gb2312", false);

            //设置类路径
            ProjectManager.getInstance().setModuleLibrary(project,module);
        } catch (BusinessException e) {
            e.printStackTrace();
            Messages.showErrorDialog(this, e.getMessage(), "Error");
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
