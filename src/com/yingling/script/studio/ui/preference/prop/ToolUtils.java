package com.yingling.script.studio.ui.preference.prop;

import nc.uap.plugin.studio.ui.preference.rsa.Encode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolUtils {
    private static Encode encode = new Encode();

    public static boolean isNumber(String str) {
        return str.matches("\\d+");
    }

    public static boolean isSnNumber(String str) {
        return str.matches("(?m)^3\\d{7}$");
    }

    public static boolean checkIPAddress(String ipAddress) {
        return ipAddress
                .matches("(?m)^(0|[1-9]\\d?|[0-1]\\d{2}|2[0-4]\\d|25[0-5])\\.(0|[1-9]\\d?|[0-1]\\d{2}|2[0-4]\\d|25[0-5])\\.(0|[1-9]\\d?|[0-1]\\d{2}|2[0-4]\\d|25[0-5])\\.(0|[1-9]\\d?|[0-1]\\d{2}|2[0-4]\\d|25[0-5])$");
    }

    public static boolean is_2length_azAZ09(String str) {
        return str.matches("[YZ][A-Z0-9]");
    }

    public static boolean isChinese(String str) {
        return str.matches("[\\u4e00-\\u9fa5]");
    }

    public static String[] getJDBCInfo(String url) {
        String[] jdbc = new String[3];
        Pattern regex = Pattern.compile("(?<=(?://|@))([^:;]+)(?::((?<=:)\\d+))?.*?[=:/]([^<;]+).*", 128);
        Matcher matcher = regex.matcher(url);
        if (matcher.find()) {
            jdbc[0] = matcher.group(1);
            jdbc[1] = (matcher.group(2) != null) ? matcher.group(2) : "";
            jdbc[2] = matcher.group(3);
        }
        return jdbc;
    }

    public static boolean isJDBCUrl(String url) {
        Pattern regex = Pattern.compile("^[^:]+:[^:]+:[^:]+$", 136);
        Matcher matcher = regex.matcher(url);
        return !matcher.find();
    }

    public static String getJDBCUrl(String example, String database, String host, String port) {
        StringBuffer url = new StringBuffer();
        Pattern regex = Pattern.compile("(.*)(?<=(?://|@))([^:;]+)(:(?<=:)\\d+)?(.*?[=:/])([^<;]+)(.*)", 128);
        Matcher matcher = regex.matcher(example);
        if (matcher.find()) {
            url.append(matcher.group(1));
            url.append(host);
            if (matcher.group(3) != null)
                if (!"".equals(port)) {
                    url.append(":");
                    url.append(port);
                } else {
                    url.append(matcher.group(3));
                }
            url.append(matcher.group(4));
            url.append(database);
            url.append(matcher.group(6));
        }
        return url.toString();
    }

    public static String getODBCDBName(String url) {
        Pattern regex = Pattern.compile(".*:([^:]+)", 128);
        Matcher matcher = regex.matcher(url);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    public static String getODBCUrl(String example, String database) {
        StringBuffer url = new StringBuffer();
        Pattern regex = Pattern.compile("(.*:)([^:]+)", 128);
        Matcher matcher = regex.matcher(example);
        if (matcher.find()) {
            url.append(matcher.group(1));
            url.append(database);
        }
        return url.toString();
    }

    public static Encode getEncode() {
        return encode;
    }
}
