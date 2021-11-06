package com.yingling.script.common.tablestruct.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("subTable")
public class SubTableCfg {
    String tableName;

    @XStreamAlias("foreignKeyColumn")
    String fkColumn;

    String whereCondition;

    String sqlNo;

    String substitutionGroup;

    String grpField;

    String tableDesc;

    @XStreamAlias("subTableGroup")
    List<SubTableCfg> children;

    public String getTableName() {
        return (this.tableName == null) ? null : this.tableName.toLowerCase();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFkColumn() {
        return this.fkColumn;
    }

    public void setFkColumn(String fkColumn) {
        this.fkColumn = fkColumn;
    }

    public String getWhereCondition() {
        return this.whereCondition;
    }

    public void setWhereCondition(String whereCondition) {
        this.whereCondition = whereCondition;
    }

    public String getSqlNo() {
        return this.sqlNo;
    }

    public void setSqlNo(String sqlNo) {
        this.sqlNo = sqlNo;
    }

    public String getSubstitutionGroup() {
        return this.substitutionGroup;
    }

    public void setSubstitutionGroup(String substitutionGroup) {
        this.substitutionGroup = substitutionGroup;
    }

    public String getGrpField() {
        return this.grpField;
    }

    public void setGrpField(String grpField) {
        this.grpField = grpField;
    }

    public String getTableDesc() {
        return this.tableDesc;
    }

    public void setTableDesc(String tableDesc) {
        this.tableDesc = tableDesc;
    }

    public List<SubTableCfg> getChildren() {
        return this.children;
    }

    public void setChildren(List<SubTableCfg> children) {
        this.children = children;
    }
}
