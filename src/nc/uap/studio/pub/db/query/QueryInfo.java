package nc.uap.studio.pub.db.query;

import nc.uap.studio.pub.db.model.ITable;

import java.util.List;

public class QueryInfo implements IQueryInfo {
    public ITable table;

    public String whereCondition;

    public List<IQueryInfo> children;

    public List<IQueryInfo> getChildren() {
        return this.children;
    }

    public ITable getTable() {
        return this.table;
    }

    public String getWhereCondition() {
        return this.whereCondition;
    }

    public void setTable(ITable table) {
        this.table = table;
    }

    public void setWhereCondition(String whereCondition) {
        this.whereCondition = whereCondition;
    }

    public void setChildren(List<IQueryInfo> childs) {
        this.children = childs;
    }
}
