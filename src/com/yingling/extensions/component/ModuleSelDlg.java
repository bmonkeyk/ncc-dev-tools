package com.yingling.extensions.component;

import com.intellij.execution.RunManager;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.table.JBTable;
import com.pub.util.ProjectManager;
import com.yingling.extensions.service.NccEnvSettingService;
import com.yingling.libraries.util.ModuleFileUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.*;

public class ModuleSelDlg extends JDialog {
    private Project project;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane dataScrollPane;
    private JButton selAllBtn;
    private JButton mustSelBtn;
    private JButton cancelSelBtn;
    private JBTable table;


    public ModuleSelDlg() {
        project = ProjectManager.getInstance().getProject();
        initialization();
        initData();
    }

    private void initialization() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);

        //根据屏幕的尺寸计算窗体尺寸
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int width = screenSize.width / 3;
        int height = screenSize.height / 2;
        setSize(width, height);
        setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);//居中

        /**
         *  按钮监听
         */

        //确认按钮
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        //取消按钮
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        //全选按钮
        selAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSelAll();
            }
        });

        //全消按钮
        cancelSelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancelSel();
            }
        });
        mustSelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMustSel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        //键盘监听esc按就按
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void onMustSel() {
        int rowCount = getTable().getRowCount();
        Set<String> moduleSet = null;
        String mustModuleStr = NccEnvSettingService.getInstance(project).getMust_modules();
        if (StringUtils.isNotBlank(mustModuleStr)) {
            String[] moduleArr = mustModuleStr.split(",");
            moduleSet = new HashSet<>();
            for (String s : moduleArr) {
                moduleSet.add(s);
            }
        } else {
            moduleSet = ModuleFileUtil.getModuleSet();
        }
        for (int i = 0; i < rowCount; i++) {
            String moduleName = getTable().getValueAt(i, 2).toString();
            boolean checkFlag = false;
            if (moduleSet.contains(moduleName)) {
                checkFlag = true;
            }
            getTable().setValueAt(checkFlag, i, 1);
        }
    }

    private void onCancelSel() {
        setAllCheckState(false);
    }

    private void setAllCheckState(boolean checked) {
        int rowCount = getTable().getRowCount();
        for (int i = 0; i < rowCount; i++) {
            getTable().getModel().setValueAt(checked, i, 1);
        }
    }

    private void onSelAll() {
        setAllCheckState(true);
    }

    /**
     * 初始化模块数据
     */
    private void initData() {
        String homePath = NccEnvSettingService.getInstance().getNcHomePath();
        File moduleFile = new File(homePath + File.separator + "modules");
        File[] moduleArr = moduleFile.listFiles();
        if (ArrayUtils.isEmpty(moduleArr)) {
            dispose();
            Messages.showMessageDialog("Please set nchome first", "tips", Messages.getInformationIcon());
            return;
        }
        DefaultTableModel model = new DefaultTableModel(null, new String[]{
                "NO.", "Checked", "moduleName"}) {
            public Class getColumnClass(int c) {
                switch (c) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }

            //第二列不允许编辑
            public boolean isCellEditable(int row, int column) {
                boolean flag = false;
                if (column == 1) {
                    flag = true;
                }
                return flag;
            }
        };

        List<String> moduleList = new ArrayList<>();
        for (int i = 0; i < moduleArr.length; i++) {
            String moduleName = getNCModuleName(moduleArr[i]);
            if (StringUtils.isNotBlank(moduleName)) {
                moduleList.add(moduleArr[i].getName());
            }
        }
        //排序
        Collections.sort(moduleList);
        //构造列表
        String exModulesStr = NccEnvSettingService.getInstance().getEx_modules();
        int i = 1;
        for (String str : moduleList) {
            boolean checked = true;
            if (StringUtils.isNotBlank(exModulesStr) && exModulesStr.contains(str)) {
                checked = false;
            }
            Vector v = new Vector();
            v.add(i);
            v.add(checked);
            v.add(str);
            model.addRow(v);
            i++;
        }

        getTable().setModel(model);
        dataScrollPane.setViewportView(getTable());
        setVisible(true);
    }

    private void onOK() {
        int rowCount = getTable().getRowCount();
        String exModules = "";
        for (int i = 0; i < rowCount; i++) {
            boolean checked = (boolean) getTable().getValueAt(i, 1);
            if (!checked) {
                exModules += "," + getTable().getValueAt(i, 2).toString();
            }
        }
        if (exModules.length() > 1) {
            exModules = exModules.substring(1);
        }
        NccEnvSettingService.getInstance(project).setEx_modules(exModules);
        RunManagerImpl runManager = (RunManagerImpl) RunManager.getInstance(project);
        List<RunConfiguration> configurationsList = runManager.getConfigurationsList(ApplicationConfigurationType.getInstance());
        if (null != configurationsList && !configurationsList.isEmpty()) {
            for (RunConfiguration configuration : configurationsList) {
                ApplicationConfiguration conf = (ApplicationConfiguration) configuration;
                Map<String, String> envs = conf.getEnvs();
                if (envs != null && !envs.keySet().contains("FIELD_CLINET_IP")) {//通过FIELD_CLINET_IP 判断不是client应用
                    envs.put("FIELD_EX_MODULES", exModules);
                }
                conf.setEnvs(envs);
            }
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public JBTable getTable() {
        if (table == null) {
            table = new JBTable();
            table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        }
        return table;
    }

    /**
     * nc 模块名称
     *
     * @param module
     * @return
     */
    private String getNCModuleName(File module) {

        String ncModuleName = null;
        String moduleFilePath = module.getPath() + File.separator + "META-INF" + File.separator + "module.xml";
        try {
            File file = new File(moduleFilePath);
            if (file.exists()) {
                InputStream in = new FileInputStream(file);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(in);
                Element root = doc.getDocumentElement();
                ncModuleName = root.getAttribute("name");
            }
        } catch (Exception e) {
            //抛错就认为不是nc项目
        }
        return ncModuleName;
    }
}
