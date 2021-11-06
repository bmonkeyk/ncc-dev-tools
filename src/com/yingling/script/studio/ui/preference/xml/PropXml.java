package com.yingling.script.studio.ui.preference.xml;

import com.yingling.script.studio.ui.preference.prop.DataSourceMeta;
import com.yingling.script.studio.ui.preference.prop.PropInfo;
import com.yingling.script.studio.ui.preference.dbdriver.DatabaseDriverSetInfo;

import java.io.File;

public class PropXml {
    private String xmlPath = "/bin/dbdriverset.xml";

    private String xmlPath2 = "/ierp/bin/dbdriverset.xml";

    public PropInfo loadPropInfo(String propfile) throws Exception {
        return (PropInfo) XMLToObject.getJavaObjectFromFile(propfile,
                PropInfo.class, true);
    }

    public DataSourceMeta[] getDSMetaWithDesign(String propfile) throws Exception {
        DataSourceMeta[] metas = loadPropInfo(propfile).getDataSource();
        for (int i = 0; i < metas.length; i++) {
            DataSourceMeta meta = metas[i];
            if ("design".equals(meta.getDataSourceName())) {
                if (i != 0) {
                    DataSourceMeta tmp = metas[i];
                    metas[i] = metas[0];
                    metas[0] = tmp;
                }
                return metas;
            }
        }
        DataSourceMeta[] metaswithdesign = new DataSourceMeta[metas.length + 1];
        System.arraycopy(metas, 0, metaswithdesign, 1, metas.length);
        metaswithdesign[0] = new DataSourceMeta();
        return metaswithdesign;
    }

    public void saveMeta(String nchome, DataSourceMeta[] metas) throws Exception {
        PropInfo propinfo = loadPropInfo(nchome);
        propinfo.setDataSource(metas);
        storePorpInfo(nchome, propinfo);
    }

    private void storePorpInfo(String propfile, PropInfo propInfo) throws Exception {
        ObjectToXML.saveAsXmlFile(propfile, propInfo);
    }

    public DatabaseDriverSetInfo getDriverSet(String nchome) throws Exception {
        String fileName = String.valueOf(nchome) + this.xmlPath2;
        File file = new File(fileName);
        if (!file.exists())
            file = new File(String.valueOf(nchome) + this.xmlPath);
        if (!file.exists())
            throw new IllegalArgumentException("Configuration file not found");
        return (DatabaseDriverSetInfo) XMLToObject.getJavaObjectFromFile(file,
                DatabaseDriverSetInfo.class, true);
    }

}
