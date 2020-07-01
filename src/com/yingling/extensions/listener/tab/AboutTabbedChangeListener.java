package com.yingling.extensions.listener.tab;

import com.intellij.openapi.ui.Messages;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractTabListener;

import javax.swing.event.ChangeEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * 关于页面监听
 */
public class AboutTabbedChangeListener extends AbstractTabListener {
    public AboutTabbedChangeListener(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void afterChange(ChangeEvent event, NccDevSettingDlg dlg) {
        openDictionaries(dlg);
    }

    @Override
    protected void click(MouseEvent event, NccDevSettingDlg dlg) {
        openDictionaries(dlg);
    }

    private void openDictionaries(NccDevSettingDlg dlg) {

        if (dlg.getNccSetTab().getSelectedIndex() != dlg.getNccSetTab().getTabCount()-1) {
            return;
        }
        int index = dlg.getAboutTab().getSelectedIndex();
        //调用有道笔记来显示"使用说明"和"开发指南"
        String url = "";
        switch (index) {
            case 0:
                url = "http://note.youdao.com/noteshare?id=4485948a8170316f3967095b7697bb28";
                break;
            case 1:
                url = "http://note.youdao.com/noteshare?id=577b88e523ac2684846f4c5d1c557620";
                break;
            default:

        }
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
