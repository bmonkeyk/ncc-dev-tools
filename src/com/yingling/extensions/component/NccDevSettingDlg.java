package com.yingling.extensions.component;


import com.intellij.ui.table.JBTable;
import com.yingling.extensions.listener.action.*;
import com.yingling.extensions.listener.item.DataSourceMetaBoxListener;
import com.yingling.extensions.listener.item.DatabaseDriverInfoBoxListener;
import com.yingling.extensions.listener.item.DriverInfoBoxListener;
import com.yingling.extensions.listener.mouse.SearFieldMouseListener;
import com.yingling.extensions.listener.tab.AboutTabbedChangeListener;
import com.yingling.extensions.listener.tab.NccSetTabbedChangeListener;
import com.yingling.extensions.service.NccEnvSettingService;
import com.yingling.extensions.util.DataSourceUtil;
import com.yingling.extensions.util.SearchTableUtil;
import nc.uap.plugin.studio.ui.preference.dbdriver.DatabaseDriverInfo;
import nc.uap.plugin.studio.ui.preference.dbdriver.DriverInfo;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.event.*;
import java.util.HashMap;
import java.util.regex.Pattern;

public class NccDevSettingDlg extends JDialog {

    private JPanel contentPane;

    private JTabbedPane nccSetTab;

    //设置nc home 组件
    private JPanel homePanel;
    private JTextField homeText;
    private JTextField tableText;
    private JButton homeSelBtn;
    private JButton tableSelBtn;
//    private JButton retrenchBtn;
    private JButton moduleSelBtn;
    private JButton setMustAction;

    //mac 设置备份module
    private JButton setLibBtn;


    //设置数据源 组件
    private JPanel dataSourcePanel;
    private JComboBox dataSourceMetaBox;
    private JButton testConBtn;
    private JButton setDevBtn;
    private JButton setBaseBtn;
    private JButton copyBtn;
    private JButton delBtn;
    private JComboBox driverInfoBox;
    private JComboBox databaseDriverInfoBox;
    private JTextField hostText;
    private JTextField portText;
    private JTextField dbNameText;
    private JTextField oidText;
    private JTextField userNameTest;
    private JPasswordField pwdText;
    private JCheckBox baseCheck;
    private JCheckBox devCheck;
    private boolean dirty = false;
    private boolean switchFlag = false;

    private String connHost;
    private String connPort;
    private String connName;
    private String connOID;
    private String connUser;
    private String connPasswd;
    private HashMap<String, DatabaseDriverInfo> databaseDriverInfoMap = new HashMap<>();
    private HashMap<String, DataSourceMeta> dataSourceMetaMap = new HashMap<>();
    private HashMap<String, DriverInfo> driverInfoMap = new HashMap<>();
    //当前数据源
    private DataSourceMeta currmeta;

    //关于 组件
    private JTabbedPane aboutTab;

    //数据字典组件
    private JPanel dataDicPanel;
    private JTextField searchField;
    private JButton searchBtn;
    private JScrollPane tableListScrollPane;
    private JScrollPane tableInfoScrollPane;
    private JLabel mdInfoLabel;



    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    public NccDevSettingDlg() {

        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });


        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        oidText.setDocument(new NccDevSettingDlg.DSDocument("[YZ]?|[YZ][A-Z0-9]", 2));
        portText.setDocument(new NccDevSettingDlg.DSDocument("[0-9]*"));

        //页签切换
        nccSetTab.addChangeListener(new NccSetTabbedChangeListener(this));

        NccEnvSettingService service = NccEnvSettingService.getInstance();
        if(service == null){
            return ;
        }

        //初始化设置参数和监听
        homeSetListener();

        dataSourceSetListener();

        dataDirSetListener();

        //设置关于页面
        aboutSetListener();

    }


    /**
     * nc home 设置面板
     */
    private void homeSetListener() {

        NccEnvSettingService service = NccEnvSettingService.getInstance();
        homeText.setText(service.getNcHomePath());
        tableText.setText(service.getTablesPath());

        homeSelBtn.addActionListener(new HomeSelAction(this));
        tableSelBtn.addActionListener(new TableSelAction(this));
//        retrenchBtn.addActionListener(new RetrenchAction(this));
        moduleSelBtn.addActionListener(new ModuleSelAction(this));
        setLibBtn.addActionListener(new LibrarySetAction(this));
        setMustAction.addActionListener(new SetMustAction(this));
    }

    /**
     * 数据源设置面板
     */
    private void dataSourceSetListener() {

        //测试按钮添加监听
        testConBtn.addActionListener(new TestConnectAction(this));
        //设为开发库添加监听
        setDevBtn.addActionListener(new SetDevDataBaseAction(this));
        //设为基准库添加监听
        setBaseBtn.addActionListener(new SetBaseDataBaseAction(this));
        //复制数据源
        copyBtn.addActionListener(new CopyDataBaseAction(this));
        //删除数据源监听
        delBtn.addActionListener(new DelDataBaseAction(this));


        //数据源列表下拉监听
        dataSourceMetaBox.addItemListener(new DataSourceMetaBoxListener(this));
        //数据库下拉类型监听
        databaseDriverInfoBox.addItemListener(new DatabaseDriverInfoBoxListener(this));
        //驱动类型下拉监听
        driverInfoBox.addItemListener(new DriverInfoBoxListener(this));

        //判断是否设置了nc home 再初始化数据源
        if (StringUtils.isBlank(getHomeText().getText())
                && StringUtils.isBlank(NccEnvSettingService.getInstance().getNcHomePath())) {
            return;
        }
        DataSourceUtil.initDataSourceComposite(this);
    }

    private void dataDirSetListener() {
        JBTable tableList = new JBTable();
        tableList.setModel(new SearchTableUtil().getTableList(null));
        //隐藏第一列
        tableList.getColumnModel().getColumn(0).setMinWidth(0);
        tableList.getColumnModel().getColumn(0).setMaxWidth(0);
        tableListScrollPane.setViewportView(tableList);

        JBTable tableInfo = new JBTable();
        tableInfo.setModel(new SearchTableUtil().getTableInfo(null, null));
        //隐藏第6、8两列 datatype,classtype
        tableInfo.getColumnModel().getColumn(8).setMinWidth(0);
        tableInfo.getColumnModel().getColumn(8).setMaxWidth(0);
        tableInfo.getColumnModel().getColumn(6).setMinWidth(0);
        tableInfo.getColumnModel().getColumn(6).setMaxWidth(0);
        tableInfoScrollPane.setViewportView(tableInfo);
        searchBtn.addActionListener(new SearTableAction(this));

        searchField.addMouseListener(new SearFieldMouseListener(this));
    }

    private void aboutSetListener() {
        aboutTab.addChangeListener(new AboutTabbedChangeListener(this));
        aboutTab.addMouseListener(new AboutTabbedChangeListener(this));
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public void setSearchField(JTextField searchField) {
        this.searchField = searchField;
    }

    public JScrollPane getTableListScrollPane() {
        return tableListScrollPane;
    }

    public void setTableListScrollPane(JScrollPane tableListScrollPane) {
        this.tableListScrollPane = tableListScrollPane;
    }

    public JScrollPane getTableInfoScrollPane() {
        return tableInfoScrollPane;
    }

    public void setTableInfoScrollPane(JScrollPane tableInfoScrollPane) {
        this.tableInfoScrollPane = tableInfoScrollPane;
    }

    public JLabel getMdInfoLabel() {
        return mdInfoLabel;
    }

    public void setMdInfoLabel(JLabel mdInfoLabel) {
        this.mdInfoLabel = mdInfoLabel;
    }


    /***********************
     *
     * 用到的一些正则表达式
     *
     * *********************/
    class DSDocument extends PlainDocument {
        private int maxLength = -1;//-1 不限制
        private String regex;

        public DSDocument(String regex, int maxLength) {
            this.regex = regex;
            this.maxLength = maxLength;
        }

        public DSDocument(String regex) {
            this.regex = regex;
        }

        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            try {
                if (str == null)
                    return;
                if (maxLength == -1 || (this.getLength() + str.length()) <= maxLength) //限制字符长度
                {
                    String allstr = this.getText(0, this.getLength()) + str;
                    if (Pattern.compile(regex).matcher(allstr).matches()) //限制字符长度
                    {
                        super.insertString(offset, str, a);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class OIDDocument extends PlainDocument {


        public OIDDocument() {
        }

        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            try {
                if (str == null)
                    return;
                if ((this.getLength() + str.length()) <= 2) //限制字符长度
                {
                    String allstr = this.getText(0, this.getLength()) + str;
                    if ((Pattern.compile("[YZ]?|[YZ][A-Z0-9]")).matcher(allstr).matches()) //限制字符长度
                    {
                        super.insertString(offset, str, a);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class PortDocument extends PlainDocument {

        public PortDocument() {
        }

        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            try {
                if (str == null)
                    return;
                if ((Pattern.compile("[0-9]*")).matcher(str).matches()) //限制字符长度
                {
                    super.insertString(offset, str, a);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<String, DatabaseDriverInfo> getDatabaseDriverInfoMap() {
        return databaseDriverInfoMap;
    }

    public HashMap<String, DataSourceMeta> getDataSourceMetaMap() {
        return dataSourceMetaMap;
    }

    public HashMap<String, DriverInfo> getDriverInfoMap() {
        return driverInfoMap;
    }

    public JPanel getHomePanel() {
        return homePanel;
    }

    public void setCurrmeta(DataSourceMeta currmeta) {
        this.currmeta = currmeta;
    }

    public DataSourceMeta getCurrmeta() {
        return currmeta;
    }

    public JTextField getHomeText() {
        return homeText;
    }

    public JTextField getTableText() {
        return tableText;
    }

    public JButton getHomeSelBtn() {
        return homeSelBtn;
    }

    public JButton getTableSelBtn() {
        return tableSelBtn;
    }

//    public JButton getRetrenchBtn() {
//        return retrenchBtn;
//    }

    public JButton getModuleSelBtn() {
        return moduleSelBtn;
    }

    public JButton getSetLibBtn() {
        return setLibBtn;
    }

    public JPanel getDataSourcePanel() {
        return dataSourcePanel;
    }

    public JComboBox getDataSourceMetaBox() {
        return dataSourceMetaBox;
    }

    public JButton getTestConBtn() {
        return testConBtn;
    }

    public JButton getSetDevBtn() {
        return setDevBtn;
    }

    public JButton getSetBaseBtn() {
        return setBaseBtn;
    }

    public JButton getCopyBtn() {
        return copyBtn;
    }

    public JButton getDelBtn() {
        return delBtn;
    }

    public JComboBox getDriverInfoBox() {
        return driverInfoBox;
    }

    public JComboBox getDatabaseDriverInfoBox() {
        return databaseDriverInfoBox;
    }

    public JTextField getHostText() {
        return hostText;
    }

    public JTextField getPortText() {
        return portText;
    }

    public JTextField getDbNameText() {
        return dbNameText;
    }

    public JTextField getOidText() {
        return oidText;
    }

    public JTextField getUserNameTest() {
        return userNameTest;
    }

    public JPasswordField getPwdText() {
        return pwdText;
    }

    public JCheckBox getBaseCheck() {
        return baseCheck;
    }

    public JCheckBox getDevCheck() {
        return devCheck;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isSwitchFlag() {
        return switchFlag;
    }

    public void setSwitchFlag(boolean switchFlag) {
        this.switchFlag = switchFlag;
    }

    public String getConnHost() {
        return connHost;
    }

    public void setConnHost(String connHost) {
        this.connHost = connHost;
    }

    public String getConnPort() {
        return connPort;
    }

    public void setConnPort(String connPort) {
        this.connPort = connPort;
    }

    public String getConnName() {
        return connName;
    }

    public void setConnName(String connName) {
        this.connName = connName;
    }

    public String getConnOID() {
        return connOID;
    }

    public void setConnOID(String connOID) {
        this.connOID = connOID;
    }

    public String getConnUser() {
        return connUser;
    }

    public void setConnUser(String connUser) {
        this.connUser = connUser;
    }

    public String getConnPasswd() {
        return connPasswd;
    }

    public void setConnPasswd(String connPasswd) {
        this.connPasswd = connPasswd;
    }

    public JTabbedPane getNccSetTab() {
        return nccSetTab;
    }

    public JTabbedPane getAboutTab() {
        return aboutTab;
    }

}
