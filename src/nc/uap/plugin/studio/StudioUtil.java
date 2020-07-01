package nc.uap.plugin.studio;

import com.yingling.extensions.service.NccEnvSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class StudioUtil {
    protected static Logger logger = LoggerFactory.getLogger(StudioUtil.class.getName());
    private static NccEnvSettingService envSettingService = NccEnvSettingService.getInstance();
    private static final String UAPHOME_FOLDERNAME = "UAP";

    private static final String LANG_FILE = ".lang";

    private static String studioHome;

    public static final String getStudioHome() {
        if (studioHome == null) {
            File file = new File(System.getProperty("user.dir"));
            studioHome = file.getParent();
        }
        return studioHome;
    }

    public static final String getNCHome() {
        return envSettingService.getNcHomePath();
    }

    public static String getDefaultNCHome() {
        return String.valueOf(getStudioHome()) + File.separator + "UAP";
    }

    public static String getStudioLang() {
        return "zh_CN";
    }

    public static String getStudioPersistenceLang() {
        String file = String.valueOf(getStudioHome()) + File.separator + ".lang";
        File langFile = new File(file);
        if (!langFile.exists())
            return null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(langFile));
            return reader.readLine().trim();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException iOException) {
                }
        }
        return null;
    }

    public static boolean setStudioLang(String lang) {
        if (lang == null)
            lang = "en";
        String file = String.valueOf(getStudioHome()) + File.separator + ".lang";
        File langFile = new File(file);
        FileWriter writer = null;
        try {
            writer = new FileWriter(langFile);
            writer.write(lang);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return false;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
        }
        return true;
    }
}
