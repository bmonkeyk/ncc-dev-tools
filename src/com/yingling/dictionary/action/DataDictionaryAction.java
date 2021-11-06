package com.yingling.dictionary.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.yingling.abs.AbstractAnAction;
import com.yingling.dictionary.DataDictionaryDialog;

/**
 * 数据字典
 */
public class DataDictionaryAction extends AbstractAnAction {
    @Override
    public void doAction(AnActionEvent event) {
        DataDictionaryDialog dialog = new DataDictionaryDialog();
        dialog.setVisible(true);
    }
}
