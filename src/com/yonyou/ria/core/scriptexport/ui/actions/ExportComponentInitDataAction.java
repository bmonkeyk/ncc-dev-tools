package com.yonyou.ria.core.scriptexport.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.pub.exception.BusinessException;
import com.yonyou.common.tablestruct.model.InitDataCfg;
import com.yonyou.common.tablestruct.model.MainTableCfg;
import com.yonyou.common.tablestruct.model.SubTableCfg;
import com.yonyou.common.tablestruct.model.TableMapping;
import com.yonyou.common.tablestruct.service.CommonTableStructQueryServiceFactory;
import com.yonyou.common.tablestruct.util.XStreamParser;
import com.yonyou.uap.studio.connection.ConnectionService;
import com.yonyou.uap.studio.connection.exception.ConnectionException;
import nc.uap.studio.pub.db.IQueryService;
import nc.uap.studio.pub.db.IScriptService;
import nc.uap.studio.pub.db.QueryService;
import nc.uap.studio.pub.db.ScriptService;
import nc.uap.studio.pub.db.model.TableStructure;
import nc.uap.studio.pub.db.query.SqlQueryResultSet;
import nc.uap.studio.pub.db.script.export.InitDataExportStratege2;
import org.apache.commons.lang.StringUtils;



import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.*;

public class ExportComponentInitDataAction extends AnAction {
//    protected static Logger logger = LoggerFactory.getLogger(ExportComponentInitDataAction.class.getName());
    private static final IScriptService service = new ScriptService();
    private static final IQueryService queryService = new QueryService();

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        VirtualFile psiFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (psiFile.isDirectory()) {
            return;
        }
        if (!"items.xml".equals(psiFile.getName())) {
            Messages.showMessageDialog(project, "please select item.xml", "tips", Messages.getInformationIcon());
            return;
        }
        //Messages.showMessageDialog(project, psiFile.getPath(), "提示", Messages.getInformationIcon());

        String dsname = ConnectionService.getBaseDataSourceName();
        if (StringUtils.isBlank(dsname)) {
            Messages.showMessageDialog(project, "can't find basedata", "tips", Messages.getInformationIcon());
            return;
        }
        //Messages.showMessageDialog(project, dsname, "提示", Messages.getInformationIcon());

        //script/conf目录
        VirtualFile confDir = psiFile.getParent();
        VirtualFile initCfgRoot = getChildFile(confDir, "initdata");

        List<InitDataInfo> confs = null;
        try {
            confs = getInitDataInfo(initCfgRoot, psiFile);
        } catch (Exception e3) {
            Messages.showMessageDialog(project, MessageFormat.format("failed analysis tables config :{0}\n{1}", confDir.getPath(), e3.getMessage()), "tips", Messages.getInformationIcon());
            return;
        }
        //script目录
        VirtualFile folder = confDir.getParent();
        Connection con = null;
        try {
            con = ConnectionService.getConnection(dsname);
        } catch (ConnectionException e2) {
//            logger.error(e2.getMessage(), e2);
            Messages.showMessageDialog(project, "connection faild", "tips", Messages.getInformationIcon());
            return;
        }
        Map<String, List<String>> mlTableInfo = null;
        try {
            mlTableInfo = getMLTableInfo(dsname);
        } catch (ConnectionException e1) {
//            logger.error(e1.getMessage(), e1);
            Messages.showMessageDialog(project, "connection faild", "tips", Messages.getInformationIcon());
            return;
        }
        try {
            for (InitDataInfo conf : confs) {
                TableStructure struct = conf.getStruct();
                Map<String, String> tableNoMap = conf.getTableNoMap();
                SqlQueryResultSet rs = null;
                try {
                    rs = queryService.query(conf.getTable(),
                            conf.getWhere(), struct, con);
                } catch (Exception e1) {
//                    logger.error(e1.getMessage(), e1);
                }
                if (rs == null)
                    continue;
                InitDataExportStratege2 strategy = new InitDataExportStratege2(folder, conf.isBusiness(), conf.getMapName(), conf.getStruct(), tableNoMap, "simpchn", mlTableInfo);
                service.export(rs, strategy);
            }
        } catch (RuntimeException e2) {
//            logger.error(e2.getMessage(), e2);
            Messages.showMessageDialog(project, "connection faild", "tips", Messages.getInformationIcon());
            return;
        } finally {
            try {
                con.close();
            } catch (SQLException sQLException) {
            }
        }
    }

    public List<InitDataInfo> getInitDataInfo(VirtualFile cfgRoot, VirtualFile file) throws Exception {
        List<InitDataInfo> cfgs = new ArrayList<InitDataInfo>();
        List<InitDataCfg> configurations = XStreamParser.getInitDataCfgs(file);
        List<TableMapping> mappingCfgs = getMappingCfgs(cfgRoot);
        List<TableMapping> pubMappingCfgs = getPubMappingCfgs();
        List<MainTableCfg> mainTableCfgs = getMainTableCfgs(cfgRoot);
        List<MainTableCfg> pubMainTableCfgs = getPubMainTableCfgs();
        for (InitDataCfg cfg : configurations) {
            String tableName = cfg.getTableName();
            InitDataInfo conf = new InitDataInfo();
            conf.setBusiness(true);
            conf.setTable(tableName);
            conf.setWhere(cfg.getWhereCondition());
            for (TableMapping mapping : pubMappingCfgs) {
                if (mapping.getKey().equalsIgnoreCase(tableName)) {
                    conf.setMapName(mapping.getValue());
                    conf.setBusiness(false);
                    break;
                }
            }
            if (conf.isBusiness())
                for (TableMapping mapping : mappingCfgs) {
                    if (mapping.getKey().equalsIgnoreCase(tableName)) {
                        conf.setMapName(mapping.getValue());
                        break;
                    }
                }
            if (conf.getMapName() == null ||
                    conf.getMapName().trim().length() == 0)
                conf.setMapName(tableName);
            Map<String, String> tableNoMap = new HashMap<String, String>();
            for (MainTableCfg mainCfg : mainTableCfgs) {
                if (mainCfg.getTableName().equalsIgnoreCase(tableName)) {
                    TableStructure struct = getTableStructByMainTableCfg(
                            mainCfg, tableNoMap);
                    conf.setStruct(struct);
                    break;
                }
            }
            if (conf.getStruct() == null)
                for (MainTableCfg mainCfg : pubMainTableCfgs) {
                    if (mainCfg.getTableName().equalsIgnoreCase(tableName)) {
                        TableStructure struct = getTableStructByMainTableCfg(
                                mainCfg, tableNoMap);
                        conf.setStruct(struct);
                        break;
                    }
                }
            if (conf.getStruct() == null) {
                TableStructure struct = new TableStructure();
                struct.setSubTables(new ArrayList());
                struct.setTable(tableName);
                tableNoMap.put(tableName.toLowerCase(), "001");
                conf.setStruct(struct);
            }
            conf.setTableNoMap(tableNoMap);
            cfgs.add(conf);
        }
        return cfgs;
    }

    private List<MainTableCfg> getMainTableCfgs(VirtualFile cfgRoot) {
        VirtualFile subTableCfgsFolder = getChildFile(cfgRoot, "table");
        if (subTableCfgsFolder != null && subTableCfgsFolder.exists()) {
            List<MainTableCfg> list = new ArrayList<>();
            try {
                VirtualFile[] members = subTableCfgsFolder.getChildren();
                byte b;
                for (b = 0; b < members.length; b++) {
                    VirtualFile member = members[b];
                    if (member instanceof VirtualFile) {
                        MainTableCfg mtCfg = XStreamParser.getMainTableCfg(member);
                        if (mtCfg != null)
                            list.add(mtCfg);
                    }
                }
            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
            }
            return list;
        }
        return new ArrayList();
    }

    private List<TableMapping> getMappingCfgs(VirtualFile root) {
        VirtualFile mappingCfgFile = getChildFile(root, "mapping.properties");
        List<TableMapping> moduleMappings = new ArrayList<TableMapping>();
        Properties mappingCfgs = new Properties();
        if (mappingCfgFile != null && mappingCfgFile.exists())
            try {
                InputStream input = mappingCfgFile.getInputStream();
                mappingCfgs.load(input);
                input.close();
                for (Object key : mappingCfgs.keySet()) {
                    TableMapping tm = new TableMapping();
                    tm.setKey((String) key);
                    tm.setValue(mappingCfgs.getProperty((String) key));
                    moduleMappings.add(tm);
                }
            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
            }
        return moduleMappings;
    }

    private List<MainTableCfg> getPubMainTableCfgs() throws BusinessException {
        return CommonTableStructQueryServiceFactory.getService()
                .getCommonMainTableCfgs();
    }

    private List<TableMapping> getPubMappingCfgs() throws BusinessException {
        List<TableMapping> mappings = new ArrayList<TableMapping>();
        Properties pubMappingCfgs =
                CommonTableStructQueryServiceFactory.getService().getCommonMapping();
        if (pubMappingCfgs != null)
            for (Object key : pubMappingCfgs.keySet()) {
                TableMapping tm = new TableMapping();
                tm.setKey((String) key);
                tm.setValue(pubMappingCfgs.getProperty((String) key));
                mappings.add(tm);
            }
        return mappings;
    }

    public static TableStructure getTableStructByMainTableCfg(MainTableCfg mainCfg, Map<String, String> tableNoMap) {
        TableStructure struct = new TableStructure();
        struct.setTable(mainCfg.getTableName());
        struct.setSubTables(new ArrayList());
        if (tableNoMap != null)
            tableNoMap.put(mainCfg.getTableName().toLowerCase(),
                    mainCfg.getSqlNo());
        if (mainCfg.getChildren() == null)
            return struct;
        for (SubTableCfg subTableCfg : mainCfg.getChildren())
            processSubTableStructure(struct, subTableCfg, tableNoMap);
        return struct;
    }

    private static void processSubTableStructure(TableStructure parentStruct, SubTableCfg subTableCfg, Map<String, String> tableNoMap) {
        TableStructure struct = new TableStructure();
        struct.setSubTables(new ArrayList());
        struct.setForeignKey(subTableCfg.getFkColumn());
        struct.setTable(subTableCfg.getTableName());
        if (tableNoMap != null)
            tableNoMap.put(subTableCfg.getTableName().toLowerCase(),
                    subTableCfg.getSqlNo());
        if (subTableCfg.getChildren() != null)
            for (SubTableCfg subCfg : subTableCfg.getChildren())
                processSubTableStructure(struct, subCfg, tableNoMap);
        parentStruct.getSubTables().add(struct);
    }

    public static Map<String, List<String>> getMLTableInfo(String dsName) throws ConnectionException {
        Map<String, List<String>> mlTableMetaInfos = new HashMap<String, List<String>>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "select distinct m.tableid as tableName, c.name as columnName from md_column c inner join md_ormap m on c.id = m.columnid and m.attributeid in (select id from md_property where datatype='BS000010000100001058')  order by m.tableid, c.name";
        try {
            conn = ConnectionService.getConnection(dsName);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String tableName = rs.getString(1);
                String columnName = rs.getString(2);
                List<String> list = (List) mlTableMetaInfos.get(tableName
                        .toLowerCase());
                if (list == null) {
                    list = new ArrayList<String>();
                    mlTableMetaInfos.put(tableName.toLowerCase(), list);
                }
                if (!list.contains(columnName))
                    list.add(columnName);
            }
        } catch (SQLException e) {
//            logger.error(e.getMessage(), e);
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException sQLException) {
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (SQLException sQLException) {
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException sQLException) {
                }
        }
        return mlTableMetaInfos;
    }

    private VirtualFile getChildFile(VirtualFile parent, String fileName) {
        if (parent == null) {
            return null;
        }
        VirtualFile[] children = parent.getChildren();
        if (children == null || children.length == 0) {
            return null;
        }

        for (VirtualFile child : children) {
            if (fileName.equals(child.getName())) {
                return child;
            }
        }
        return null;
    }
}
