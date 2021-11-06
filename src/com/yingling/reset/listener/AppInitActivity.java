package com.yingling.reset.listener;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.progress.ProgressIndicator;
import com.yingling.reset.helper.Constants;
import org.jetbrains.annotations.NotNull;

public class AppInitActivity extends PreloadingActivity {
    public void preload(@NotNull ProgressIndicator indicator) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ActionManager.getInstance().getAction(Constants.RESET_ACTION_ID);
            }
        });
    }
}
