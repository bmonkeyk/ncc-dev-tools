package com.yingling.extensions.component;

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

public class MustModuleDlg extends JDialog {
    private Project project;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane dataScrollPane;
    private JButton selAllBtn;
    private JButton cancelSelBtn;
    private JButton mustSelBtn;
    private JBTable table;


    public MustModuleDlg() {
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

        //全选
        selAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSelAll();
            }
        });

        mustSelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSelDefault();
            }
        });
        //全消
        cancelSelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCancelAll();
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

    private void doSelDefault() {
        int rowCount = getTable().getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String name = (String) getTable().getModel().getValueAt(i, 2);
            boolean checked = false;
            if (ModuleFileUtil.getModuleSet().contains(name)) {
                checked = true;
            }
            getTable().getModel().setValueAt(checked, i, 1);
        }
    }

    private void doCancelAll() {
        setAllCheckState(false);
    }

    private void doSelAll() {
        setAllCheckState(true);
    }

    private void setAllCheckState(boolean checked) {
        int rowCount = getTable().getRowCount();
        for (int i = 0; i < rowCount; i++) {
            getTable().getModel().setValueAt(checked, i, 1);
        }
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
            Messages.showMessageDialog("Please check nchome first", "tips", Messages.getInformationIcon());
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
                Object obj = getTable().getValueAt(row,2);
                if (column == 1 && !ModuleFileUtil.getModuleSet().contains(obj)) {
                    flag = true;
                }
                return flag;
            }
        };

        List<String> moduleList = new ArrayList<>();
        for (int i = 0; i < moduleArr.length; i++) {
            String moduleName = getNCModuleName(moduleArr[i]);
            if (StringUtils.isNotBlank(moduleName)) {//判定是nc模块
                moduleList.add(moduleArr[i].getName());
            }
        }
        //排序
        Collections.sort(moduleList);
        //构造列表
        String mustModules = NccEnvSettingService.getInstance().getMust_modules();
        Set<String> moduleSet = null;
        if (StringUtils.isNotBlank(mustModules)) {
            moduleSet = new HashSet<>();
            String[] ss = mustModules.split(",");
            for (String s : ss) {
                moduleSet.add(s);
            }
        } else {
            moduleSet = ModuleFileUtil.getModuleSet();
        }
        int i = 1;
        for (String str : moduleList) {
            boolean checked = false;
            if (moduleSet.contains(str)) {
                checked = true;
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
        String mustModuleStr = "";
        for (int i = 0; i < rowCount; i++) {
            boolean checkFlag = (boolean) getTable().getValueAt(i, 1);
            if (checkFlag) {
                String name = getTable().getValueAt(i, 2).toString();
                mustModuleStr += "," + name;
            }
        }
        if (mustModuleStr.length() > 1) {
            mustModuleStr = mustModuleStr.substring(1);
        }
        NccEnvSettingService.getInstance(project).setMust_modules(mustModuleStr);
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
