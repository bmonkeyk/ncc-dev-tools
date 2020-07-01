package nc.uap.studio.pub.db.script.init;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MLTableMetaInfo {
    private static final String ML_COLUMN_NAME_PATTERN = "\\d$";

    private String tableName;

    private List<String> columnNames;

    public String[] getColumnNames() {
        if (this.columnNames != null) {
            String[] retValue = new String[this.columnNames.size()];
            this.columnNames.toArray(retValue);
            return retValue;
        }
        return null;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void addColumnName(String columnName) {
        if (columnName != null) {
            if (this.columnNames == null)
                this.columnNames = new ArrayList();
            this.columnNames.add(columnName.toLowerCase());
        }
    }

    public boolean includeColumn(String columnName) {
        String lcColumnName = columnName.toLowerCase();
        if (this.columnNames.contains(lcColumnName))
            return true;
        for (String column : this.columnNames) {
            Pattern p = Pattern.compile(String.valueOf(column) + "\\d$");
            Matcher m = p.matcher(lcColumnName);
            if (m.find())
                return true;
        }
        return false;
    }
}
