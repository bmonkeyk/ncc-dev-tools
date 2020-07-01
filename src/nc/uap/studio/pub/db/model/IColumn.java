package nc.uap.studio.pub.db.model;

public interface IColumn {
    String getName();

    ITableBase getTableBase();

    int getDataType();

    String getTypeName();

    int getLength();

    int getPrecise();

    boolean isNullable();

    String getDefaultValue();

    String getDesc();

    String getStereotype();
}
