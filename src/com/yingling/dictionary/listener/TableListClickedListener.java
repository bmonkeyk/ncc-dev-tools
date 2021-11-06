package com.yingling.dictionary.listener;

import com.intellij.ui.table.JBTable;
import com.yingling.abs.AbstractDialog;
import com.yingling.abs.AbstractMouseListener;
import com.yingling.base.NccEnvSettingService;
import com.yingling.dictionary.util.SearchTableUtil;

import javax.swing.JScrollPane;
import java.awt.event.MouseEvent;

public class TableListClickedListener extends AbstractMouseListener {
    public TableListClickedListener(AbstractDialog dlg) {
        super(dlg);
    }

    @Override
    protected void click(MouseEvent event, AbstractDialog dlg) {
        JBTable tableList = (JBTable) event.getSource();
        int row = tableList.getSelectedRow();
        if (row < 0) {
            return;
        }
        String classId = (String) tableList.getValueAt(row, 0);
        String tableId = (String) tableList.getValueAt(row, 1);

        JBTable table = SearchTableUtil.getInstance(NccEnvSettingService.getInstance().getConnected_data_source()).getTableInfo(classId, tableId);
        dlg.getComponent(JScrollPane.class, "tableInfoScrollPane").setViewportView(table);

    }

}
