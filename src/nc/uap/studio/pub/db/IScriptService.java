package nc.uap.studio.pub.db;

import nc.uap.studio.pub.db.query.SqlQueryResultSet;
import nc.uap.studio.pub.db.script.export.IScriptExportStratege;
import nc.uap.studio.pub.db.script.export.SqlQueryInserts;

public interface IScriptService {
    SqlQueryInserts convert2InsertSQLs(SqlQueryResultSet paramSqlQueryResultSet);

    SqlQueryInserts convert2InsertSQLs(SqlQueryResultSet paramSqlQueryResultSet, boolean paramBoolean);

    boolean export(SqlQueryResultSet paramSqlQueryResultSet, IScriptExportStratege paramIScriptExportStratege);

    boolean export(SqlQueryResultSet paramSqlQueryResultSet, IScriptExportStratege paramIScriptExportStratege, boolean paramBoolean);

    void sync(String paramString, boolean paramBoolean, SqlQueryResultSet paramSqlQueryResultSet) throws Exception;
}
