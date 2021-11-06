package com.yingling.script.studio.connection;

import com.yingling.script.studio.connection.exception.ConnectionException;
import com.yingling.script.studio.connection.model.DataSourceMetaInfo;
import com.yingling.script.studio.connection.rsp.IResultSetProcessor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionService {

//    protected static Logger logger = LoggerFactory.getLogger(ConnectionService.class.getName());

    private static PoolFacade instance = null;

    public ConnectionService() {
    }

    public static boolean testConnection(DataSourceMetaInfo meta) throws ConnectionException {
        return getPoolFacade().testConnection(meta);
    }

    public static <T> T executeQuery(String sql, IResultSetProcessor<T> processor) throws ConnectionException {
        return executeQuery(getPoolFacade().getDefaultDataSourceName(), sql, processor);
    }

    public static void closeQuietly(Connection connection, Statement statement, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
//                logger.error(e.getMessage(), e);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
//                logger.error(e.getMessage(), e);
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
//                logger.error(e.getMessage(), e);
            }
        }

    }

    public static <T> T executeQuery(String dsName, String sql, IResultSetProcessor<T> processor) throws ConnectionException {
        Connection connection = getConnection(dsName);
        Statement statement = null;
        ResultSet rs = null;
        Object t = null;

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            t = processor.process(rs);
        } catch (SQLException e) {
//            logger.error(e.getMessage(), e);
        } finally {
            closeQuietly(connection, statement, rs);
        }

        return (T) t;
    }

    public static boolean executeBatch(String dsName, String... sqls) throws SQLException, ConnectionException {
        Connection connection = getConnection(dsName);
        Statement statement = null;

        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String[] var7 = sqls;
            int var6 = sqls.length;

            for (int var5 = 0; var5 < var6; ++var5) {
                String sql = var7[var5];
                statement.addBatch(sql);
            }

            statement.executeBatch();
            connection.commit();
            return true;
        } catch (SQLException e) {
//            logger.error(e.getMessage(), e);
            try {
                connection.rollback();
            } catch (SQLException ex) {
//                logger.error(ex.getMessage(), ex);
                throw ex;
            }

            throw e;
        } finally {
            closeQuietly(connection, statement, (ResultSet) null);
        }
    }

    public static Connection getConnection() throws ConnectionException {
        return getConnection(getPoolFacade().getDefaultDataSourceName());
    }

    public static Connection getConnection(String dsName) throws ConnectionException {
        return getPoolFacade().getConnection(dsName);
    }

    public static String[] getDataSourceNames() {
        return getPoolFacade().getDataSourceNames();
    }

    public static String getDesignDataSourceName() {
        return getPoolFacade().getDefaultDataSourceName();
    }

    public static String getBaseDataSourceName() {
        return getPoolFacade().getSecondaryDataSourceName();
    }

    public static DataSourceMetaInfo getDataSourceMetaInfo(String dataSourceName) {
        return getPoolFacade().getDataSourceMetaInfo(dataSourceName);
    }

    public static void setPoolFacade(PoolFacade facade) {
        instance = facade;
    }

    public static PoolFacade getPoolFacade() {
        if (instance == null) {
            instance = new PoolFacade();
        }
        return instance;
    }

    public static void dispose() {
        if (instance != null) {
            instance.dispose();
        }

    }
}
