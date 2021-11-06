package com.yingling.reset.listener;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.yingling.reset.common.Resetter;
import com.yingling.reset.helper.Constants;
import com.yingling.reset.helper.NotificationHelper;
import com.yingling.reset.helper.PluginHelper;

import com.yingling.reset.helper.ResetTimeHelper;
import com.yingling.reset.tw.MainToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class PluginListener implements DynamicPluginListener {
    @Override
    public void pluginLoaded(@NotNull IdeaPluginDescriptor descriptor) {
        if (!PluginHelper.myself(descriptor)) {
            return;
        }

        ActionManager.getInstance().getAction(Constants.RESET_ACTION_ID);

        String link = "https://zhile.io/2020/11/18/jetbrains-eval-reset-da33a93d.html";
        String versionTip = "Plugin version: <b>v" + descriptor.getVersion() + "</b>";
        String autoResetTip = "Auto reset option state: " + (Resetter.isAutoReset() ? "<b>on</b>" : "<b>off</b>");
        String autoLogoutTip = "Auto logout option state: " + (Resetter.isAutoLogout() ? "<b>on</b>" : "<b>off</b>");
        String lastResetTime = "Last reset time: <b>" + ResetTimeHelper.getLastResetTimeStr() + "</b>";
        String content = String.format("Plugin installed successfully!<br>For more information, <a href='%s'>visit here</a>.<br><br>%s<br>%s<br>%s<br>%s", link, versionTip, autoResetTip, autoLogoutTip, lastResetTime);
        NotificationHelper.showInfo(null, content);
    }

    @Override
    public void beforePluginUnload(@NotNull IdeaPluginDescriptor descriptor, boolean isUpdate) {
        if (!PluginHelper.myself(descriptor)) {
            return;
        }

        AnAction optionsGroup = ActionManager.getInstance().getAction("WelcomeScreen.Options");
        if ((optionsGroup instanceof DefaultActionGroup)) {
            ((DefaultActionGroup) optionsGroup).remove(ActionManager.getInstance().getAction(Constants.RESET_ACTION_ID));
        }

        ListenerConnector.dispose();
        MainToolWindowFactory.unregisterAll();
    }
}
