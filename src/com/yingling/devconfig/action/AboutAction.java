package com.yingling.devconfig.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.yingling.abs.AbstractAnAction;

import java.io.IOException;

public class AboutAction extends AbstractAnAction {
    @Override
    public void doAction(AnActionEvent event) {


        String url = "http://note.youdao.com/noteshare?id=4485948a8170316f3967095b7697bb28";
        java.net.URI uri = java.net.URI.create(url);
        //获取当前系统桌面扩展
        java.awt.Desktop dp = java.awt.Desktop.getDesktop();
        //判断系统桌面是否支持要执行的功能
        if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
            //获取系统默认浏览器打开链接
            try {
                dp.browse(uri);
            } catch (IOException e) {
                e.printStackTrace();
                Messages.showMessageDialog("Error accessing instruction document\nplease browse : " + url, "error", Messages.getInformationIcon());
            }
        }
    }
}
