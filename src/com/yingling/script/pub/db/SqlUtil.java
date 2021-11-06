package com.yingling.script.pub.db;

import com.yingling.script.pub.db.model.IColumn;
import com.yingling.script.pub.db.model.IFkConstraint;
import com.yingling.script.pub.db.model.IPkConstraint;
import com.yingling.script.pub.db.model.ITable;
import com.yingling.script.pub.db.exception.DatabaseRuntimeException;
import com.yingling.script.pub.db.model.impl.Column;
import com.yingling.script.pub.db.model.impl.FkConstraint;
import com.yingling.script.pub.db.model.impl.PkConstraint;
import com.yingling.script.pub.db.model.impl.Table;
import com.yingling.script.pub.db.query.IQueryInfo;
import com.yingling.script.pub.db.query.SqlQueryResultSet;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlUtil {
    private static final String DB_TYPE_ORACLE = "Oracle";

    private static final String DB_TYPE_SQLSERVER = "Microsoft SQL Server";

    private static final String DB_TYPE_DB2 = "DB2";

//    protected static Logger logger = LoggerFactory.getLogger(SqlUtil.class.getName());

    public static ITable retrieveTable(String tableName, List<String> fkColNames, Connection conn) throws DatabaseRuntimeException {
        DatabaseMetaData metaData = null;
        String dbType = null, userName = null;
        try {
            metaData = conn.getMetaData();
            dbType = metaData.getDatabaseProductName();
            userName = metaData.getUserName();
        } catch (SQLException e) {
//            logger.error("获取表" + tableName + "元数据出错。", e);
            throw new DatabaseRuntimeException("获取表" + tableName + "元数据出错。");
        }
        return retrieveTable(tableName, fkColNames, metaData, dbType, userName);
    }

    public static ITable retrieveTable(String tableName, List<String> fkColNames, DatabaseMetaData metaData, String dbType, String userName) throws DatabaseRuntimeException {
        Table table = new Table();
        table.setName(tableName);
        String[] pkColNamesBySeq = new String[10];
        int nPos = 0;
        ResultSet pkRs = null;
        try {
            pkRs = metaData.getPrimaryKeys(retriveCatelog(dbType),
                    retriveSchema(dbType, userName),
                    formatTableName(tableName, dbType));
            while (pkRs.next()) {
                String colName = pkRs.getString("COLUMN_NAME");
                short seq = pkRs.getShort("KEY_SEQ");
                pkColNamesBySeq[seq - 1] = colName;
                nPos++;
            }
        } catch (SQLException e) {
//            logger.error("获取表" + tableName + "主键信息出错。", e);
            throw new DatabaseRuntimeException("获取表" + tableName + "主键信息出错。");
        } finally {
            if (pkRs != null)
                try {
                    pkRs.close();
                } catch (SQLException e) {
//                    logger.error("Close result set error.", e);
                }
        }
        List<String> pkColNames = new ArrayList<String>(nPos);
        for (int i = 0; i < nPos; i++)
            pkColNames.add(pkColNamesBySeq[i]);
        List<IColumn> allCols = table.getAllColumns();
        PkConstraint pkConstraint = new PkConstraint();
        table.setPkConstraint(pkConstraint);
        IColumn[] pkCols = new IColumn[pkColNames.size()];
        List<IFkConstraint> fkConstraints = table.getFkConstraints();
        boolean hasFkCols = false;
        List<String> upperFkColNames = null;
        FkConstraint fkConstraint = null;
        if (fkColNames != null && !fkColNames.isEmpty()) {
            hasFkCols = true;
            upperFkColNames = new ArrayList<String>();
            for (String str : fkColNames)
                upperFkColNames.add(str.toUpperCase());
            fkConstraint = new FkConstraint();
            fkConstraints.add(fkConstraint);
        }
        ResultSet colRs = null;
        try {
            colRs = metaData.getColumns(retriveCatelog(dbType),
                    retriveSchema(dbType, userName),
                    formatTableName(tableName, dbType), "%");
            while (colRs.next()) {
                String colName = colRs.getString("COLUMN_NAME");
                short dataType = colRs.getShort("DATA_TYPE");
                String typeName = colRs.getString("TYPE_NAME");
                Column col = new Column();
                col.setName(colName);
                col.setDataType(dataType);
                col.setTypeName(typeName);
                allCols.add(col);
                if (hasFkCols &&
                        upperFkColNames.contains(colName.toUpperCase())) {
                    fkConstraint.getColumns().add(col);
                    continue;
                }
                if (pkColNames.contains(colName)) {
                    int pos = pkColNames.indexOf(colName);
                    pkCols[pos] = col;
                }
            }
            if (allCols.isEmpty())
                return null;
            if (hasFkCols && fkConstraint.getColumns().isEmpty())
                throw new DatabaseRuntimeException("表" +
                        tableName + "的外键列" +
                        StringUtils.join(fkColNames.iterator(), ",") +
                        "在表中不存在。");
            pkConstraint.getColumns().addAll(Arrays.asList(pkCols));
            return table;
        } catch (SQLException sQLException) {
//            logger.error(String.format("获取表%s列信息出错。", new Object[]{tableName}));
            throw new DatabaseRuntimeException(String.format("获取表%s列信息出错。", new Object[]{tableName}));
        } finally {
            if (colRs != null)
                try {
                    colRs.close();
                } catch (SQLException e) {
//                    logger.error("Close result set error.", e);
                }
        }
    }

    public static SqlQueryResultSet queryResults(IQueryInfo queryInfo, Connection conn) throws DatabaseRuntimeException {
        return queryResults(queryInfo, queryInfo.getWhereCondition(), conn);
    }

    public static SqlQueryResultSet queryResults(ITable table, String whereCondition, Connection conn) throws DatabaseRuntimeException {
        if (table != null) {
            checkTable(table);
            String sql = getSql(table, whereCondition);
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                SqlQueryResultSet sqlQueryResultSet = new SqlQueryResultSet(
                        table);
                while (rs.next()) {
                    Map<String, Object> colNameValueMap = new LinkedHashMap<String, Object>();
                    for (IColumn col : table.getAllColumns())
                        colNameValueMap.put(col.getName(),
                                rs.getObject(col.getName()));
                    sqlQueryResultSet.getResults().add(colNameValueMap);
                }
                return sqlQueryResultSet;
            } catch (SQLException e) {
//                logger.error("数据库查询异常:", e);
                throw new DatabaseRuntimeException("数据库查询异常，请检查查询条件是否有效:" + sql);
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
            }
        }
        throw new DatabaseRuntimeException("查询条件参数有误。");
    }

    public static String formatSql(Object obj, int dataType) {
        if (obj != null) {
            String str = obj.toString().trim();
            if (isTypeString(dataType)) {
                str = str.replace("'", "''");
                str = "'" + str + "'";
            }
            return str;
        }
        return "null";
    }

    public static String geneInClause(String col, Collection<String> values) {
        StringBuilder sql = (new StringBuilder(col)).append(" in(");
        int i = 0, remainedSize = values.size();
        for (String str : values) {
            i++;
            remainedSize--;
            sql.append(str).append(",");
            if (i >= 100 && remainedSize > 0) {
                sql.deleteCharAt(sql.length() - 1);
                sql.append(")");
                sql.append(" or ").append(col).append(" in(");
                i = 0;
            }
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        return sql.toString();
    }

    private static SqlQueryResultSet queryResults(IQueryInfo queryInfo, String whereCondition, Connection conn) throws DatabaseRuntimeException {
        ITable mainTable = queryInfo.getTable();
        SqlQueryResultSet sqlQueryResultSet = queryResults(mainTable,
                whereCondition, conn);
        if (sqlQueryResultSet != null &&
                !sqlQueryResultSet.getResults().isEmpty() &&
                queryInfo.getChildren() != null &&
                !queryInfo.getChildren().isEmpty()) {
            IPkConstraint pkConstraint = queryInfo.getTable()
                    .getPkConstraint();
            List<IColumn> pkCols = (pkConstraint != null) ? pkConstraint
                    .getColumns() : null;
            if (pkCols == null || pkCols.isEmpty())
                throw new DatabaseRuntimeException("表" +
                        mainTable.getName() + "不存在主键。");
            if (pkCols.size() != 1)
                throw new DatabaseRuntimeException("表" +
                        mainTable.getName() + "为复合主键，不支持子表查询。");
            List<Map<String, Object>> results = sqlQueryResultSet
                    .getResults();
            String pkName = ((IColumn) pkCols.get(0)).getName();
            int dataType = ((IColumn) pkCols.get(0)).getDataType();
            Set<String> pks = new HashSet<String>();
            for (Map<String, Object> colNameValue : results) {
                Object obj = colNameValue.get(pkName);
                pks.add(formatSql(obj, dataType));
            }
            for (IQueryInfo subQryInfo : queryInfo.getChildren()) {
                ITable subTable = subQryInfo.getTable();
                IFkConstraint fkConstraint = null;
                if (subTable == null)
                    continue;
                if (subTable.getFkConstraints() != null)
                    if (subTable.getFkConstraints().size() == 1) {
                        fkConstraint = (IFkConstraint) subTable.getFkConstraints().get(0);
                    } else {
                        fkConstraint = subTable
                                .getFkConstraintByRefTableName(mainTable
                                        .getName());
                    }
                if (fkConstraint == null)
                    throw new DatabaseRuntimeException("子表" +
                            subTable.getName() + "没有外键列。");
                if (fkConstraint.getColumns().size() != 1)
                    throw new DatabaseRuntimeException("子表" +
                            subTable.getName() + "为多外键列，不支持。");
                SqlQueryResultSet subResultSet = queryResults(
                        subQryInfo,
                        geneInClause(((IColumn) fkConstraint.getColumns().get(0))
                                .getName(), pks), conn);
                if (subResultSet != null)
                    sqlQueryResultSet.getSubResultSets().add(
                            subResultSet);
            }
        }
        return sqlQueryResultSet;
    }

    private static void checkTable(ITable table) throws DatabaseRuntimeException {
        if (table != null && StringUtils.isNotBlank(table.getName()) &&
                table.getAllColumns() != null &&
                !table.getAllColumns().isEmpty())
            return;
        throw new DatabaseRuntimeException("查询条件参数有误。");
    }

    private static String getSql(ITable table, String whereCondition) {
        StringBuilder sql = new StringBuilder("select ");
        for (IColumn col : table.getAllColumns())
            sql.append(col.getName()).append(", ");
        sql.delete(sql.length() - 2, sql.length());
        sql.append(" from ").append(table.getName());
        if (StringUtils.isNotBlank(whereCondition))
            sql.append(" where ").append(whereCondition);
        return sql.toString();
    }

    private static boolean isTypeString(int dataType) {
        if (1 != dataType && 12 != dataType &&
                -1 != dataType && -9 != dataType &&
                -15 != dataType)
            return false;
        return true;
    }

    private static String retriveCatelog(String dataBaseType) {
        if ("Oracle".equalsIgnoreCase(dataBaseType))
            return "";
        return null;
    }

    private static String retriveSchema(String dbType, String schema) {
        if ("Microsoft SQL Server".equalsIgnoreCase(dbType))
            return null;
        if ("Oracle".equalsIgnoreCase(dbType))
            return schema.toUpperCase();
        if ("DB2".equalsIgnoreCase(dbType))
            return schema;
        return null;
    }

    private static String formatTableName(String tableName, String dbType) {
        if ("Microsoft SQL Server".equalsIgnoreCase(dbType))
            return tableName;
        if ("Oracle".equalsIgnoreCase(dbType))
            return tableName.toUpperCase();
        if ("DB2".equalsIgnoreCase(dbType))
            return tableName.toUpperCase();
        return null;
    }
}
