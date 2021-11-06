package com.yingling.reset.ui.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.yingling.reset.ui.form.MainForm;

import javax.swing.JComponent;

public class MainDialog extends DialogWrapper {
    public MainDialog(String title) {
        super(true);
        init();
        setTitle(title);
    }

    @Override
    protected JComponent createCenterPanel() {
        MainForm mainForm = new MainForm(getDisposable(), this);

        return mainForm.getContent(getDisposable());
    }

    @Override
    protected JComponent createSouthPanel() {
        return null;
    }
}
