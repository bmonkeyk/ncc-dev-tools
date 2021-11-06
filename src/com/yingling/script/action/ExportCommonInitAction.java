package com.yingling.script.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.abs.AbstractAnAction;
import com.yingling.base.BusinessException;
import com.yingling.script.common.tablestruct.model.InitDataCfg;
import com.yingling.script.common.tablestruct.model.MainTableCfg;
import com.yingling.script.common.tablestruct.model.SubTableCfg;
import com.yingling.script.common.tablestruct.model.TableMapping;
import com.yingling.script.common.tablestruct.service.CommonTableStructQueryServiceFactory;
import com.yingling.script.common.tablestruct.util.XStreamParser;
import com.yingling.script.dialog.InitDataDialog;
import com.yingling.script.pub.db.IQueryService;
import com.yingling.script.pub.db.IScriptService;
import com.yingling.script.pub.db.QueryService;
import com.yingling.script.pub.db.ScriptService;
import com.yingling.script.pub.db.model.TableStructure;
import com.yingling.script.pub.db.query.SqlQueryResultSet;
import com.yingling.script.pub.db.script.export.InitDataExportStratege2;
import com.yingling.script.common.InitDataInfo;
import com.yingling.script.studio.connection.ConnectionService;
import com.yingling.script.studio.connection.exception.ConnectionException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * items预制脚本导出
 */
public class ExportCommonInitAction extends AbstractAnAction {



    @Override
    public void doAction(AnActionEvent event) {

        InitDataDialog dialog = new InitDataDialog();
        VirtualFile selectFile = getSelectFile(event);
        if (selectFile.isDirectory()) {
            return;
        }
        if (!"items.xml".equals(selectFile.getName())) {
            Messages.showErrorDialog("please select item.xml", "tips");
            return;
        }
        //Messages.showErrorDialog(project, psiFile.getPath(), "提示", Messages.getInformationIcon());

        String dsname = dialog.getDsName();
        if (StringUtils.isBlank(dsname)) {
            Messages.showErrorDialog("can't find basedata", "tips");
            return;
        }
        dialog.setItemFile(selectFile);
        dialog.setVisible(true);

    }




    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile file = getSelectFile(e);
        boolean flag = file.getPath().contains("script" + File.separator + "conf") && file.getPath().endsWith("items.xml");
        e.getPresentation().setEnabledAndVisible(flag);
    }
}
