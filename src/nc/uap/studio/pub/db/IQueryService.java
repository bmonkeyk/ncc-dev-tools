package nc.uap.studio.pub.db;

import nc.uap.studio.pub.db.model.TableStructure;
import nc.uap.studio.pub.db.query.IQueryInfo;
import nc.uap.studio.pub.db.query.SqlQueryResultSet;

import java.sql.Connection;

public interface IQueryService {
    SqlQueryResultSet query(String paramString1, String paramString2, TableStructure paramTableStructure, Connection paramConnection) throws Exception;

    SqlQueryResultSet query(IQueryInfo paramIQueryInfo, Connection paramConnection);
}
