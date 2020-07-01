package nc.uap.studio.pub.db.model;

import java.util.List;

public interface IFkConstraint extends IConstraint {
    ITable getRefTable();

    List<IColumn> getRefColumns();
}
