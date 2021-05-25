package nc.uap.studio.pub.db.script.init.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;



import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

@XStreamAlias("oidMarkRule")
public class OidMarkRule {
//    protected static Logger logger = LoggerFactory.getLogger(OidMarkRule.class.getName());
    @XStreamImplicit
    private List<OidMarkMap> oidMarkMaps;

    @XStreamAlias("table")
    @XStreamImplicit
    private List<String> tables;

    private File file;

    private long lastModified;

    private Map<String, String> deptOidMarkMap;

    private static Map<String, OidMarkRule> instanceMap = new HashMap();

    public static OidMarkRule getInstance(String oidMarkRuleFileName) {
        if (StringUtils.isBlank(oidMarkRuleFileName))
            return null;
        File file = new File(oidMarkRuleFileName);
        oidMarkRuleFileName = file.getPath();
        OidMarkRule rule = (OidMarkRule) instanceMap.get(oidMarkRuleFileName);
        if (rule == null) {
            rule = parseOIDMarkRule(file);
            if (rule != null)
                instanceMap.put(oidMarkRuleFileName, rule);
        } else {
            long lastModified = rule.file.lastModified();
            if (lastModified == 0L) {
                instanceMap.remove(oidMarkRuleFileName);
                return null;
            }
            if (lastModified > rule.lastModified)
                instanceMap.put(oidMarkRuleFileName, parseOIDMarkRule(file));
        }
        return (OidMarkRule) instanceMap.get(oidMarkRuleFileName);
    }

    public List<String> getTables() {
        return this.tables;
    }

    public Map<String, String> getDeptOidMarkMap() {
        return this.deptOidMarkMap;
    }

    private static OidMarkRule parseOIDMarkRule(File ruleCfgfile) {
        XStream xstream = new XStream();
        List<Class<?>> clazzs = new ArrayList<Class<?>>();
        clazzs.add(OidMarkRule.class);
        clazzs.add(OidMarkMap.class);
        Annotations.configureAliases(xstream, (Class[]) clazzs.toArray(new Class[0]));
        InputStreamReader reader = null;
        OidMarkRule rule = null;
        long lastModified = ruleCfgfile.lastModified();
        try {
            reader = new InputStreamReader(new FileInputStream(ruleCfgfile), "UTF-8");
            rule = (OidMarkRule) xstream.fromXML(reader);
        } catch (Exception e) {
//            logger.error("Failed to parse oidmark with version path: (" + ruleCfgfile.getPath() + ")", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        if (rule != null) {
            rule.file = ruleCfgfile;
            rule.lastModified = lastModified;
            cvtOIDMarkRule(rule);
        }
        return rule;
    }

    private static void cvtOIDMarkRule(OidMarkRule rule) {
        if (rule.tables != null) {
            rule.tables = Collections.unmodifiableList(rule.tables);
        } else {
            rule.tables = Collections.unmodifiableList(Collections.EMPTY_LIST);
        }
        Map<String, String> deptOidMapTemp = new HashMap<String, String>();
        if (rule.oidMarkMaps != null && !rule.oidMarkMaps.isEmpty())
            for (OidMarkMap oidMarkMap : rule.oidMarkMaps)
                deptOidMapTemp.put(oidMarkMap.getDepartment(), oidMarkMap.getOidMark());
        rule.deptOidMarkMap = Collections.unmodifiableMap(deptOidMapTemp);
    }

    @XStreamAlias("oidMarkMap")
    static class OidMarkMap {
        private String department;

        private String oidMark;

        public String getDepartment() {
            return this.department;
        }

        public String getOidMark() {
            return this.oidMark;
        }

        public String toString() {
            return String.valueOf(this.department) + ":" + this.oidMark;
        }
    }
}
