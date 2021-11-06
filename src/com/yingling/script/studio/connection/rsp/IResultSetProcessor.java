package com.yingling.script.studio.connection.rsp;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IResultSetProcessor<T> {
    T process(ResultSet paramResultSet) throws SQLException;
}
