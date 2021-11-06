package com.yingling.script.pub.db.model;

import java.util.List;

public interface IIndex {
    String getName();

    ITable getTable();

    List<IColumn> getColumns();

    boolean isClustered();

    boolean isUnique();

    String getDesc();
}
