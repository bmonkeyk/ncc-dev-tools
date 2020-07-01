package nc.uap.studio.pub.db.model;

import java.util.List;

public interface IConstraint {
    String getName();

    ITable getTable();

    List<IColumn> getColumns();
}
