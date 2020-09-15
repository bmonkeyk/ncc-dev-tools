package com.yingling.libraries.util;

import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LibrariesScanUtil {

    /**
     * 扫描nchome
     *
     * @return
     */
    public static Map<String, Map<String, Set<String>>> getLibraryJar(String homePath) {
        Map<String, Set<String>> jarMap = new HashMap<>();

        homeScan(homePath, jarMap);

        Map<String, Map<String, Set<String>>> libraryMap = new HashMap<>();
        libraryMap.put("jar", jarMap);

        return libraryMap;
    }

    private static void homeScan(String homePath, Map<String, Set<String>> jarMap) {
        if (StringUtils.isBlank(homePath)) {
            return;
        }
        File homeFile = new File(homePath);
        if (!homeFile.exists()) {
            return;
        }

        //扫描ant目录
        homePath += File.separator;
        String antPath = homePath + "ant";
        Set<String> pathList = scanJarAndClasses(antPath, true);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_ANT, pathList);

        //扫描framework目录
        String frameworkPath = homePath + "framework";
        pathList = scanJarAndClasses(frameworkPath, false);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_FRAMEWORK, pathList);

        //扫描ejb目录
        String ejbPath = homePath + "ejb";
        pathList = scanJarAndClasses(ejbPath, false);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_EJB, pathList);

        //扫描middleware目录
        String middlewarePath = homePath + "middleware";
        pathList = scanJarAndClasses(middlewarePath, false);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_MIDDLEWARE, pathList);

        //扫描lang目录
        String langPath = homePath + "langlib";
        pathList = scanJarAndClasses(langPath, false);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_LANG, pathList);

        //扫描lib目录
        String libPath = homePath + "lib";
        pathList = scanJarAndClasses(libPath, false);
        //扫描external目录
        String externalPath = homePath + "external";
        Set<String> externalSet = scanJarAndClasses(externalPath, true);
        pathList.addAll(externalSet);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_PRODUCT, pathList);

        //扫描hotwebs目录
        String hotwebsPath = homePath + "hotwebs" + File.separator + "nccloud" + File.separator + "WEB-INF" + File.separator;
        //hotweb下非ui类jar包转移到external下
        hotwebEspecial(hotwebsPath,externalPath);

        pathList = scanJarAndClasses(hotwebsPath, true);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_NCCLOUD, pathList);

        //扫描resource
        String resourcePath = homePath + "resources";
        pathList = scanJarAndClasses(resourcePath, false);
        jarMap.put("resources", pathList);

        //扫描modules
        scanModules(homePath, jarMap);
    }

    /**
     * hotweb下非ui类jar包转移到external下
     *
     * @param hotwebsPath
     * @param externalPath
     */
    private static void hotwebEspecial(String hotwebsPath, String externalPath) {
        File hotwebFile = new File(hotwebsPath + File.separator + "lib");
        File externalFile = new File(externalPath + File.separator + "lib");
        for (File file : hotwebFile.listFiles()) {
            if (file.exists() && !file.isDirectory() && file.getName().endsWith(".jar") && !file.getName().startsWith("ui")) {
                try {
                    //jar包复制到external/lib下
                    FileUtil.copy(file, new File(externalFile.getPath() + File.separator + file.getName()));
                    //复制后删除
                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 扫描modules
     *
     * @param homePath
     * @param jarMap
     */
    private static void scanModules(String homePath, Map<String, Set<String>> jarMap) {
        File modulesFile = new File(homePath + "modules");
        if (!modulesFile.exists()) {
            return;
        }

        File[] modules = modulesFile.listFiles();

        Set<String> publicLibrarySet = new HashSet<>();
        Set<String> privateLibrarySet = new HashSet<>();
        Set<String> clientLibrarySet = new HashSet<>();
        for (File module : modules) {
            String modulePath = module.getPath();
            String publicPath = modulePath;
            String clientPath = modulePath + File.separator + "client";
            String privatePath = modulePath + File.separator + "META-INF";
            publicLibrarySet.addAll(scanJarAndClasses(publicPath, true));
            privateLibrarySet.addAll(scanJarAndClasses(privatePath, true));
            clientLibrarySet.addAll(scanJarAndClasses(clientPath, true));
        }
        jarMap.put(ClassPathConstantUtil.PATH_NAME_PUBLIC, publicLibrarySet);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_PRIVATE, privateLibrarySet);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_CLIENT, clientLibrarySet);

    }


    /**
     * 扫描指定目录下的lib 和classes
     *
     * @param basePath
     * @param libFlag
     * @return
     */
    private static Set<String> scanJarAndClasses(String basePath, boolean libFlag) {
        Set<String> pathList = new HashSet<>();
        basePath += File.separator;

        //扫描lib
        String jarPath = basePath;
        if (libFlag) {
            jarPath += "lib";
        }
        File jarFile = new File(jarPath);
        if (jarFile.exists()) {
            pathList.add(jarFile.getPath());
            File [] files = jarFile.listFiles();
            for(File file : files){
                if(file.getName().endsWith("_src.jar")){
                    pathList.add(file.getPath());
                }
            }
        }

        //扫描classes
        String classesPath = basePath + "classes";
        File classesFile = new File(classesPath);
        if (classesFile.exists()) {
            pathList.add(classesFile.getPath());
        }
        return pathList;
    }


}
