package com.yingling.reset.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.yingling.abs.AbstractAnAction;
import com.yingling.reset.helper.Constants;
import com.yingling.reset.ui.dialog.MainDialog;

public class ResetAction extends AbstractAnAction{//AnAction implements DumbAware {
    public ResetAction() {
//        super(Constants.ACTION_NAME, "Reset my IDE eval information", AllIcons.General.Reset);

//        AnAction optionsGroup = ActionManager.getInstance().getAction("WelcomeScreen.Options");
//        if ((optionsGroup instanceof DefaultActionGroup)) {
//            ((DefaultActionGroup) optionsGroup).add(this);
//        }

//        ListenerConnector.setup();
    }

    @Override
    public void doAction(AnActionEvent event) {
        {
//        Project project = e.getProject();
//
//        NotificationHelper.checkAndExpire(e);
//
//        if (project == null) {
            MainDialog mainDialog = new MainDialog(Constants.ACTION_NAME);
            mainDialog.show();

//            return;
//        }
//
//        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(Constants.ACTION_NAME);
//        if (null == toolWindow) {
//            ToolWindowEP ep = new ToolWindowEP();
//            ep.id = Constants.ACTION_NAME;
//            ep.anchor = ToolWindowAnchor.BOTTOM.toString();
//            ep.icon = "AllIcons.General.Reset";
//            ep.factoryClass = MainToolWindowFactory.class.getName();
//            ep.setPluginDescriptor(PluginHelper.getPluginDescriptor());
//            ToolWindowManagerEx.getInstanceEx(project).initToolWindow(ep);
////            ToolWindowManagerEx.getInstanceEx(project).init
//
//            toolWindow = ToolWindowManager.getInstance(project).getToolWindow(Constants.ACTION_NAME);
//        }
//
//        toolWindow.show(null);
        }
    }
}
