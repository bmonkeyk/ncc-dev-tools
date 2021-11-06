package com.yingling.script.studio.connection.rsp;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountResultSetProcessor extends Object implements IResultSetProcessor<Integer> {
    public Integer process(ResultSet resultSet) throws SQLException {
        if (resultSet.next())
            return Integer.valueOf(resultSet.getInt(1));
        return Integer.valueOf(-1);
    }
}
