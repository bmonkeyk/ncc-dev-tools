package com.yingling.script.pub.db.query;

import com.yingling.script.pub.db.model.IColumn;
import com.yingling.script.pub.db.model.ITable;

public class DBRowBlobsResult {
    private ITable table;

    private IColumn pkColumn;

    private Object pkValue;

    private IColumn[] blobColumns;

    private byte[][] blobs;

    public ITable getTable() {
        return this.table;
    }

    public void setTable(ITable table) {
        this.table = table;
    }

    public IColumn getPkColumn() {
        return this.pkColumn;
    }

    public void setPkColumn(IColumn pkColumn) {
        this.pkColumn = pkColumn;
    }

    public Object getPkValue() {
        return this.pkValue;
    }

    public void setPkValue(Object pkValue) {
        this.pkValue = pkValue;
    }

    public IColumn[] getBlobColumns() {
        return this.blobColumns;
    }

    public void setBlobColumns(IColumn[] blobColumns) {
        this.blobColumns = blobColumns;
    }

    public byte[][] getBlobs() {
        return this.blobs;
    }

    public void setBlobs(byte[][] blobs) {
        this.blobs = blobs;
    }
}
