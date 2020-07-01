package nc.uap.studio.pub.db.model;

import java.util.List;

public interface ITable extends ITableBase {
    IPkConstraint getPkConstraint();

    List<IFkConstraint> getFkConstraints();

    IFkConstraint getFkConstraintByRefTableName(String paramString);
}
