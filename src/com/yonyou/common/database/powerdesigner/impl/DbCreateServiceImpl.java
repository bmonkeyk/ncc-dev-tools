package com.yonyou.common.database.powerdesigner.impl;

import com.intellij.openapi.vfs.VirtualFile;
import com.yonyou.common.database.powerdesigner.core.Pdm;
import com.yonyou.common.database.powerdesigner.exception.PDMParseRuntimeException;
import com.yonyou.common.database.powerdesigner.itf.IDbCreateService;
import com.yonyou.common.database.powerdesigner.itf.IDdlGenerator;
import com.yonyou.common.database.powerdesigner.util.PdmUtil;
import nc.uap.studio.pub.db.model.IColumn;
import nc.uap.studio.pub.db.model.ITable;
import nc.uap.studio.pub.db.model.impl.Column;
import nc.uap.studio.pub.db.model.impl.Table;
import org.apache.commons.io.IOUtils;



import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DbCreateServiceImpl implements IDbCreateService {
//    protected static Logger logger = LoggerFactory.getLogger(DbCreateServiceImpl.class.getName());
    private static final int DDL_TYPE_TABLE = 1;

    private static final int DDL_TYPE_INDEX_AND_REFERENCE = 2;

    private static final int DDL_TYPE_VIEW = 3;

    private static final String SQL_FILE_ENCODING = "gb2312";

    private static final int MAX_PK_NAME_LENGTH_SQLSERVER = 30;

    private static final int MAX_PK_NAME_LENGTH_DB2 = 18;

    private static final int MAX_PK_NAME_LENGTH_ORACLE = 30;

    public void validatePdm(VirtualFile pdmFile, boolean parseReference) {
        try {
            PdmUtil.validatePdm(pdmFile);
            Pdm pdm = PdmUtil.parsePdm(pdmFile, parseReference);
            String pdmFileName = pdmFile.getName();
            int index = -1;
            if ((index = pdmFileName.lastIndexOf(".")) != -1)
                pdmFileName = pdmFileName.substring(0, index).toLowerCase();
            pdm.setPdmName(pdmFileName);
            int origTableSize = pdm.getTables().size();
            checkPkConstaintNameLength(pdm);
            if (pdm.getTables().size() != origTableSize)
                throw new PDMParseRuntimeException("Constraint name of primary key is overlength.");
        } catch (PDMParseRuntimeException e) {
            throw e;
        }
    }

    public void geneSqlFile(VirtualFile pdmFile, boolean geneReference, VirtualFile sqlRoot) {
        List<DatabaseType> dbTypes = new ArrayList<DatabaseType>();
        dbTypes.add(DatabaseType.SQLSERVER);
        dbTypes.add(DatabaseType.ORACLE);
        dbTypes.add(DatabaseType.DB2);
        geneSqlFileInner(pdmFile, geneReference, sqlRoot, dbTypes);
    }

    public void geneSqlFile(VirtualFile pdmFile, boolean geneReference, VirtualFile sqlRoot, DatabaseType dbType) {
        List<DatabaseType> dbTypes = new ArrayList<DatabaseType>();
        dbTypes.add(dbType);
        geneSqlFileInner(pdmFile, geneReference, sqlRoot, dbTypes);
    }

    public void geneDataDictionary(VirtualFile pdmFile, boolean geneReference, VirtualFile ddRoot) {
    }

    private void geneSqlFileInner(VirtualFile pdmFile, boolean geneReference, VirtualFile sqlRoot, Collection<DatabaseType> dbTypes) {
        try {
            PdmUtil.validatePdm(pdmFile);
            Pdm pdm = PdmUtil.parsePdm(pdmFile, geneReference);
            String pdmFileName = pdmFile.getName();
            int index = -1;
            if ((index = pdmFileName.lastIndexOf(".")) != -1)
                pdmFileName = pdmFileName.substring(0, index).toLowerCase();
            pdm.setPdmName(pdmFileName);
            for (DatabaseType dbType : dbTypes)
                checkPkConstaintNameLength(pdm.getTables(), dbType,
                        pdm.getPdmName());
            for (DatabaseType dbType : dbTypes)
                geneSqlFileInner(pdm, sqlRoot, dbType);
        } catch (PDMParseRuntimeException e) {
            throw e;
        }
    }

    private void geneSqlFileInner(Pdm pdm, VirtualFile sqlRoot, DatabaseType dbType) {
        pdm = getCvtedPdm(pdm, dbType);
        File tableSqlFile = getSqlFile(pdm.getPdmName(), sqlRoot, dbType,
                1);
        File indexAndRefSqlFile = getSqlFile(
                pdm.getPdmName(), sqlRoot, dbType, 2);
        File viewSqlFile = getSqlFile(
                pdm.getPdmName(), sqlRoot, dbType, 3);
        if (pdm.getTables().isEmpty()) {
            tableSqlFile.delete();
        } else if (!tableSqlFile.getParentFile().exists() &&
                !tableSqlFile.getParentFile().mkdirs()) {
            String msg = MessageFormat.format("Create directory {0} failed.", new Object[]{tableSqlFile
                    .getParentFile().getAbsolutePath()});
            throw new PDMParseRuntimeException(msg);
        }
        if (pdm.getIndexs().isEmpty() && pdm.getFkConstraints().isEmpty()) {
            indexAndRefSqlFile.delete();
        } else if (!indexAndRefSqlFile.getParentFile().exists() &&
                !indexAndRefSqlFile.getParentFile().mkdirs()) {
            String msg = MessageFormat.format("Create directory {0} failed.", new Object[]{indexAndRefSqlFile.getParentFile().getAbsolutePath()});
            throw new PDMParseRuntimeException(msg);
        }
        if (pdm.getViews().isEmpty()) {
            viewSqlFile.delete();
        } else if (!viewSqlFile.getParentFile().exists() &&
                !viewSqlFile.getParentFile().mkdirs()) {
            String msg = MessageFormat.format("Create directory {0} failed.", new Object[]{viewSqlFile
                    .getParentFile().getAbsolutePath()});
            throw new PDMParseRuntimeException(msg);
        }
        IDdlGenerator generator = DdlGeneratorFactory.getInstance(dbType);
        Writer tableWriter = null, indexAndReferWriter = null, viewWriter = null;
        try {
            if (!pdm.getTables().isEmpty()) {
                tableWriter = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(tableSqlFile), "gb2312"));
                generator.geneCreateTableDdl(pdm.getTables(), tableWriter);
            }
            if (indexAndRefSqlFile.exists() && !indexAndRefSqlFile.delete()) {
                String msg = MessageFormat.format("Create directory {0} failed.", new Object[]{indexAndRefSqlFile.getParentFile().getAbsolutePath()});
                throw new PDMParseRuntimeException(msg);
            }
            if (!pdm.getIndexs().isEmpty() || !pdm.getFkConstraints().isEmpty()) {
                indexAndReferWriter = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(
                                indexAndRefSqlFile, true), "gb2312"));
                generator.geneCreateIndexDdl(pdm.getIndexs(),
                        indexAndReferWriter);
                generator.geneAddConstraintDdl(pdm.getFkConstraints(),
                        indexAndReferWriter);
            }
            if (!pdm.getViews().isEmpty()) {
                viewWriter = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(viewSqlFile), "gb2312"));
                generator.geneCreateViewDdl(pdm.getViews(), viewWriter);
            }
        } catch (FileNotFoundException e) {
//            logger.error("File not found", e);
            throw new PDMParseRuntimeException("File not found");
        } catch (UnsupportedEncodingException e) {
            String msg = MessageFormat.format("{0} code unsupported.", new Object[]{"gb2312"});
//            logger.error(msg, e);
            throw new PDMParseRuntimeException(msg);
        } finally {
            IOUtils.closeQuietly(tableWriter);
            IOUtils.closeQuietly(indexAndReferWriter);
            IOUtils.closeQuietly(viewWriter);
        }
    }

    private File getSqlFile(String pdmName, VirtualFile sqlRoot, DatabaseType dbType, int ddlType) {
        StringBuilder sb = new StringBuilder();
        if (DatabaseType.SQLSERVER == dbType) {
            sb.append("SQLSERVER");
        } else if (DatabaseType.ORACLE == dbType) {
            sb.append("ORACLE");
        } else if (DatabaseType.DB2 == dbType) {
            sb.append("DB2");
        } else {
            throw new IllegalArgumentException("Unsupported dbType: " + dbType);
        }
        sb.append(File.separator);
        switch (ddlType) {
            case 1:
                sb.append("00001").append(File.separator).append("tb_")
                        .append(pdmName);
                sb.append(".sql");
                return new File(sqlRoot.getPath(), sb.toString());
            case 2:
                sb.append("00002").append(File.separator).append("fi_").append(pdmName);
                sb.append(".sql");
                return new File(sqlRoot.getPath(), sb.toString());
            case 3:
                sb.append("00003").append(File.separator).append("vtp_").append(pdmName);
                sb.append(".sql");
                return new File(sqlRoot.getPath(), sb.toString());
        }
        throw new IllegalArgumentException("Unsupported dbType:" + dbType);
    }

    private Pdm getCvtedPdm(Pdm pdm, DatabaseType dbType) {
        Pdm cvtedPdm = new Pdm();
        List<ITable> origTables = pdm.getTables();
        List<ITable> cvtTables = new ArrayList<ITable>();
        for (ITable origTable : origTables) {
            Table table = SqlConvertor.cloneTable(origTable);
            for (IColumn col : table.getAllColumns()) {
                Column column = (Column) col;
                String columnType = column.getTypeName().toLowerCase();
                if (columnType.startsWith("varchar") &&
                        "locale".equals(column.getStereotype())) {
                    int newColumnLen =
                            Math.round((float) (column.getLength() * 1.5D));
                    if (newColumnLen > 4000)
                        newColumnLen = 4000;
                    column.setLength(newColumnLen);
                    column.setTypeName("varchar(" + newColumnLen + ")");
                }
            }
            cvtTables.add(table);
        }
        origTables = cvtTables;
        List<Pdm.ViewInfo> origViews = pdm.getViews();
        if (DatabaseType.ORACLE == dbType) {
            for (ITable origTable : origTables)
                cvtedPdm.getTables().add(
                        SqlConvertor.cvtSqlServer2Oracle(origTable));
            for (Pdm.ViewInfo origView : origViews)
                cvtedPdm.getViews().add(
                        SqlConvertor.cvtViewFromSqlServer2Oracle(origView));
        } else if (DatabaseType.DB2 == dbType) {
            for (ITable origTable : origTables)
                cvtedPdm.getTables().add(
                        SqlConvertor.cvtSqlServer2Db2(origTable));
            for (Pdm.ViewInfo origView : origViews)
                cvtedPdm.getViews().add(
                        SqlConvertor.cvtViewFromSqlServer2Db2(origView));
        } else if (DatabaseType.SQLSERVER == dbType) {
            for (ITable origTable : origTables)
                cvtedPdm.getTables().add(origTable);
            for (Pdm.ViewInfo origView : origViews)
                cvtedPdm.getViews().add(origView);
        }
        cvtedPdm.setPdmName(pdm.getPdmName());
        cvtedPdm.getFkConstraints().addAll(pdm.getFkConstraints());
        cvtedPdm.getIndexs().addAll(pdm.getIndexs());
        return cvtedPdm;
    }

    private void checkPkConstaintNameLength(Pdm pdm) {
        checkPkConstaintNameLength(pdm.getTables(), DatabaseType.ORACLE,
                pdm.getPdmName());
        checkPkConstaintNameLength(pdm.getTables(), DatabaseType.DB2,
                pdm.getPdmName());
        checkPkConstaintNameLength(pdm.getTables(), DatabaseType.SQLSERVER,
                pdm.getPdmName());
    }

    private void checkPkConstaintNameLength(List<ITable> tables, DatabaseType dbType, String pdmName) {
        int length = 30;
        if (DatabaseType.ORACLE == dbType) {
            length = 30;
        } else if (DatabaseType.DB2 == dbType) {
            length = 18;
        }
        for (Iterator<ITable> iter = tables.iterator(); iter.hasNext(); ) {
            ITable table = (ITable) iter.next();
            if (table.getPkConstraint() != null) {
                String pkConstaintName = table.getPkConstraint().getName();
                if (pkConstaintName != null &&
                        pkConstaintName.length() > length) {
                    String msg = MessageFormat.format(
                            "The length of constraint name of primary key of table {1} belongs to  is exceeding", new Object[]{pdmName,
                                    table.getName(), String.valueOf(length)});
//                    logger.error(msg);
                    iter.remove();
                }
            }
        }
    }
}
