package com.yingling.libraries.listener;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.pub.exception.BusinessException;
import com.pub.util.ProjectManager;
import com.yingling.libraries.util.ClassPathConstantUtil;
import com.yingling.libraries.util.LibrariesScanUtil;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.*;

public class LibrariesPathSetListener {
    /**
     * 设置library
     *
     * @param homePath
     */
    public static void setLibraries(String homePath) throws BusinessException {

        //nc类路径
        List<String> ncLibraries = ClassPathConstantUtil.getNCLibrary();

        //当前工程
        Project project = ProjectManager.getInstance().getProject();

        //扫描nchome
        Map<String, Map<String, Set<String>>> libraryMap = LibrariesScanUtil.getLibraryJar(homePath);

        //获取classpath
        Map<String, Set<String>> jarMap = libraryMap.get("jar");

        if (jarMap == null || jarMap.isEmpty()) {
            throw new BusinessException("please set nchome");
        }

        //当前工程lib列表
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
            for (String classRootURL : classRootUrls) {
                libraryModel.removeRoot(classRootURL, OrderRootType.CLASSES);
            }
            // 加入新的classpath
            Set<String> pathList = jarMap.get(libraryName);
            for (String path : pathList) {
                // 注意jar格式jar:{path_to_jar}.jar!/
                if (path.endsWith("_src.jar")) {
                    libraryModel.addRoot(VirtualFileManager.constructUrl("jar", path + "!/"), OrderRootType.SOURCES);
                } else if (path.endsWith("classes") || path.endsWith("resources")) {
                    libraryModel.addRoot(VirtualFileManager.constructUrl("file", path), OrderRootType.CLASSES);
                    libraryModel.addRoot(VirtualFileManager.constructUrl("file", path), OrderRootType.SOURCES);
                } else {
                    libraryModel.addJarDirectory(VirtualFileManager.constructUrl("file", path), false);
//                    libraryModel.addRoot(VirtualFileManager.constructUrl("file", path), OrderRootType.SOURCES);
                }
            }

            // 提交库变更
            WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                @Override
                public void run() {
                    libraryModel.commit();
                }
            });
            // 向项目模块依赖中增加新增的库
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module module : modules) {
                if (module.getModuleFile() == null) {
                    continue;
                }
                String modulePath = module.getModuleFile().getParent().getPath();
                //通过module.xml文件是否存在判定是不是nc module,只个nc的module设置类路径
                if (new File(modulePath + File.separator + "META-INF" + File.separator + "module.xml").exists()) {
                    if (ModuleRootManager.getInstance(module).getModifiableModel().findLibraryOrderEntry(library) == null) {
                        ModuleRootModificationUtil.addDependency(module, library);
                    }
                } else {
                    //去除非nc module上的nc类路径，暂时先不实现（没找到工具类）
                }
            }
        }

        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                model.commit();
            }
        });
    }


    /**
     * 设置依赖库
     *
     * @param urlSet
     * @param project
     * @param libraryModel
     */
    private static void setLibrary(List<String> urlSet, Project project, LibraryEx.ModifiableModelEx libraryModel) {

        for (String url : urlSet) {
            File file = new File(url);
            if (file.exists()) {
                if (file.isDirectory()) {
                    libraryModel.addRoot(VirtualFileManager.constructUrl("file", url), OrderRootType.CLASSES);
                } else {
                    String classFileUrl = null;
                    String jarFileUrl = null;
                    OrderRootType orderRootType;
                    if (url.endsWith("_src.jar")) {
                        jarFileUrl = VirtualFileManager.constructUrl("jar", url + "!/");
                        orderRootType = OrderRootType.SOURCES;
                    } else if (url.endsWith(".jar")) {
                        jarFileUrl = VirtualFileManager.constructUrl("jar", url + "!/");
                        orderRootType = OrderRootType.CLASSES;
                    } else if (url.endsWith(".java")) {
                        String classPath = url.split("classes")[0] + "classes";
                        orderRootType = OrderRootType.SOURCES;
                        classFileUrl = VirtualFileManager.constructUrl("file", classPath) + orderRootType.toString();
                    } else if (url.endsWith(".class")) {
                        String classPath = url.split("classes")[0] + "classes";
                        orderRootType = OrderRootType.CLASSES;
                        classFileUrl = VirtualFileManager.constructUrl("file", classPath) + orderRootType.toString();
                    } else {
                        continue;
                    }

                    VirtualFile virtualFile = null;
                    if (StringUtils.isNotBlank(jarFileUrl)) {
                        virtualFile = VirtualFileManager.getInstance().findFileByUrl(jarFileUrl);
                    } else if (StringUtils.isNotBlank(classFileUrl)) {
                        virtualFile = VirtualFileManager.getInstance().findFileByUrl(classFileUrl.split("Root")[0]);

                    }
                    if (virtualFile != null) {
                        libraryModel.addRoot(virtualFile, orderRootType);
                    }
                }
            }

        }

        // 提交库变更
        WriteCommandAction.runWriteCommandAction(project, libraryModel::commit);
    }


}
