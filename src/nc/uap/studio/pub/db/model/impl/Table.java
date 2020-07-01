package nc.uap.studio.pub.db.model.impl;

import nc.uap.studio.pub.db.model.IColumn;
import nc.uap.studio.pub.db.model.IFkConstraint;
import nc.uap.studio.pub.db.model.IPkConstraint;
import nc.uap.studio.pub.db.model.ITable;
import org.apache.commons.io.IOUtils;

import java.util.ArrayList;
import java.util.List;

public class Table implements ITable {
    private String name;

    private List<IColumn> allColumns = new ArrayList();

    private List<IFkConstraint> fkConstraints = new ArrayList();

    private IPkConstraint pkConstraint;

    private String desc;

    public List<IColumn> getAllColumns() {
        return this.allColumns;
    }

    public List<IFkConstraint> getFkConstraints() {
        return this.fkConstraints;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null) {
            this.name = null;
        } else {
            this.name = name.toLowerCase();
        }
    }

    public IPkConstraint getPkConstraint() {
        return this.pkConstraint;
    }

    public void setPkConstraint(IPkConstraint pkConstraint) {
        this.pkConstraint = pkConstraint;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public IColumn getColumnByName(String colName) {
        for (IColumn col : this.allColumns) {
            if (col.getName().equalsIgnoreCase(colName))
                return col;
        }
        return null;
    }

    public IFkConstraint getFkConstraintByRefTableName(String refTableName) {
        for (IFkConstraint fkConstraint : this.fkConstraints) {
            if (fkConstraint.getRefTable().getName().equalsIgnoreCase(refTableName))
                return fkConstraint;
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append("(");
        for (IColumn col : this.allColumns)
            sb.append(col.getName()).append(" ").append(col.getTypeName()).append(", ");
        sb.append(IOUtils.LINE_SEPARATOR).append("primary key: (");
        for (IColumn pkCol : this.pkConstraint.getColumns())
            sb.append(pkCol.getName()).append(",");
        sb.deleteCharAt(sb.length() - 1).append(")");
        if (this.fkConstraints != null && !this.fkConstraints.isEmpty())
            for (IFkConstraint fkCol : this.fkConstraints) {
                sb.append(IOUtils.LINE_SEPARATOR).append("foreign key: (");
                for (IColumn col : fkCol.getColumns())
                    sb.append(col.getName()).append(",");
                sb.deleteCharAt(sb.length() - 1).append(")");
            }
        sb.append(")");
        return sb.toString();
    }
}
