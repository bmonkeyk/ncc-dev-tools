//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.yingling.script.studio.connection.dynamic;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.yingling.script.studio.connection.exception.ConnectionException;
import com.yingling.script.studio.connection.model.DataSourceMetaInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionUtil {
    public ConnectionUtil() {
    }

    public static void closeQuitely(BoneCP dataSource, Statement statement, Connection conn) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException var6) {
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException var5) {
            }
        }

        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (Exception var4) {
            }
        }

    }

    public static boolean testConnection(DataSourceMetaInfo meta) throws ConnectionException, SQLException, ClassNotFoundException {
        BoneCP dataSource = getDataSource(meta);
        Connection conn = dataSource.getConnection();
        if (conn != null) {
            Statement statement = null;

            try {
                statement = conn.createStatement();
                return true;
            } catch (SQLException var8) {
            } finally {
                closeQuitely(dataSource, statement, conn);
            }

            return false;
        } else {
            return false;
        }
    }

    public static BoneCP getDataSource(DataSourceMetaInfo meta) throws ConnectionException, SQLException, ClassNotFoundException {
        if (meta != null) {
            Class.forName(meta.getDriver(), true, BoneCPConfig.class.getClassLoader());
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(meta.getUrl());
            config.setUsername(meta.getUser());
            config.setPassword(meta.getPwd());
            BoneCP connPool = new BoneCP(config);
            return connPool;
        } else {
            return null;
        }
    }
}
