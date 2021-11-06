package com.yingling.debug.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.FsRoot;
import org.jetbrains.annotations.NotNull;

public class AppGroupAction extends DefaultActionGroup {


    @Override
    public void update(@NotNull AnActionEvent e) {


        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        Module module = e.getData(LangDataKeys.MODULE);
        boolean flag = module != null
                && file != null
                && !(file instanceof FsRoot)
                && module.getName().equals(file.getName());

        e.getPresentation().setEnabledAndVisible(flag);

    }
}
