package com.yingling.extensions.listener.tab;

import com.intellij.ui.table.JBTable;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractMouseListener;
import com.yingling.extensions.util.SearchTableUtil;

import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;

/**
 * 数据字典表列表鼠标点击监听
 */
public class TableListClickedListener extends AbstractMouseListener {
    public TableListClickedListener(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void click(MouseEvent event, NccDevSettingDlg dlg) {
        JBTable tableList = (JBTable) event.getSource();
        int row = tableList.getSelectedRow();
        if (row < 0) {
            return;
        }
        String classId = (String) tableList.getValueAt(row, 0);
        String tableId = (String) tableList.getValueAt(row, 1);

        SearchTableUtil util = new SearchTableUtil(dlg.getCurrmeta());
        DefaultTableModel model = util.getTableInfo(classId, tableId);
        JBTable table = new JBTable();
        table.setModel(model);

        //隐藏第6、8两列 datatype,classtype
        table.getColumnModel().getColumn(8).setMinWidth(0);
        table.getColumnModel().getColumn(8).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        dlg.getTableInfoScrollPane().setViewportView(table);

        dlg.getMdInfoLabel().setText(util.getMdInfo(classId));
    }
}
