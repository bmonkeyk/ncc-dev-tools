package com.yingling.extensions.component;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.yingling.extensions.service.NccEnvSettingService;
import com.yingling.extensions.util.DataSourceUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NccDevSettingComponent implements SearchableConfigurable {

    private NccDevSettingDlg dlg = null;
    private NccEnvSettingService service = NccEnvSettingService.getInstance();

    @NotNull
    @Override
    public String getId() {
        return "com.yingling.extensions.component.NccDevSettingComponent";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "NC/NCC Setting";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if(getDlg() != null){
            return getDlg().getContentPane();
        }
        return null;
    }

    private NccDevSettingDlg getDlg() {
        if(service == null){
            return null;
        }
        if (dlg == null) {
            dlg = new NccDevSettingDlg();
            dlg.setSize(new Double(dlg.getSize().getWidth()).intValue(), 200);
        }
        return dlg;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        if(service == null){
            return;
        }
        int i = dlg.getNccSetTab().getSelectedIndex();//0是home设置页面，1是数据源设置页面
        if (i == 0) {
            String newPath = getDlg().getHomeText().getText();
            String oldPath = service.getNcHomePath();
            if(StringUtils.isBlank(newPath)){
                newPath = "";
            }
            if(!newPath.equals(oldPath)){
                dlg.setCurrmeta(null);//nc home 变了要清除掉已经测试的数据源和当前默认数据源
                service.setConnected_data_source("");
            }
            service.setNcHomePath(getDlg().getHomeText().getText());
            service.setTablesPath(getDlg().getTableText().getText());
        } else if (i == 1) {
            service.setNcHomePath(getDlg().getHomeText().getText());
            service.setTablesPath(getDlg().getTableText().getText());
            DataSourceUtil.saveDesignDataSourceMeta(getDlg());
        }
    }

    @Override
    public void reset() {
        if(service == null){
            return ;
        }
        getDlg().getHomeText().setText(service.getNcHomePath());
        getDlg().getTableText().setText(service.getTablesPath());
    }
}
