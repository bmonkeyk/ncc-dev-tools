package com.yingling.script.pub.db;

import com.yingling.script.pub.db.model.ITable;
import com.yingling.script.pub.db.model.TableStructure;
import com.yingling.script.pub.db.query.IQueryInfo;
import com.yingling.script.pub.db.query.QueryInfo;
import com.yingling.script.pub.db.query.SqlQueryResultSet;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class QueryService implements IQueryService {
    public SqlQueryResultSet query(String tableName, String whereCond, TableStructure struct, Connection conn) throws Exception {
        tableName = tableName.toLowerCase();
        if (!struct.getTable().equals(tableName))
            throw new IllegalArgumentException("主子表配置与传入表不一致。");
        QueryInfo queryInfo = new QueryInfo();
        ITable table = SqlUtil.retrieveTable(tableName, null, conn);
        if (table == null) {
            throw new Exception("在指定数据源中查询表" + tableName + "失败。");
        }
        queryInfo.setTable(table);
        queryInfo.setWhereCondition(whereCond);
        queryInfo.setChildren(new ArrayList());
        if (struct.getSubTables() != null && struct.getSubTables().size() > 0)
            for (TableStructure ts : struct.getSubTables())
                fillQueryInfoWithTableStructure(queryInfo, ts, conn);
        return query(queryInfo, conn);
    }

    private void fillQueryInfoWithTableStructure(QueryInfo queryInfo, TableStructure struct, Connection conn) {
        QueryInfo info = new QueryInfo();
        String tableName = struct.getTable();
        List<String> fkColNames = null;
        if (!StringUtils.isBlank(struct.getForeignKey())) {
            fkColNames = new ArrayList<String>();
            fkColNames.add(struct.getForeignKey());
        }
        ITable table = SqlUtil.retrieveTable(tableName, fkColNames, conn);
        info.setTable(table);
        info.setChildren(new ArrayList());
        if (struct.getSubTables() != null && struct.getSubTables().size() > 0)
            for (TableStructure ts : struct.getSubTables())
                fillQueryInfoWithTableStructure(info, ts, conn);
        queryInfo.getChildren().add(info);
    }

    public SqlQueryResultSet query(IQueryInfo queryInfo, Connection conn) {
        return SqlUtil.queryResults(queryInfo, conn);
    }
}
