package nc.uap.studio.pub.db.model.impl;

import nc.uap.studio.pub.db.model.IColumn;
import nc.uap.studio.pub.db.model.IFkConstraint;
import nc.uap.studio.pub.db.model.ITable;

import java.util.ArrayList;
import java.util.List;

public class FkConstraint extends Constraint implements IFkConstraint {
    private ITable refTable;

    private List<IColumn> refColumns = new ArrayList();

    public ITable getRefTable() {
        return this.refTable;
    }

    public void setRefTable(ITable refTable) {
        this.refTable = refTable;
    }

    public List<IColumn> getRefColumns() {
        return this.refColumns;
    }
}
