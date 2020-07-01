package com.yingling.util;

/**
 * 判断是系统是否是mac
 */
public class SystemUtil {
    public static boolean isMac(){
        String osName = System.getProperties().getProperty("os.name");
        return !osName.toLowerCase().startsWith("win");
    }
}
