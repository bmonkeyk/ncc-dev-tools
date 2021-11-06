package com.yingling.devconfig;

import com.yingling.abs.AbstractDialog;
import com.yingling.base.NccEnvSettingService;
import com.yingling.devconfig.action.button.ApplyAction;
import com.yingling.devconfig.action.button.CancelAction;
import com.yingling.devconfig.action.button.SetLibraryAction;
import com.yingling.devconfig.action.button.datasource.CopyDataSourceAction;
import com.yingling.devconfig.action.button.datasource.DeleteDataSourceAction;
import com.yingling.devconfig.action.button.OKAction;
import com.yingling.devconfig.action.button.SelHomePathAction;
import com.yingling.devconfig.action.button.SelTablePathAction;
import com.yingling.devconfig.action.button.datasource.SetBaseDataSourceAction;
import com.yingling.devconfig.action.button.datasource.SetDevDataSourceAction;
import com.yingling.devconfig.action.button.datasource.TestConnectionAction;
import com.yingling.devconfig.action.button.module.CancelAllAction;
import com.yingling.devconfig.action.button.module.DefaultModuleAction;
import com.yingling.devconfig.action.button.module.SelAllAction;
import com.yingling.devconfig.action.item.DBBoxListener;
import com.yingling.devconfig.action.item.DBTypeBoxListener;
import com.yingling.devconfig.action.item.DriverBoxListener;
import com.yingling.devconfig.action.listenner.ConfigTabbedChangeListener;
import com.yingling.devconfig.util.DataSourceUtil;
import com.yingling.devconfig.util.TableModelUtil;
import com.yingling.script.studio.ui.preference.dbdriver.DatabaseDriverInfo;
import com.yingling.script.studio.ui.preference.dbdriver.DriverInfo;
import com.yingling.script.studio.ui.preference.prop.DataSourceMeta;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 开发配置主界面
 */
public class DevConfigDialog extends AbstractDialog {

    private JPanel contentPane;
    private JButton okBtn;
    private JButton cancelBtn;


    private JTabbedPane tabbedPane;

    private JTextField homeText;
    private JTextField tablesText;
    private JTextField hostText;
    private JTextField portText;
    private JTextField dbNameText;
    private JTextField oidText;
    private JTextField userText;
    private JCheckBox baseChx;
    private JCheckBox devChx;
    private JComboBox dbTypeBox;
    private JComboBox driverBox;
    private JComboBox dbBox;
    private JPasswordField pwdText;


    private JButton homeSelBtn;
    private JButton tableSelBtn;
    private JButton testBtn;
    private JButton setDevBtn;
    private JButton setBaseBtn;
    private JButton copyBtn;
    private JButton delBtn;
    private JPanel dsTab;
    private JPanel moduleTab;
    private JButton applyBtn;
    private JButton defaultBtn;
    private JButton selAllLBtn;
    private JButton cancelAllLBtn;
    private JButton mustBtn;
    private JButton selAllRBtn;
    private JButton cancelRBtn;
    private JTable mustTable;
    private JTable selTable;
    private JButton setLibBtn;

    //数据源相关缓存
    private Map<String, DatabaseDriverInfo> databaseDriverInfoMap = new HashMap<>();
    private Map<String, DataSourceMeta> dataSourceMetaMap = new LinkedHashMap();
    private Map<String, DriverInfo> driverInfoMap = new HashMap();
    //当前数据源
    private DataSourceMeta currMeta;

    public DevConfigDialog() {
        initUI();
        initListener();
        initPath();
    }

    /**
     * 加载home和tables路径
     */
    private void initPath() {
        homeText.setText(NccEnvSettingService.getInstance().getNcHomePath());
        tablesText.setText(NccEnvSettingService.getInstance().getTablesPath());
        DataSourceUtil.initDataSource(this);
    }

    /**
     * 按钮监听初始化
     */
    private void initListener() {

        //页签监听
        tabbedPane.addChangeListener(new ConfigTabbedChangeListener(this));
        tabbedPane.setSelectedIndex(0);


        //面板按钮
        okBtn.addActionListener(new OKAction(this));
        cancelBtn.addActionListener(new CancelAction(this));
        applyBtn.addActionListener(new ApplyAction(this));

        //home路径选择按钮
        homeSelBtn.addActionListener(new SelHomePathAction(this));
        setLibBtn.addActionListener(new SetLibraryAction(this));
        tableSelBtn.addActionListener(new SelTablePathAction(this));

        //数据源相关按钮
        testBtn.addActionListener(new TestConnectionAction(this));
        setDevBtn.addActionListener(new SetDevDataSourceAction(this));
        setBaseBtn.addActionListener(new SetBaseDataSourceAction(this));
        copyBtn.addActionListener(new CopyDataSourceAction(this));
        delBtn.addActionListener(new DeleteDataSourceAction(this));

        //数据源相关下拉
        dbBox.addItemListener(new DBBoxListener(this));
        dbTypeBox.addItemListener(new DBTypeBoxListener(this));
        driverBox.addItemListener(new DriverBoxListener(this));

        defaultBtn.addActionListener(new DefaultModuleAction(this, TableModelUtil.MODULE_TYPE_MUST));
        selAllLBtn.addActionListener(new SelAllAction(this, TableModelUtil.MODULE_TYPE_MUST));
        cancelAllLBtn.addActionListener(new CancelAllAction(this, TableModelUtil.MODULE_TYPE_MUST));

        //模块选择相关按钮
        mustBtn.addActionListener(new DefaultModuleAction(this, TableModelUtil.MODULE_TYPE_SEL));
        selAllRBtn.addActionListener(new SelAllAction(this, TableModelUtil.MODULE_TYPE_SEL));
        cancelRBtn.addActionListener(new CancelAllAction(this, TableModelUtil.MODULE_TYPE_SEL));

    }

    /**
     * 界面初始化
     */
    private void initUI() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okBtn);
//        setSize(new Dimension(800, 600));

        //获取显示屏尺寸，使界面居中
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setBounds((width - 800) / 2, (height - 600) / 2, 800, 600);

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

        //JComponent 集合
        addComponent("homeText", homeText);
        addComponent("tablesText", tablesText);
        addComponent("dbBox", dbBox);
        addComponent("dbTypeBox", dbTypeBox);
        addComponent("driverBox", driverBox);
        addComponent("hostText", hostText);
        addComponent("portText", portText);
        addComponent("dbNameText", dbNameText);
        addComponent("oidText", oidText);
        addComponent("userText", userText);
        addComponent("pwdText", pwdText);
        addComponent("devChx", devChx);
        addComponent("baseChx", baseChx);
        addComponent("okBtn", okBtn);
        addComponent("cancelBtn", cancelBtn);
        addComponent("applyBtn", applyBtn);
        addComponent("tabbedPane", tabbedPane);
        addComponent("dsTab", dsTab);
        addComponent("moduleTab", moduleTab);
        addComponent("homeSelBtn", homeSelBtn);
        addComponent("tableSelBtn", tableSelBtn);
        addComponent("testBtn", testBtn);
        addComponent("setDevBtn", setDevBtn);
        addComponent("setBaseBtn", setBaseBtn);
        addComponent("copyBtn", copyBtn);
        addComponent("delBtn", delBtn);

        addComponent("tabbedPane", tabbedPane);
        addComponent("mustTable", mustTable);
        addComponent("selTable", selTable);
        addComponent("setLibBtn",setLibBtn);

    }

//    private void onOK() {
//        // add your code here
//        dispose();
//    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    public static void main(String[] args) {
        DevConfigDialog dialog = new DevConfigDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public Map<String, DatabaseDriverInfo> getDatabaseDriverInfoMap() {
        return databaseDriverInfoMap;
    }

    public LinkedHashMap<String, DataSourceMeta> getDataSourceMetaMap() {
        return (LinkedHashMap<String, DataSourceMeta>) dataSourceMetaMap;
    }

    public Map<String, DriverInfo> getDriverInfoMap() {
        return driverInfoMap;
    }

    public DataSourceMeta getCurrMeta() {
        return currMeta;
    }

    public void setCurrMeta(DataSourceMeta currMeta) {
        this.currMeta = currMeta;
    }

    public int getTabIndex() {
        return tabbedPane.getSelectedIndex();
    }
}
