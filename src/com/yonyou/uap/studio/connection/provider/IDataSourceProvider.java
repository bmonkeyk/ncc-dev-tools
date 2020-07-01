package com.yonyou.uap.studio.connection.provider;

import com.yonyou.uap.studio.connection.model.DataSourceMetaInfo;

import java.net.URL;

public interface IDataSourceProvider {
    URL[] getDriverLibraries();

    DataSourceMetaInfo[] getDataSourceMetas();

    String getDefaultDataSourceName();

    String  getSecondaryDataSourceName();

    String[] getDataSourceNames();

    void reset();
}
