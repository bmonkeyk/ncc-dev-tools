package com.yingling.patcher.dialog;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.pub.exception.BusinessException;
import com.yingling.patcher.util.ExportPatcherUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class PatcherDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField savePath;
    private JButton fileChooseBtn;
    private JPanel filePanel;
    private JTextField patcherName;
    private JTextField serverName;
    private AnActionEvent event;
    private JBList fieldList;

    public PatcherDialog(final AnActionEvent event) {
        this.event = event;
        setTitle("export ncc patcher...");

        patcherName.setEditable(true);
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
        fileChooseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userDir = System.getProperty("user.home");
                JFileChooser fileChooser = new JFileChooser(userDir/** + "/Desktop"**/);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int flag = fileChooser.showOpenDialog(null);
                if (flag == JFileChooser.APPROVE_OPTION) {
                    savePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

    }

    private void onOK() {
        // 条件校验
        if (null == patcherName.getText() || "".equals(patcherName.getText())) {
            Messages.showErrorDialog(this, "Please set patcher name!", "Error");
            return;
        }
        if (null == savePath.getText() || "".equals(savePath.getText())) {
            Messages.showErrorDialog(this, "Please Select Save Path!", "Error");
            return;
        }
        ListModel<VirtualFile> model = fieldList.getModel();
        if (model.getSize() == 0) {
            Messages.showErrorDialog(this, "Please Select Export File!", "Error");
            return;
        }

        String exportPath = savePath.getText();
        ExportPatcherUtil util = new ExportPatcherUtil(patcherName.getText(), serverName.getText(), exportPath, event);
        try {
            util.exportPatcher();
            String zipName = util.getZipName();
            if (StringUtils.isBlank(zipName)) {
                zipName = "no files export , please build project , or select src retry !";
            } else {
                zipName = "outFile : " + zipName;
            }
            Messages.showInfoMessage("Success!\n" + zipName, "Tips");
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
            Messages.showErrorDialog(e.getMessage(), "Error");
        } finally {
            util.delete(new File(util.getExportPath()));
        }

        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        VirtualFile[] data = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        fieldList = new JBList(data);
        fieldList.setEmptyText("No File Selected!");
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fieldList);
        filePanel = decorator.createPanel();
    }
}
