package com.yingling.abs;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

public abstract class AbstractAnAction extends AnAction {
    public AbstractAnAction(String s, Object o, Icon moduleDirectory) {
        super(s, String.valueOf(o),moduleDirectory );
    }

    public AbstractAnAction() {

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        doAction(anActionEvent);
    }

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

    /**
     * 获取选中的多个文件
     *
     * @param event
     * @return
     */
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
        Module module = getSelectModule(event);
        File file = new File(selectFile.getPath() + File.separator + "META-INF" + File.separator + "module.xml");
        return file.exists() && module.getModuleFile().getParent().getPath().equals(selectFile.getPath());
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
