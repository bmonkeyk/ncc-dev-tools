package nc.uap.studio.pub.db.model.impl;

import nc.uap.studio.pub.db.model.IColumn;
import nc.uap.studio.pub.db.model.IIndex;
import nc.uap.studio.pub.db.model.ITable;

import java.util.ArrayList;
import java.util.List;

public class Index implements IIndex {
    private String name;

    private ITable table;

    private List<IColumn> columns = new ArrayList();

    private boolean clustered;

    private boolean unique;

    private String desc;

    public List<IColumn> getColumns() {
        return this.columns;
    }

    public String getName() {
        return this.name;
    }

    public ITable getTable() {
        return this.table;
    }

    public boolean isClustered() {
        return this.clustered;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTable(ITable table) {
        this.table = table;
    }

    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
