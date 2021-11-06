package com.yingling.devconfig.action.button.datasource;

import com.intellij.openapi.ui.Messages;
import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;
import com.yingling.devconfig.DevConfigDialog;
import com.yingling.devconfig.util.DataSourceUtil;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;

/**
 * 删除数据源
 */
public class DeleteDataSourceAction extends AbstractButtonAction {
    public DeleteDataSourceAction(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void doAction(ActionEvent event) {
        DevConfigDialog dialog = (DevConfigDialog) getDialog();
        String dsName = dialog.getCurrMeta().getDataSourceName();
        JComboBox box = dialog.getComponent(JComboBox.class, "dbBox");
        int index = box.getSelectedIndex();
        int count = box.getItemCount();

        if (count == 1) {
            Messages.showMessageDialog("当前环境只剩一下一个数据源，请不要删除！", "tips", Messages.getInformationIcon());
            return;
        }

        if (index == count - 1) {
            index = index - 1;
        }
        box.removeItem(dsName);
        box.setSelectedIndex(index);
        dialog.getDataSourceMetaMap().remove(dsName);
        int opt = Messages.showYesNoDialog("删除成功，是否保存并退出设置窗口？", "提示", Messages.getQuestionIcon());
        if (opt == Messages.OK) {
            DataSourceUtil.saveDesignDataSourceMeta(dialog);
            dialog.dispose();
        }
    }
}
