package com.yingling.dictionary.listener;

import com.yingling.abs.AbstractDialog;
import com.yingling.abs.AbstractItemListener;
import com.yingling.base.NccEnvSettingService;
import org.apache.commons.lang.StringUtils;

import javax.swing.JComboBox;
import java.awt.event.ItemEvent;

public class DBChangedListener extends AbstractItemListener {
    public DBChangedListener(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void afterSelect(ItemEvent e) {
        String dsname = (String) getDialog().getComponent(JComboBox.class, "dbBox").getSelectedItem();
        if (StringUtils.isNotBlank(dsname)) {
            NccEnvSettingService.getInstance().setConnected_data_source(dsname);
        }
    }
}
