package nc.uap.studio.pub.util;

import org.apache.commons.io.IOUtils;



import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

public class PrintIOUtil {

//    protected static Logger logger = LoggerFactory.getLogger(PrintIOUtil.class.getName());

    private Map<String, List<String>> sqlMap = new HashMap();

    private static PrintIOUtil instance = null;

    public static PrintIOUtil getInstance() {
        if (instance == null)
            instance = new PrintIOUtil();
        return instance;
    }

    public boolean printSQLFile() {
        Set<String> fileNameSet = this.sqlMap.keySet();
        if (fileNameSet == null || fileNameSet.size() == 0)
            return false;
        try {
            for (String fileName : fileNameSet) {
                PrintWriter writer = null;
                List<String> sqlList = (List) this.sqlMap.get(fileName);
                if (sqlList == null || sqlList.size() == 0)
                    continue;
                Collections.sort(sqlList);
                try {
                    writer = new PrintWriter(new OutputStreamWriter(
                            new FileOutputStream(new File(fileName), false),
                            "UTF-8"));
                    for (String sql : sqlList)
                        writer.println(sql);
                } catch (Exception e) {
//                    logger.error(e.getMessage(), e);
                    IOUtils.closeQuietly(writer);
                } finally {
                    IOUtils.closeQuietly(writer);
                }
            }
        } finally {
            this.sqlMap.clear();
        }
        return true;
    }

    public boolean resaveSql(List<String> sqlList, File folder, String mapFileNo) {
        if (sqlList != null && sqlList.size() > 0) {
            String fileName = String.valueOf(folder.getAbsolutePath()) + IOUtils.DIR_SEPARATOR +
                    mapFileNo + ".sql";
            List<String> list = (List) this.sqlMap.get(fileName);
            if (list == null) {
                list = new ArrayList<String>();
                this.sqlMap.put(fileName, list);
            }
            list.addAll(sqlList);
        }
        return true;
    }
}
