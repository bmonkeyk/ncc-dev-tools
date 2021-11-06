package com.yingling.script.pub.db.query;

import com.yingling.script.pub.db.model.ITable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlQueryResultSet {
    private ITable table;

    private List<Map<String, Object>> results;

    private List<DBRowBlobsResult> blobs;

    private List<SqlQueryResultSet> subResultSets;

    public SqlQueryResultSet(ITable table) {
        this.table = table;
        this.results = new ArrayList();
        this.subResultSets = new ArrayList();
        this.blobs = new ArrayList();
    }

    public ITable getTable() {
        return this.table;
    }

    public List<Map<String, Object>> getResults() {
        return this.results;
    }

    public List<SqlQueryResultSet> getSubResultSets() {
        return this.subResultSets;
    }

    public List<DBRowBlobsResult> getBlobs() {
        return this.blobs;
    }
}
