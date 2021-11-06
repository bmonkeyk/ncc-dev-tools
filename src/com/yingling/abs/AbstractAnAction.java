package com.yingling.abs;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.base.ProjectManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * idea按钮抽象类
 */
public abstract class AbstractAnAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        ProjectManager.getInstance().setProject(event.getProject());
        doAction(event);
    }

    /**
     * 子类实现
     *
     * @param event
     */
    public abstract void doAction(AnActionEvent event);

    /**
     * 获取选中文件
     *
     * @param event
     * @return
     */
    public VirtualFile getSelectFile(AnActionEvent event) {
        return event.getData(CommonDataKeys.VIRTUAL_FILE);
    }

    public VirtualFile[] getSelectFileArr(AnActionEvent event) {
        return event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
    }

    /**
     * 是否nc module 根目录
     *
     * @param event
     * @return
     */
    public boolean isNCModule(AnActionEvent event) {
        VirtualFile selectFile = getSelectFile(event);
        if (selectFile == null) {
            return false;
        }
        Module module = ProjectManager.getInstance().getModule(selectFile.getName());
        if (module == null) {
            return false;
        }


        return StringUtils.isBlank(module.getName()) ? false : module.getName().equals(selectFile.getName());
    }

    /**
     * 获取选中模块
     *
     * @param event
     * @return
     */
    public Module getSelectModule(AnActionEvent event) {
        return event.getData(LangDataKeys.MODULE);
    }
}
