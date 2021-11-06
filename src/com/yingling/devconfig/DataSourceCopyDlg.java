package com.yingling.devconfig;

import com.intellij.openapi.ui.Messages;
import com.yingling.abs.AbstractDialog;
import com.yingling.devconfig.util.DataSourceUtil;
import com.yingling.script.studio.ui.preference.prop.DataSourceMeta;
import org.apache.commons.lang.StringUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 复制数据源
 */
public class DataSourceCopyDlg extends AbstractDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField newNameText;

    private DevConfigDialog parentDlg;

    public DataSourceCopyDlg() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        //获取显示屏尺寸，使界面居中
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setBounds((width - 600) / 2, (height - 200) / 2, 600, 200);

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

        String newName = newNameText.getText();
        if (StringUtils.isBlank(newName)) {
            Messages.showErrorDialog("数据源名称不能为空！", "出错了");
            return;
        }

        if (parentDlg.getDataSourceMetaMap().keySet().contains(newName)) {
            Messages.showErrorDialog("该数据源名称已存在！请更换一个", "出错了");
            return;
        }
        try {
            DataSourceMeta newMeta = (DataSourceMeta) parentDlg.getCurrMeta().clone();
            newMeta.setBase(false);
            newMeta.setDataSourceName(newName);

            parentDlg.getComponent(JCheckBox.class, "devChx").setSelected("design".equals(newName));
            parentDlg.getComponent(JCheckBox.class, "baseChx").setSelected(false);
            JComboBox box = parentDlg.getComponent(JComboBox.class, "dbBox");
            box.addItem(newName);
            box.setSelectedItem(newName);
            parentDlg.getDataSourceMetaMap().put(newName, newMeta);
            parentDlg.setCurrMeta(newMeta);

        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), "出错了");
        }
        dispose();
        int opt = Messages.showYesNoDialog("复制成功，是否退出设置窗口？", "提示", Messages.getQuestionIcon());
        if (opt == Messages.OK) {
            DataSourceUtil.saveDesignDataSourceMeta(parentDlg);
            parentDlg.dispose();
        }

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();

    }

    public static void main(String[] args) {
        DataSourceCopyDlg dialog = new DataSourceCopyDlg();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public DevConfigDialog getParentDlg() {
        return parentDlg;
    }

    public void setParentDlg(DevConfigDialog parentDlg) {
        this.parentDlg = parentDlg;
    }
}
