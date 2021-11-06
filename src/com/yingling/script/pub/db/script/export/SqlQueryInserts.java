package com.yingling.script.pub.db.script.export;

import com.yingling.script.pub.db.model.ITable;
import com.yingling.script.pub.db.query.SqlQueryResultSet;

import java.util.ArrayList;
import java.util.List;

public class SqlQueryInserts {
    private ITable table;

    private List<String> results;

    private List<SqlQueryInserts> subInserts;

    private SqlQueryResultSet resultSet;

    public SqlQueryInserts(ITable table) {
        this.table = table;
        this.results = new ArrayList();
        this.subInserts = new ArrayList();
    }

    public ITable getTable() {
        return this.table;
    }

    public List<SqlQueryInserts> getSubResultSets() {
        return this.subInserts;
    }

    public List<SqlQueryInserts> getSubInserts() {
        return this.subInserts;
    }

    public void setSubInserts(List<SqlQueryInserts> subInserts) {
        this.subInserts = subInserts;
    }

    public List<String> getResults() {
        return this.results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public SqlQueryResultSet getResultSet() {
        return this.resultSet;
    }

    public void setResultSet(SqlQueryResultSet resultSet) {
        this.resultSet = resultSet;
    }
}
