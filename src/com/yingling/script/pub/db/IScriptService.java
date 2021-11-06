package com.yingling.script.pub.db;

import com.yingling.script.pub.db.query.SqlQueryResultSet;
import com.yingling.script.pub.db.script.export.IScriptExportStratege;
import com.yingling.script.pub.db.script.export.SqlQueryInserts;

public interface IScriptService {
    SqlQueryInserts convert2InsertSQLs(SqlQueryResultSet paramSqlQueryResultSet);

    SqlQueryInserts convert2InsertSQLs(SqlQueryResultSet paramSqlQueryResultSet, boolean paramBoolean);

    boolean export(SqlQueryResultSet paramSqlQueryResultSet, IScriptExportStratege paramIScriptExportStratege);

    boolean export(SqlQueryResultSet paramSqlQueryResultSet, IScriptExportStratege paramIScriptExportStratege, boolean paramBoolean);

    void sync(String paramString, boolean paramBoolean, SqlQueryResultSet paramSqlQueryResultSet) throws Exception;
}
