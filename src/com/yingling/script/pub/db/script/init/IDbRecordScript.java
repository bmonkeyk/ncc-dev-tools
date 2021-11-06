package com.yingling.script.pub.db.script.init;

import com.yingling.script.pub.db.query.IQueryInfo;

import java.util.List;

public interface IDbRecordScript extends IQueryInfo, IDbRecordExportInfo {
    List<IDbRecordScript> getChildren();
}
