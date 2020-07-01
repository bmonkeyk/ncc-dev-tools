package nc.uap.studio.pub.db.model;

import java.util.List;

public interface ITableBase {
    String getName();

    List<IColumn> getAllColumns();

    IColumn getColumnByName(String paramString);

    String getDesc();
}
