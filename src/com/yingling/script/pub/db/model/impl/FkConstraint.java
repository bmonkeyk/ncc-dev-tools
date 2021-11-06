package com.yingling.script.pub.db.model.impl;

import com.yingling.script.pub.db.model.IColumn;
import com.yingling.script.pub.db.model.IFkConstraint;
import com.yingling.script.pub.db.model.ITable;

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
