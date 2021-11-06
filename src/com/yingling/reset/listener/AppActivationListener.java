package com.yingling.reset.listener;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.wm.IdeFrame;

import com.yingling.reset.common.Resetter;
import com.yingling.reset.helper.BrokenPlugins;
import com.yingling.reset.helper.Constants;
import com.yingling.reset.helper.NotificationHelper;
import com.yingling.reset.helper.ResetTimeHelper;
import org.jetbrains.annotations.NotNull;

public class AppActivationListener implements ApplicationActivationListener {
    private boolean tipped = false;

    public void applicationActivated(@NotNull IdeFrame ideFrame) {
        BrokenPlugins.fix();

        if (tipped || !ResetTimeHelper.overResetPeriod()) {
            return;
        }

        tipped = true;
        AnAction action = ActionManager.getInstance().getAction(Constants.RESET_ACTION_ID);
        NotificationType type = NotificationType.INFORMATION;
        String message = "It has been a long time since the last reset!\nWould you like to reset it again?";
        if (Resetter.isAutoReset()) {
            action = ActionManager.getInstance().getAction(Constants.RESTART_ACTION_ID);
            type = NotificationType.WARNING;
        }

        NotificationHelper.show(null, null, null, message, type, action);
    }

    public void applicationDeactivated(@NotNull IdeFrame ideFrame) {
        applicationActivated(ideFrame);
    }

//    public void delayedApplicationDeactivated(@NotNull IdeFrame ideFrame) {
//
//    }
}
