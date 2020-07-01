package nc.uap.studio.pub.db.query;

import nc.uap.studio.pub.db.model.ITable;

import java.util.List;

public interface IQueryInfo {
    ITable getTable();

    String getWhereCondition();

    List<? extends IQueryInfo> getChildren();
}
