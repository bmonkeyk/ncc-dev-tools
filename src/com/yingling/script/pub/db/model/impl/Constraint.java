package com.yingling.script.pub.db.model.impl;

import com.yingling.script.pub.db.model.IColumn;
import com.yingling.script.pub.db.model.IConstraint;
import com.yingling.script.pub.db.model.ITable;

import java.util.ArrayList;
import java.util.List;

public class Constraint implements IConstraint {
    private String name;

    private ITable table;

    private List<IColumn> columns = new ArrayList();

    public List<IColumn> getColumns() {
        return this.columns;
    }

    public String getName() {
        return this.name;
    }

    public ITable getTable() {
        return this.table;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTable(ITable table) {
        this.table = table;
    }

    public void setColumns(List<IColumn> columns) {
        this.columns = columns;
    }
}
