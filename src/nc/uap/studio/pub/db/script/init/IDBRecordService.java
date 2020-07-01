package nc.uap.studio.pub.db.script.init;

import nc.uap.studio.pub.db.script.ISqlFile;

import java.sql.Connection;
import java.util.Map;

public interface IDBRecordService {
    ISqlFile[] geneSqlFile(IDbRecordScript paramIDbRecordScript, Connection paramConnection, DbRecordSqlFileCfg paramDbRecordSqlFileCfg, Map<String, MLTableMetaInfo> paramMap);
}
