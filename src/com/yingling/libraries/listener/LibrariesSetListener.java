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
import com.intellij.openapi.vfs.VirtualFileManager;
import com.pub.exception.BusinessException;
import com.pub.util.ProjectManager;
import com.yingling.libraries.util.ClassPathConstantUtil;
import com.yingling.libraries.util.LibrariesScanUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 设置工程libraries
 */
public class LibrariesSetListener {


    /**
     * 构造方法
     */
    public LibrariesSetListener() {

    }

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
                if (path.endsWith(".jar")) {
                    libraryModel.addRoot(VirtualFileManager.constructUrl("jar", path + "!/"), OrderRootType.CLASSES);
                } else if (path.endsWith("classes") || path.endsWith("resources")) {
                    libraryModel.addRoot(VirtualFileManager.constructUrl("file", path), OrderRootType.CLASSES);
                    libraryModel.addRoot(VirtualFileManager.constructUrl("file", path), OrderRootType.SOURCES);
                } else {
                    libraryModel.addJarDirectory(VirtualFileManager.constructUrl("file", path), false);
                    libraryModel.addRoot(VirtualFileManager.constructUrl("file", path), OrderRootType.SOURCES);
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

}
