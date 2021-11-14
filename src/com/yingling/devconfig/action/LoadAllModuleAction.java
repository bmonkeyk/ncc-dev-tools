package com.yingling.devconfig.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.yingling.abs.AbstractAnAction;
import com.yingling.base.BusinessException;
import com.yingling.base.ProjectManager;
import com.yingling.devconfig.LoadModuleDialog;
import com.yingling.module.util.ModuleUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.SwingUtilities;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 扫描工程目录所有模块
 */
public class LoadAllModuleAction extends AbstractAnAction {
    @Override
    public void doAction(AnActionEvent e) {

        int opt = Messages.showYesNoDialog("是否确认扫描当前project下所有模块？"
                , "询问", Messages.getQuestionIcon());
        if (opt != Messages.OK) {
            return;
        }
        boolean flag = true;
        Project project = e.getProject();
        String projectPath = project.getBasePath();

        //递归获取所有可以创建为模块的目录
        File projectFile = new File(projectPath);
        Set<String> modulePathSet = getModuleDirSet(projectFile);


        LoadModuleDialog dialog = new LoadModuleDialog();
        dialog.setVisible(true);
        dialog.getProgressBar().setValue(0);
        dialog.getProgressBar().setMaximum(modulePathSet.size());


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                doLoad(project, dialog, modulePathSet);
            }
        });
        String msg = dialog.getMsg();

        if (StringUtils.isBlank(msg)) {
            msg = "加载完成";
            Messages.showInfoMessage(msg, "成功");
        } else {
            Messages.showErrorDialog(msg, "出错了");
        }
        dialog.dispose();

    }

    private void doLoad(Project project, LoadModuleDialog dialog, Set<String> modulePathSet) {

        String msg = "";
        int index = 1;
        ModuleUtil util = new ModuleUtil();
        for (String path : modulePathSet) {
            try {
                util.coverToModule(project, path);
                dialog.getProgressBar().setValue(index);
                index++;
            } catch (BusinessException businessException) {
                msg = businessException.getMessage();
                dialog.setMsg(msg);
                break;
            }
        }
    }

    private Set<String> getModuleDirSet(File projectFile) {
        Set<String> modulePathSet = new HashSet();
        for (File file : projectFile.listFiles()) {
            if (file.isFile()) {
                continue;
            }
            scanModule(file, modulePathSet);
        }
        return modulePathSet;
    }

    private void scanModule(File file, Set<String> modulePathSet) {
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                continue;
            }
            int moduleType = isModuleDir(f);
            if (moduleType == ModuleUtil.MODULE_TYPE_NC /**|| moduleType == ModuleUtil.MODULE_TYPE_MAVEN**/) {
                //当前路径应该是 hrkq/MATE-INF,因此,是上级目录可以作为模块目录
                modulePathSet.add(f.getParent());
                //判断上级是否需要加入
                Module module = ProjectManager.getInstance().getModule(f.getParentFile().getParentFile().getName());
                if (module == null) {
                    modulePathSet.add(f.getParentFile().getPath());
                }
            }
            scanModule(f, modulePathSet);
        }
    }

    //判断是否是模块目录
    private int isModuleDir(File f) {

        String ncModulePath = f.getPath() + File.separator + "module.xml";
        String mavenModulePath = f.getPath() + File.separator + "pom.xml";

        File ncModuleFile = new File(ncModulePath);
        File mavenModuleFile = new File(mavenModulePath);

        if (ncModuleFile.exists()) {
            Module module = ProjectManager.getInstance().getModule(f.getName());
            if (module == null) {
                return ModuleUtil.MODULE_TYPE_NC;
            }
        } else if (mavenModuleFile.exists()) {
            Module module = ProjectManager.getInstance().getModule(f.getName());
            if (module == null) {
                return ModuleUtil.MODULE_TYPE_MAVEN;
            }
        }
        return ModuleUtil.MODULE_TYPE_JAVA;
    }
}
