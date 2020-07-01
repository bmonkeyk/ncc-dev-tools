package com.yonyou.uap.studio.connection.ierp;

import com.yonyou.uap.studio.connection.model.DataSourceMetaInfo;
import com.yonyou.uap.studio.connection.provider.IDataSourceProvider;
import nc.uap.plugin.studio.StudioUtil;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;
import nc.uap.plugin.studio.ui.preference.xml.PropXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class IerpDataSourceProvider implements IDataSourceProvider {

    protected static Logger logger = LoggerFactory.getLogger(IerpDataSourceProvider.class.getName());

    private static final String DESIGN_DS_NAME = "design";

    private static final String DRIVER_FOLDER = "/driver";

    private static final String PROP_XML = "/ierp/bin/prop.xml";

    private static final PropXml propxml = new PropXml();

    private static Map<String, DataSourceMeta> uapDatasourceMetas;

    public URL[] getDriverLibraries() {
        File driverFolder = new File(StudioUtil.getNCHome() + "/driver");
        List<URL> results = new ArrayList<URL>();
        Stack<File> stack = new Stack<File>();
        stack.add(driverFolder);
        while (!stack.isEmpty()) {
            File pop = (File) stack.pop();
            if (pop != null) {
                if (pop.isDirectory()) {
                    Collections.addAll(stack, pop.listFiles());
                    continue;
                }
                if (pop.getName().toLowerCase().endsWith(".jar"))
                    try {
                        results.add(pop.toURI().toURL());
                    } catch (MalformedURLException e) {
                        logger.error(e.getMessage(), e);
                    }
            }
        }
        return (URL[]) results.toArray(new URL[0]);
    }

    public DataSourceMetaInfo[] getDataSourceMetas() {
        Map<String, DataSourceMeta> dataSourceMetaMap = getDataSourceMetaMap();
        if (dataSourceMetaMap != null) {
            List<DataSourceMetaInfo> results = new ArrayList<DataSourceMetaInfo>();
            for (DataSourceMeta meta : dataSourceMetaMap.values()) {
                DataSourceMetaInfo info = new DataSourceMetaInfo();
                info.setName(meta.getDataSourceName());
                info.setDriver(meta.getDriverClassName());
                info.setUser(meta.getUser());
                info.setPwd(meta.getPassword());
                info.setUrl(meta.getDatabaseUrl());
                info.setOidMark(meta.getOidMark());
                results.add(info);
            }
            return (DataSourceMetaInfo[]) results.toArray(new DataSourceMetaInfo[0]);
        }
        return new DataSourceMetaInfo[0];
    }

    public String getDefaultDataSourceName() {
        return "design";
    }

    public String getSecondaryDataSourceName() {
        Iterator<DataSourceMeta> iterator = getDataSourceMetaMap()
                .values().iterator();
        while (iterator.hasNext()) {
            DataSourceMeta next = (DataSourceMeta) iterator.next();
            if (next.isBase())
                return next.getDataSourceName();
        }
        return null;
    }

    public String[] getDataSourceNames() {
        Set<String> dataSourceNames = getDataSourceMetaMap().keySet();
        return (String[]) dataSourceNames.toArray(
                new String[getDataSourceMetaMap().keySet().size()]);
    }

    protected static Map<String, DataSourceMeta> getDataSourceMetaMap() {
        if (uapDatasourceMetas == null) {
            uapDatasourceMetas = new HashMap();
            try {
                DataSourceMeta[] datasourceMetas = propxml
                        .getDSMetaWithDesign(StudioUtil.getNCHome() + "/ierp/bin/prop.xml");
                byte b;
                int i;
                DataSourceMeta[] arrayOfDataSourceMeta = new DataSourceMeta[datasourceMetas.length];
                for (b = 0; b < datasourceMetas.length; b++) {
                    DataSourceMeta meta = datasourceMetas[b];
                    uapDatasourceMetas.put(meta.getDataSourceName(), meta);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                uapDatasourceMetas = null;
            }
        }
        return uapDatasourceMetas;
    }

    public void reset() {
        uapDatasourceMetas = null;
    }
}
