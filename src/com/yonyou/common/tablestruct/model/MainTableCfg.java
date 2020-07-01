package com.yonyou.common.tablestruct.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("hierarchy")
public class MainTableCfg {
    private String tableName;

    private String sqlNo;

    private String substitutionGroup;

    @XStreamAlias("subTableGroup")
    private List<SubTableCfg> children;

    public String getTableName() {
        return (this.tableName == null) ? null : this.tableName.toLowerCase();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    public List<SubTableCfg> getChildren() {
        return this.children;
    }

    public void setChildren(List<SubTableCfg> children) {
        this.children = children;
    }
}
