package com.yingling.extensions.listener.action;

import com.intellij.ui.table.JBTable;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;
import com.yingling.extensions.listener.tab.TableListClickedListener;
import com.yingling.extensions.util.SearchTableUtil;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;

import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;

/**
 * 数据字典查询按钮
 */
public class SearTableAction extends AbstractButtonAction {
    public SearTableAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        //索引字段
        String searchKey = dlg.getSearchField().getText();
        //当前数据源
        DataSourceMeta meta = dlg.getCurrmeta();
        //查询工具
        SearchTableUtil util = new SearchTableUtil(meta);

        //查询数据
        DefaultTableModel model = util.getTableList(searchKey);

        //构建table
        JBTable table = new JBTable();
        table.setModel(model);
        //隐藏第一列
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.addMouseListener(new TableListClickedListener(dlg));

        //显示数据
        dlg.getTableListScrollPane().setViewportView(table);

    }

}
