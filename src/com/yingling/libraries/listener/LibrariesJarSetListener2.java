package com.yingling.libraries.listener;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.pub.exception.BusinessException;
import com.pub.util.ProjectManager;
import com.yingling.libraries.util.ClassPathConstantUtil;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 设置nc类路径-按目录
 */
public class LibrariesJarSetListener2 {


    public static void setLibraries(String homePath) throws BusinessException {

        //nc类路径
        List<String> ncLibraries = ClassPathConstantUtil.getNCLibrary();

        //当前工程
        Project project = ProjectManager.getInstance().getProject();

        //判断nchome是否存在
        if (StringUtils.isBlank(homePath)) {
            return;
        }
        File homeFile = new File(homePath);
        if (!homeFile.exists()) {
            return;
        }


        //首先创建库
        LibraryTable.ModifiableModel model = LibraryTablesRegistrar.getInstance().getLibraryTable(project).getModifiableModel();
        for (String libraryName : ncLibraries) {
            //根据库名获取库
            LibraryEx library = (LibraryEx) model.getLibraryByName(libraryName);
            // 库不存在创建新的
            if (library == null) {
                library = (LibraryEx) model.createLibrary(libraryName);
            }

            // 库中已有的删除
            LibraryEx.ModifiableModelEx libraryModel = library.getModifiableModel();
            String[] classRootUrls = libraryModel.getUrls(OrderRootType.CLASSES);
            String[] sourcesRootUrls = libraryModel.getUrls(OrderRootType.SOURCES);

            Arrays.stream(classRootUrls).forEach(url -> {
                libraryModel.removeRoot(url, OrderRootType.CLASSES);
            });
            Arrays.stream(sourcesRootUrls).forEach(url -> {
                libraryModel.removeRoot(url, OrderRootType.SOURCES);
            });
        }

        /*扫描 nc home */

        //设置ant
        String antPath = homePath + File.separator + "ant";
        Set<String> antUrl = scanJarAndClasses(antPath, true, false);

        //设置framework
        String frameworkPath = homePath + File.separator + "framework";
        Set<String> frameworkList = scanJarAndClasses(frameworkPath, false, false);

        //设置middleware
        String middlewarePath = homePath + File.separator + "middleware";
        Set<String> middlewareList = scanJarAndClasses(middlewarePath, false, false);

        //扫描lang目录
        String langPath = homePath + File.separator + "langlib";
        Set<String> langList = scanJarAndClasses(langPath, false, false);

        //扫描hotwebs
        String hotwebPath = homePath + File.separator + "hotwebs" + File.separator + "nccloud" + File.separator + "WEB-INF";
        String externalPath = homePath + File.separator + "external";
//        hotwebEspecial(homePath, externalPath);//移动pub_platform到external
        Set<String> nccloudList = scanJarAndClasses(hotwebPath, true, true);

        //扫描lib 和 external
        String libPath = homePath + File.separator + "lib";
        Set<String> libList = scanJarAndClasses(libPath, false, false);
        Set<String> externalList = scanJarAndClasses(externalPath, true, true);
        Set<String> productList = new HashSet<>();
        productList.addAll(libList);
        productList.addAll(externalList);

        //扫描ejb目录
        String ejbPath = homePath + "ejb";
        Set<String> ejbList = scanJarAndClasses(ejbPath, false, false);
        //扫描resource
        String resourcePath = homePath + File.separator + "resources";
        Set<String> resourcesList = new HashSet<>();
        resourcesList.add(resourcePath);

        //扫描modules
        String modulesPath = homePath + File.separator + "modules";
        Map<String, Set<String>> moduleMap = scanModules(modulesPath);
        if (!moduleMap.isEmpty()) {
            for (String key : moduleMap.keySet()) {
                setLibrary(moduleMap.get(key), project, (LibraryEx.ModifiableModelEx) model.getLibraryByName(key).getModifiableModel());
            }
        }
        //设置类路径
        setLibrary(antUrl, project, (LibraryEx.ModifiableModelEx) model.getLibraryByName(ClassPathConstantUtil.PATH_NAME_ANT).getModifiableModel());
        setLibrary(frameworkList, project, (LibraryEx.ModifiableModelEx) model.getLibraryByName(ClassPathConstantUtil.PATH_NAME_FRAMEWORK).getModifiableModel());
        setLibrary(middlewareList, project, (LibraryEx.ModifiableModelEx) model.getLibraryByName(ClassPathConstantUtil.PATH_NAME_MIDDLEWARE).getModifiableModel());
        setLibrary(langList, project, (LibraryEx.ModifiableModelEx) model.getLibraryByName(ClassPathConstantUtil.PATH_NAME_LANG).getModifiableModel());
        setLibrary(productList, project, (LibraryEx.ModifiableModelEx) model.getLibraryByName(ClassPathConstantUtil.PATH_NAME_PRODUCT).getModifiableModel());
        setLibrary(ejbList, project, (LibraryEx.ModifiableModelEx) model.getLibraryByName(ClassPathConstantUtil.PATH_NAME_EJB).getModifiableModel());
        setLibrary(nccloudList, project, (LibraryEx.ModifiableModelEx) model.getLibraryByName(ClassPathConstantUtil.PATH_NAME_NCCLOUD).getModifiableModel());
        setLibrary(resourcesList, project, (LibraryEx.ModifiableModelEx) model.getLibraryByName(ClassPathConstantUtil.PATH_NAME_RESOURCES).getModifiableModel());


        WriteCommandAction.runWriteCommandAction(project, model::commit);
        ProjectManager.getInstance().setAllModuleLibrary();
    }

    /**
     * 设置依赖库
     *
     * @param urlSet
     * @param project
     * @param libraryModel
     */
    private static void setLibrary(Set<String> urlSet, Project project, LibraryEx.ModifiableModelEx libraryModel) {

        for (String url : urlSet) {
            File file = new File(url);
            if(file.exists()){
                if(!file.getName().endsWith("classes") && !file.getName().endsWith("resources")) {//非补丁目录,非resources目录
                    libraryModel.addJarDirectory(VirtualFileManager.constructUrl("file", url), false);
                    libraryModel.addJarDirectory(VirtualFileManager.constructUrl("file", url), false, OrderRootType.SOURCES);
                } else {
                    libraryModel.addRoot(VirtualFileManager.constructUrl("file", url), OrderRootType.CLASSES);
                    libraryModel.addRoot(VirtualFileManager.constructUrl("file", url), OrderRootType.SOURCES);
                }
            }
        }

        // 提交库变更
        WriteCommandAction.runWriteCommandAction(project, libraryModel::commit);
    }


    /**
     * 扫描指定目录下的lib 和classes
     *
     * @param basePath
     * @param libFlag
     * @return
     */
    private static Set<String> scanJarAndClasses(String basePath, boolean libFlag, boolean classFlag) {
        Set<String> pathList = new HashSet<>();
        basePath += File.separator;

        if (classFlag) {
            //扫描classes
            String classesPath = basePath + "classes";
            File classesFile = new File(classesPath);
            if (classesFile.exists()) {
//                Set<String> classSet = new HashSet<>();
//                getClassFiles(classesFile, classSet);
//                pathList.addAll(classSet);
                pathList.add(classesPath);
            }
        }
        //扫描lib
        String jarPath = basePath;
        if (libFlag) {
            jarPath += "lib";
        }
        File jarFile = new File(jarPath);
        if (jarFile.exists()) {
            pathList.add(jarPath);
//            File[] jarFiles = jarFile.listFiles();
//            if (jarFiles != null) {
//                for (File file : jarFiles) {
//                    if (file.getName().endsWith(".jar")) {
//                        pathList.add(file.getPath());
//                    }
//                }
//            }
        }
        return pathList;
    }

    /**
     * 递归查询所有的java和class
     *
     * @param classesFile
     * @param classSet
     */
    private static void getClassFiles(File classesFile, Set<String> classSet) {
        if (classesFile.isDirectory()) {
            File[] files = classesFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    getClassFiles(file, classSet);
                }
            }

        } else {
            if (classesFile.getName().endsWith(".class") || classesFile.getName().endsWith(".java")) {
                classSet.add(classesFile.getPath());
            }
        }
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
        if (!hotwebFile.exists() || !externalFile.exists()) {
            return;
        }
        File[] files = hotwebFile.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
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

    private static Map<String, Set<String>> scanModules(String modulesPath) {

        Map<String, Set<String>> jarMap = new HashMap<>();
        File modulesFile = new File(modulesPath);
        File[] modules = modulesFile.listFiles();

        if (modules == null) {
            return jarMap;
        }
        Set<String> publicLibrarySet = new HashSet<>();
        Set<String> privateLibrarySet = new HashSet<>();
        Set<String> clientLibrarySet = new HashSet<>();
        for (File module : modules) {
            String modulePath = module.getPath();
            String publicPath = modulePath;
            String clientPath = modulePath + File.separator + "client";
            String privatePath = modulePath + File.separator + "META-INF";
            publicLibrarySet.addAll(scanJarAndClasses(publicPath, true, true));
            privateLibrarySet.addAll(scanJarAndClasses(privatePath, true, true));
            clientLibrarySet.addAll(scanJarAndClasses(clientPath, true, true));
        }
        jarMap.put(ClassPathConstantUtil.PATH_NAME_PUBLIC, publicLibrarySet);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_PRIVATE, privateLibrarySet);
        jarMap.put(ClassPathConstantUtil.PATH_NAME_CLIENT, clientLibrarySet);

        return jarMap;
    }
}