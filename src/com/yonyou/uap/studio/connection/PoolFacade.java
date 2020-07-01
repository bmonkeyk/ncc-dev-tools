package com.yonyou.uap.studio.connection;

import com.yonyou.uap.studio.connection.dynamic.ConnectionManager;
import com.yonyou.uap.studio.connection.dynamic.ConnectionUtil;
import com.yonyou.uap.studio.connection.exception.ConnectionException;
import com.yonyou.uap.studio.connection.ierp.IerpDataSourceProvider;
import com.yonyou.uap.studio.connection.model.DataSourceMetaInfo;
import com.yonyou.uap.studio.connection.provider.IDataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class PoolFacade {
    protected static Logger logger = LoggerFactory.getLogger(PoolFacade.class.getName());

//    private static final String[] libraries = {"lib/dynamic.jar",
//            "lib/bonecp-0.7.1.RELEASE.jar", "lib/guava-14.0.1.jar",
//            "lib/slf4j-api-1.7.5.jar", "lib/slf4j-log4j12-1.7.5.jar",
//            "lib/log4j-1.2.17.jar"};


    //    private URLClassLoader loader = null;
    private IDataSourceProvider provider = null;

    private ConnectionManager manager = null;

    public String[] getDataSourceNames() {
        return (getProvider() == null) ? null : getProvider()
                .getDataSourceNames();
    }

    public Connection getConnection(String dsName) throws ConnectionException {
        ConnectionManager connectionManager = getConnectionManager();

        if (connectionManager != null) {
            try {
                return connectionManager.getConnection(dsName);
            } catch (Exception e) {
                throw new ConnectionException(e);
            }
        }
        return null;
    }

    protected ConnectionManager getConnectionManager() throws ConnectionException {
        if (this.manager == null)
            try {
                this.manager = new ConnectionManager();
                DataSourceMetaInfo[] metas = (getProvider() == null) ? new DataSourceMetaInfo[0] : getProvider().getDataSourceMetas();
                manager.reloadDataSourceMeta(metas);
            } catch (Exception e) {
                throw new ConnectionException(e);
            }
        return this.manager;
    }

    public boolean testConnection(DataSourceMetaInfo meta) throws ConnectionException {
        try {
            ConnectionUtil.testConnection(meta);
            return ConnectionUtil.testConnection(meta);
        } catch (Exception e) {
            throw new ConnectionException(e);
        }
    }

//    protected URLClassLoader getClassLoader() {
//        if (this.loader == null)
//            this.loader = new URLClassLoader(calculateLibraries(),
//                    PoolFacade.class.getClassLoader());
//        return this.loader;
//    }

//    protected URL[] calculateLibraries() {
//        List<URL> urls = new ArrayList<URL>();
//        String[] arrayOfString = new String[libraries.length];
//        for (int b = 0; b < libraries.length; b++) {
//            String path = arrayOfString[b];
//            URL url = Thread.currentThread().getContextClassLoader().getResource(path);
//            urls.add(url);
//        }
//        if (getProvider() != null)
//            Collections.addAll(urls, getProvider().getDriverLibraries());
//        return urls.toArray(new URL[0]);
//    }

    public String getDefaultDataSourceName() {
        return (getProvider() == null) ? null : getProvider()
                .getDefaultDataSourceName();
    }

    public String getSecondaryDataSourceName() {
        return (getProvider() == null) ? null : getProvider()
                .getSecondaryDataSourceName();
    }

    public DataSourceMetaInfo getDataSourceMetaInfo(String dataSourceName) {
        try {
            ConnectionManager connectionManager = getConnectionManager();
            if (connectionManager != null) {
                return connectionManager.getDataSourceMetaInfo(dataSourceName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void dispose() {
        if (this.manager != null) {
            try {
                manager.dispose();
            } catch (SecurityException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (this.provider != null)
            this.provider.reset();
        this.manager = null;
    }

    public IDataSourceProvider getProvider() {
        if (provider == null) {
            provider = new IerpDataSourceProvider();
        }
        return this.provider;
    }

    public void setProvider(IDataSourceProvider provider) {
        this.provider = provider;
    }

    public void unsetProvider(IDataSourceProvider provider) {
        if (this.provider == provider)
            this.provider = null;
    }
}
