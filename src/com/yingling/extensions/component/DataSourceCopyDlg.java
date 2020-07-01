package com.yingling.extensions.component;

import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.*;

public class DataSourceCopyDlg extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;

    private NccDevSettingDlg parent;

    public DataSourceCopyDlg() {
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
        if (StringUtils.isBlank(textField1.getText())) {
            return;
        }
        try {
            getParent().setDirty(true);
            DataSourceMeta meta = (DataSourceMeta) getParent().getCurrmeta().clone();
            String dsname = textField1.getText();
            meta.setDataSourceName(dsname);
            meta.setBase(false);
            boolean isExist = false;
            for (String item : getParent().getDataSourceMetaMap().keySet()) {
                if (dsname.equals(item)) {
                    getParent().getDataSourceMetaMap().put(meta.getDataSourceName(), meta);
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                getParent().getDataSourceMetaBox().addItem(dsname);
                getParent().getDataSourceMetaMap().put(meta.getDataSourceName(), meta);
                getParent().getDataSourceMetaBox().setSelectedIndex(getParent().getDataSourceMetaBox().getItemCount() - 1);
            }
        } catch (CloneNotSupportedException ex) {
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    @Override
    public NccDevSettingDlg getParent() {
        return parent;
    }

    public void setParent(NccDevSettingDlg parent) {
        this.parent = parent;
    }
}
