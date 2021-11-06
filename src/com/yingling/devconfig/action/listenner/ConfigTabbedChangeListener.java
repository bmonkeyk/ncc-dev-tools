package com.yingling.devconfig.action.listenner;

import com.yingling.abs.AbstractDialog;
import com.yingling.abs.AbstractTabListener;
import com.yingling.devconfig.DevConfigDialog;
import com.yingling.devconfig.util.DataSourceUtil;
import com.yingling.devconfig.util.TableModelUtil;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;

/**
 * 设置页面切换监听
 */
public class ConfigTabbedChangeListener extends AbstractTabListener {

    public ConfigTabbedChangeListener(AbstractDialog dlg) {
        super(dlg);
    }

    @Override
    protected void afterChange(ChangeEvent event, AbstractDialog dlg) {
        JTabbedPane tabbedPane = dlg.getComponent(JTabbedPane.class, "tabbedPane");
        int index = tabbedPane.getSelectedIndex();

        if (index == 0) {
            initDataSource();
        }

        if (index == 1) {
            initModule();
        }
    }

    private void initModule() {

        DefaultTableModel mustModel = TableModelUtil.getMustModel(getDlg());
        DefaultTableModel selModel = TableModelUtil.getSelModel(getDlg());

        TableModelUtil.modelHandle(getDlg(), mustModel, selModel);

        getDlg().getComponent(JTable.class, "mustTable").setModel(mustModel);
        getDlg().getComponent(JTable.class, "selTable").setModel(selModel);
    }

    @Override
    protected void click(MouseEvent event, AbstractDialog dlg) {
    }

    /**
     * 加载数据源
     */
    private void initDataSource() {
        DataSourceUtil.initDataSource((DevConfigDialog) getDlg());
    }
}
