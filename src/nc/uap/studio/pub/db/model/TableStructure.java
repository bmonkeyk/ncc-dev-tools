package nc.uap.studio.pub.db.model;

import java.util.List;

public class TableStructure {
    private String table;

    private String foreignKey;

    private List<TableStructure> subTables;

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        if (table != null) {
            this.table = table.toLowerCase();
        } else {
            this.table = null;
        }
    }

    public String getForeignKey() {
        return this.foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public List<TableStructure> getSubTables() {
        return this.subTables;
    }

    public void setSubTables(List<TableStructure> subTables) {
        this.subTables = subTables;
    }
}
