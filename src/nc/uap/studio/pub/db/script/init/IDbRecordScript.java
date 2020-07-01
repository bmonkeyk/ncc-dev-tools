package nc.uap.studio.pub.db.script.init;

import nc.uap.studio.pub.db.query.IQueryInfo;

import java.util.List;

public interface IDbRecordScript extends IQueryInfo, IDbRecordExportInfo {
    List<IDbRecordScript> getChildren();
}
