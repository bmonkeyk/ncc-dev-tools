package com.yonyou.common.tablestruct.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("item")
public class InitDataCfg {
    private String itemKey = "";

    @XStreamAlias("itemRule")
    private String tableName = "";

    @XStreamAlias("itemName")
    private String tableDesc = "";

    private String sysField = "";

    private String corpField = "";

    private String grpField = "";

    @XStreamAlias("fixedWhere")
    private String whereCondition = "1=1";

    public String getItemKey() {
        return this.itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getTableName() {
        return (this.tableName == null) ? null : this.tableName.toLowerCase();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableDesc() {
        return this.tableDesc;
    }

    public void setTableDesc(String tableDesc) {
        this.tableDesc = tableDesc;
    }

    public String getSysField() {
        return this.sysField;
    }

    public void setSysField(String sysField) {
        this.sysField = sysField;
    }

    public String getCorpField() {
        return this.corpField;
    }

    public void setCorpField(String corpField) {
        this.corpField = corpField;
    }

    public String getGrpField() {
        return this.grpField;
    }

    public void setGrpField(String grpField) {
        this.grpField = grpField;
    }

    public String getWhereCondition() {
        return this.whereCondition;
    }

    public void setWhereCondition(String whereCondition) {
        this.whereCondition = whereCondition;
    }
}
