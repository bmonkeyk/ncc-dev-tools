package com.yingling.devconfig.action.button;

import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;
import com.yingling.devconfig.DevConfigDialog;
import com.yingling.devconfig.util.DataSourceUtil;
import com.yingling.devconfig.util.TableModelUtil;

import java.awt.event.ActionEvent;

/**
 * 数据源保存
 */
public class ApplyAction extends AbstractButtonAction {
    public ApplyAction(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void doAction(ActionEvent event) {
        DevConfigDialog dialog = (DevConfigDialog) getDialog();

        //数据源保存
        if (dialog.getTabIndex() == 0) {
            DataSourceUtil.syncCurrDataSourceValue(dialog);
        }

        //模块选择保存
        if (dialog.getTabIndex() == 1) {
            TableModelUtil.saveModuleConfig(getDialog());
        }
    }
}
