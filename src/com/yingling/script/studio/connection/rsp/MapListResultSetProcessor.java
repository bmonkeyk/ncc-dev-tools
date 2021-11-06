package com.yingling.script.studio.connection.rsp;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapListResultSetProcessor extends Object implements IResultSetProcessor<List<Map<String, Object>>> {
    public List<Map<String, Object>> process(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ResultSetMetaData metaData = rs.getMetaData();
        while (rs.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i);
                Object value = rs.getObject(i);
                map.put(name.toLowerCase(), value);
            }
            list.add(map);
        }
        return list;
    }
}
