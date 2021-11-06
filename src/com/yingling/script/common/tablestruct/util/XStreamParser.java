package com.yingling.script.common.tablestruct.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.yingling.script.common.tablestruct.model.InitDataCfg;
import com.yingling.script.common.tablestruct.model.MainTableCfg;
import com.yingling.script.common.tablestruct.model.SubTableCfg;

import java.io.Closeable;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class XStreamParser {

//    protected static Logger logger = LoggerFactory.getLogger(XStreamParser.class.getName());


    private static void closeQuietly(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (Exception exception) {
            }
    }

    public static MainTableCfg getMainTableCfg(VirtualFile file) {
        InputStream input = null;
        try {
            input = file.getInputStream();
            return getMainTableCfg(input);
        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
        } finally {
            closeQuietly(input);
        }
        return null;
    }

    public static MainTableCfg getMainTableCfg(InputStream stream) {
        XStream xstream = new XStream();
        xstream.processAnnotations(new Class[]{MainTableCfg.class,
                SubTableCfg.class});
        try {
            Object obj = xstream.fromXML(stream);
            if (obj != null && obj instanceof MainTableCfg)
                return (MainTableCfg) obj;
        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public static List<InitDataCfg> getInitDataCfgs(VirtualFile file) throws Exception {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.processAnnotations(new Class[]{ItemsXMLRoot.class,
                InitDataCfg.class});
        InputStream contents = null;
        try {
            contents = file.getInputStream();
            Object object = xstream.fromXML(contents);
            if (object instanceof ItemsXMLRoot) {
                ItemsXMLRoot root = (ItemsXMLRoot) object;
                if (root.itemList != null)
                    return root.itemList;
            }
        } catch (Exception e) {
            String message = MessageFormat.format(
                    "Parsing file {0} failed", new Object[]{file.getPath()});
//            logger.error(message + e.getMessage(), e);
            throw e;
        } finally {
            closeQuietly(contents);
        }
        return new ArrayList();
    }

    @XStreamAlias("items")
    static class ItemsXMLRoot {
        @XStreamImplicit
        List<InitDataCfg> itemList;
    }
}
