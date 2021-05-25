package nc.uap.studio.pub.db.script.init;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;



import java.io.*;
import java.util.Properties;

public class DbRecordSqlFileCfg {
//    protected static Logger logger = LoggerFactory.getLogger(DbRecordSqlFileCfg.class.getName());
    public static final String MODULE_QUERYENGINE_FILE_NAME = "QE.properties";

    public static final String MODULE_FREEREPORT_FILE_NAME = "FR.properties";

    public static final String CUSTOM_QUERY = "自定义查询";

    public static final String FREE_REPORT = "自由报表";

    private static final String BUSINESS_DIR_NAME = "business";

    private static final String FORMAT_MD_ENABLE_ATTR = "bFmd";

    private static final String FORMAT_MD_ID_ATTR = "fid";

    private static final String QUERY_MD_ENABLE_ATTR = "bQmd";

    private static final String QUERY_MD_ID_ATTR = "qid";

    private static final String ENABLE = "Y";

    private String rootDir;

    private String commonMapPath;

    private String moduleMapPath;

    private String moduleQueryEngineFilePath;

    private String moduleFreeReportFilePath;

    private String oidMarkRuleFilePath;

    private String department;

    private Properties commonProp;

    private Properties moduleProp;

    private Properties qeProp;

    private Properties frProp;

    private Object obj;

    public DbRecordSqlFileCfg(String rootDir, String commonMapPath, String moduleMapPath) {
        this.obj = new Object();
        this.rootDir = rootDir;
        this.commonMapPath = commonMapPath;
        this.moduleMapPath = moduleMapPath;
    }

    public String getAbsolutePath(String tableName) {
        if (this.commonProp == null)
            synchronized (this.obj) {
                if (this.commonProp == null) {
                    this.commonProp = getProperties(this.commonMapPath);
                    this.moduleProp = getProperties(this.moduleMapPath);
                }
            }
        String commonMapValue = this.commonProp.getProperty(tableName);
        if (StringUtils.isNotBlank(commonMapValue))
            return this.rootDir + File.separator +
                    commonMapValue.trim() + File.separator + tableName;
        String moduleMapValue = this.moduleProp.getProperty(tableName);
        if (StringUtils.isNotBlank(moduleMapValue))
            return this.rootDir + File.separator + "business" +
                    File.separator + moduleMapValue.trim() + File.separator + tableName;
        return null;
    }

    public boolean isFmdEnabledOfFR() {
        return "Y".equalsIgnoreCase(getFRProperty("bFmd"));
    }

    public boolean isQmdEnabledOfFR() {
        return "Y".equalsIgnoreCase(getFRProperty("bQmd"));
    }

    public String getFidOfFR() {
        return getFRProperty("fid");
    }

    public String getQidOfFR() {
        return getFRProperty("qid");
    }

    public boolean isFmdEnabledOfQE() {
        return "Y".equalsIgnoreCase(getQEProperty("bFmd"));
    }

    public boolean isQmdEnabledOfQE() {
        return "Y".equalsIgnoreCase(getQEProperty("bQmd"));
    }

    public String getFidOfQE() {
        return getQEProperty("fid");
    }

    public String getQidOfQE() {
        return getQEProperty("qid");
    }

    private String getQEProperty(String key) {
        if (this.moduleQueryEngineFilePath == null)
            return null;
        if (this.qeProp == null)
            synchronized (this.obj) {
                if (this.qeProp == null)
                    this.qeProp = getProperties(this.moduleQueryEngineFilePath);
            }
        return this.qeProp.getProperty(key);
    }

    private String getFRProperty(String key) {
        if (this.moduleFreeReportFilePath == null)
            return null;
        if (this.frProp == null)
            synchronized (this.obj) {
                if (this.frProp == null)
                    this.frProp = getProperties(this.moduleFreeReportFilePath);
            }
        return this.frProp.getProperty(key);
    }

    private Properties getProperties(String fileName) {
        Properties prop = null;
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(is);
        } catch (FileNotFoundException e) {
//            logger.error("文件" + fileName + "不存在。", e);
            throw new RuntimeException("文件" + fileName + "不存在。");
        } catch (IOException e) {
//            logger.error("读取文件" + fileName + "失败。", e);
            throw new RuntimeException("读取文件" + fileName + "失败。");
        } finally {
            IOUtils.closeQuietly(is);
        }
        return prop;
    }

    public String getModuleQueryEngineFilePath() {
        return this.moduleQueryEngineFilePath;
    }

    public void setModuleQueryEngineFilePath(String moduleQueryEngineFilePath) {
        this.moduleQueryEngineFilePath = moduleQueryEngineFilePath;
    }

    public String getOidMarkRuleFilePath() {
        return this.oidMarkRuleFilePath;
    }

    public void setOidMarkRuleFilePath(String oidMarkRuleFilePath) {
        this.oidMarkRuleFilePath = oidMarkRuleFilePath;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getModuleFreeReportFilePath() {
        return this.moduleFreeReportFilePath;
    }

    public void setModuleFreeReportFilePath(String moduleFreeReportFilePath) {
        this.moduleFreeReportFilePath = moduleFreeReportFilePath;
    }
}
