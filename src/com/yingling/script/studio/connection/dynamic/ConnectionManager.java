//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.yingling.script.studio.connection.dynamic;

import com.jolbox.bonecp.BoneCP;
import com.yingling.script.studio.connection.exception.ConnectionException;
import com.yingling.script.studio.connection.model.DataSourceMetaInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConnectionManager {
    private Map<String, DataSourceMetaInfo> metaMap = new HashMap();
    private Map<DataSourceMetaInfo, BoneCP> dataSourceMap = new HashMap();

    public ConnectionManager() {
    }

    public void dispose() {
        Iterator var2 = this.dataSourceMap.values().iterator();

        while (var2.hasNext()) {
            BoneCP ds = (BoneCP) var2.next();

            try {
                ds.close();
            } catch (Exception var4) {
            }
        }

        this.dataSourceMap.clear();
        this.metaMap.clear();
    }

    public void reloadDataSourceMeta(DataSourceMetaInfo[] metas) {
        this.dispose();
        DataSourceMetaInfo[] var5 = metas;
        int var4 = metas.length;

        for (int var3 = 0; var3 < var4; ++var3) {
            DataSourceMetaInfo meta = var5[var3];
            String name = meta.getName();
            this.metaMap.put(name, meta);
        }

    }

    public Connection getConnection(DataSourceMetaInfo meta) throws SQLException, ConnectionException, ClassNotFoundException {
        if (meta != null) {
            BoneCP comboPooledDataSource = (BoneCP) this.dataSourceMap.get(meta);
            if (comboPooledDataSource == null) {
                comboPooledDataSource = ConnectionUtil.getDataSource(meta);
                if (comboPooledDataSource == null) {
                    String msg = MessageFormat.format("Can't get datasource, confirm the information url:{0}; driverClass:{1}", meta.getUrl(), meta.getDriver());
                    throw new ConnectionException(msg);
                }

                this.dataSourceMap.put(meta, comboPooledDataSource);
            }

            return comboPooledDataSource.getConnection();
        } else {
            return null;
        }
    }

    public Connection getConnection(String metaName) throws SQLException, ConnectionException, ClassNotFoundException {
        return this.getConnection((DataSourceMetaInfo) this.metaMap.get(metaName));
    }

    public DataSourceMetaInfo getDataSourceMetaInfo(String dataSourceName) {
        return this.metaMap != null ? (DataSourceMetaInfo) this.metaMap.get(dataSourceName) : null;
    }
}
