package com.yingling.dictionary;

import com.intellij.ui.table.JBTable;
import com.yingling.abs.AbstractDialog;
import com.yingling.base.NccEnvSettingService;
import com.yingling.dictionary.action.SearchKeyListener;
import com.yingling.dictionary.action.SearchTableAction;
import com.yingling.dictionary.listener.DBChangedListener;
import com.yingling.dictionary.listener.TableListClickedListener;
import com.yingling.dictionary.util.SearchTableUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class DataDictionaryDialog extends AbstractDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JTextField searchField;
    private JButton searchBtn;
    private JScrollPane tableListScrollPane;
    private JScrollPane tableInfoScrollPane;
    private JComboBox dbBox;

    public DataDictionaryDialog() {

        initUI();

        dataDirSetListener();

    }

    private void initUI() {
        setContentPane(contentPane);
        setModal(true);

        //获取显示屏尺寸，使界面居中
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setBounds((width - 800) / 2, (height - 600) / 2, 800, 600);

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

        addComponent("tableListScrollPane", tableListScrollPane);
        addComponent("tableInfoScrollPane", tableInfoScrollPane);
        addComponent("searchField", searchField);
        addComponent("dbBox", dbBox);
    }

    private void dataDirSetListener() {

        dbBox.addItemListener(new DBChangedListener(this));
        Set<String> dbSet = SearchTableUtil.getInstance().getDBList();
        String currDSName = NccEnvSettingService.getInstance().getConnected_data_source();
        for (String dsName : dbSet) {
            dbBox.addItem(dsName);
        }
        if (dbSet.contains(currDSName)) {
            dbBox.setSelectedItem(currDSName);
        } else {
            dbBox.setSelectedItem("design");
        }


        //查询按钮监听
        searchBtn.addActionListener(new SearchTableAction(this));
        //回车查询键监听
        searchField.addKeyListener(new SearchKeyListener(this));
        //默认焦点
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                //组件获取焦点只有在窗体打开后才能生效
                searchField.grabFocus();
            }
        });
        //默认表格
        JBTable tableList = SearchTableUtil.getInstance(currDSName).getTableList(null);
        tableList.addMouseListener(new TableListClickedListener(this));
        tableListScrollPane.setViewportView(tableList);

        JBTable tableInfo = SearchTableUtil.getInstance(currDSName).getTableInfo(null, null);
        tableInfoScrollPane.setViewportView(tableInfo);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        DataDictionaryDialog dialog = new DataDictionaryDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
