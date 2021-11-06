package com.yingling.module;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.base.BusinessException;
import com.yingling.base.ConfigureFileUtil;
import com.yingling.base.ProjectManager;
import org.apache.commons.lang.StringUtils;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.MessageFormat;

public class NewComponetDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField displayText;
    private JTextField nameText;
    private AnActionEvent event;

    public NewComponetDialog(final AnActionEvent event) {
        this.event = event;
        Project project = event.getProject();
        setTitle("creat new nc componet...");

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

    }

    private void onOK() {

        String name = nameText.getText();
        if (StringUtils.isBlank(name)) {
            Messages.showErrorDialog(this, "please set componet name!", "Error");
            return;
        }

        String display = displayText.getText();
        if (StringUtils.isBlank(display)) {
            Messages.showErrorDialog(this, "please set componet display!", "Error");
            return;
        }

        if (!name.matches("[a-zA-Z]+")) {
            Messages.showErrorDialog(this, "the name must be using letter only!", "Error");
            return;
        }
        if (!display.matches("[a-zA-Z]+")) {
            Messages.showErrorDialog(this, "the display must be using letter only!", "Error");
            return;
        }
        String modulePath = event.getData(CommonDataKeys.VIRTUAL_FILE).getPath();
        File file = new File(modulePath + File.separator + name);
        if (file.exists()) {
            Messages.showErrorDialog(this, "componet is exists! please replace name !", "Error");
        }
        //创建目录
        String[] dirs = new String[]{"META-INF", "METADATA", "resources", "src/public", "src/private", "src/client", "script/conf", "config"};
        for (String dir : dirs) {
            String path = file.getPath() + File.separator + dir;
            new File(path).mkdirs();
        }

        //创建配置文件
        ConfigureFileUtil util = new ConfigureFileUtil();
        try {
            //创建compinent文件
            String template = util.readTemplate("component.xml");
            String content = MessageFormat.format(template, name, display);
            util.outFile(new File(file.getPath() + File.separator + "component.xml"), content, "utf-8", false);
            //创建manifset文件
            File manifest = new File(modulePath + File.separator + "manifest.xml");

            String newManifest = null;

            if (manifest.exists()) {
                String oldManifest = util.readTemplate(manifest);
                template = util.readTemplate("BusinessComponet.xml");
                content = MessageFormat.format(template, name, display).replace("<Manifest>", "");
                newManifest = oldManifest.replace("</Manifest>", content);
            } else {
                template = util.readTemplate("manifest.xml");
                content = MessageFormat.format(template, name, display);
                newManifest = content;
            }
            util.outFile(manifest, newManifest, "utf-8", false);

            //添加source目录
            Module module = ProjectManager.getInstance().getModule(file.getParentFile().getName());
            ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(module).getModifiableModel();
            ContentEntry contentEntry = modifiableModel.getContentEntries()[0];
            for (String str : dirs) {
                if (str.contains("src")) {
                    VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(FileUtil.toSystemIndependentName(file.getPath() + File.separator + str));
                    contentEntry.addSourceFolder(sourceRoot, false);
                }
            }
            modifiableModel.commit();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
