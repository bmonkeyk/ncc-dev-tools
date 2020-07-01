package com.yonyou.ria.core.scriptexport.ui.actions;

import nc.uap.studio.pub.db.model.TableStructure;

import java.util.Map;

public class InitDataInfo {
    private String bc;

    private boolean isBusiness = true;

    private String table;

    private String where;

    private TableStructure struct;

    private Map<String, String> tableNoMap;

    private String mapName;

    public boolean isBusiness() {
        return this.isBusiness;
    }

    public void setBusiness(boolean isBusiness) {
        this.isBusiness = isBusiness;
    }

    public TableStructure getStruct() {
        return this.struct;
    }

    public void setStruct(TableStructure struct) {
        this.struct = struct;
    }

    public Map<String, String> getTableNoMap() {
        return this.tableNoMap;
    }

    public void setTableNoMap(Map<String, String> tableNoMap) {
        this.tableNoMap = tableNoMap;
    }

    public String getTable() {
        return (this.table == null) ? null : this.table.toLowerCase();
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getWhere() {
        return this.where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getMapName() {
        return this.mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getBc() {
        return this.bc;
    }

    public void setBc(String bc) {
        this.bc = bc;
    }
}
