package com.yingling.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * nc类路径定义
 */
public class ClassPathConstantUtil {

    public static final String PATH_NAME_ANT = "Ant_Library";
    public static final String PATH_NAME_PRODUCT = "Product_Common_Library";
    public static final String PATH_NAME_MIDDLEWARE = "Middleware_Library";
    public static final String PATH_NAME_FRAMEWORK = "Framework_Library";
    public static final String PATH_NAME_PUBLIC = "Module_Public_Library";
    public static final String PATH_NAME_CLIENT = "Module_Client_Library";
    public static final String PATH_NAME_PRIVATE = "Module_Private_Library";
    public static final String PATH_NAME_LANG = "Module_Lang_Library";
    public static final String PATH_NAME_NCCLOUD = "NCCloud_Library";
    public static final String PATH_NAME_EJB = "Generated_EJB";
    public static final String PATH_NAME_RESOURCES = "resources";

    public static Map<String, String> libPathMap = new HashMap<>();

    /**
     * nc 类路径
     * 顺序不可更改
     *
     * @return
     */
    public static List<String> getNCLibrary() {
        List<String> ncLibraries = new ArrayList<>();
        ncLibraries.add(PATH_NAME_ANT);
        ncLibraries.add(PATH_NAME_PRODUCT);
        ncLibraries.add(PATH_NAME_MIDDLEWARE);
        ncLibraries.add(PATH_NAME_FRAMEWORK);
        ncLibraries.add(PATH_NAME_PUBLIC);
        ncLibraries.add(PATH_NAME_CLIENT);
        ncLibraries.add(PATH_NAME_PRIVATE);
        ncLibraries.add(PATH_NAME_LANG);
        ncLibraries.add(PATH_NAME_NCCLOUD);
        ncLibraries.add(PATH_NAME_EJB);
        ncLibraries.add(PATH_NAME_RESOURCES);

        return ncLibraries;
    }
}
