package com.yingling.script.dialog;

import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.abs.AbstractDialog;
import com.yingling.script.action.buttion.InitDataExportAction;
import com.yingling.script.studio.connection.ConnectionService;
import com.yingling.script.studio.ui.preference.prop.DataSourceMeta;
import com.yingling.script.studio.ui.preference.prop.ToolUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.Map;

public class InitDataDialog extends AbstractDialog {
    private JPanel contentPane;
    private JTextField dsNameField;
    private JTextField hostField;
    private JTextField userField;
    private JProgressBar progressBar;
    private JButton OKButton;
    private String dsName;
    private VirtualFile itemFile;

    public InitDataDialog(){
        setContentPane(contentPane);
        //获取显示屏尺寸，使界面居中
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setBounds((width - 600) / 2, (height - 200) / 2, 600, 200);

        dsName = ConnectionService.getPoolFacade().getProvider().getSecondaryDataSourceName();
        Map<String, DataSourceMeta> metas = ConnectionService.getPoolFacade().getProvider().getDataSourceMetaMap();
        DataSourceMeta meta = metas.get(dsName);
        String[] jdbc = ToolUtils.getJDBCInfo(meta.getDatabaseUrl());

        hostField.setText(jdbc[0]);
        dsNameField.setText(dsName);
        userField.setText(meta.getUser());

        progressBar.setValue(0);
        // 绘制百分比文本（进度条中间显示的百分数）
        progressBar.setStringPainted(true);
        progressBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Dimension d = progressBar.getSize();
                Rectangle rect = new Rectangle(0, 0, d.width, d.height);
                progressBar.paintImmediately(rect);
            }
        });

        OKButton.addActionListener(new InitDataExportAction(this));
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public String getDsName(){
        return dsName;
    }

    public VirtualFile getItemFile() {
        return itemFile;
    }

    public void setItemFile(VirtualFile itemFile) {
        this.itemFile = itemFile;
    }
}
