package nc.uap.studio.pub.db.script.export;

public interface IScriptExportStratege {
    public static final String DEFAULT_ENCODING = "UTF-8";

    boolean export(SqlQueryInserts paramSqlQueryInserts);
}
