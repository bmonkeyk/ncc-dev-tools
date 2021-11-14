package com.yingling.module.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.FsRoot;
import com.yingling.abs.AbstractAnAction;
import com.yingling.base.BusinessException;
import com.yingling.base.ProjectManager;
import com.yingling.module.util.ModuleUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * 转化为module
 */
public class ConvertModuleAction extends AbstractAnAction {
    @Override
    public void doAction(AnActionEvent event) {
        VirtualFile[] file = getSelectFileArr(event);
        //暂时这么愚蠢的支持批量
        ModuleUtil util = new ModuleUtil();
        boolean flag = true;
        for (VirtualFile f : file) {
            try {
                util.coverToModule(event.getProject(), f.getPath());
            } catch (BusinessException businessException) {
                Messages.showErrorDialog(businessException.getMessage(), "出错了");
                flag = false;
                break;
            }
        }
        if (flag) {
            Messages.showInfoMessage("转换完成", "成功");
        }

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean flag = true;
        VirtualFile[] selectFileArr = getSelectFileArr(e);
        if (selectFileArr == null || selectFileArr.length == 0) {
            flag = false;
        } else {
            for (VirtualFile virtualFile : selectFileArr) {
                if (virtualFile instanceof FsRoot) {
                    flag = false;
                    break;
                }
                Module module = ProjectManager.getInstance(e.getProject()).getModule(virtualFile.getName());
                flag = module == null && new File(virtualFile.getPath() + File.separator + "META-INF" + File.separator + "module.xml").exists();

            }
        }
        e.getPresentation().setEnabledAndVisible(flag);
    }
}
