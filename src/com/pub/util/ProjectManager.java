package com.pub.util;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.pub.exception.BusinessException;
import com.yingling.libraries.util.ClassPathConstantUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 工程管理器
 *
 * @author liuchao
 */
public class ProjectManager {

    public static ProjectManager getInstance() {
        return new ProjectManager();
    }

    /**
     * 获取当前project
     *
     * @return
     */
    public Project getProject() {
        DataKey key = CommonDataKeys.PROJECT;
        DataManager dataManager = DataManager.getInstance();
//        Intrinsics.checkExpressionValueIsNotNull(dataManager, "DataManager.getInstance()");
        Project project = (Project) key.getData(dataManager.getDataContext());
        return project;
    }

    /**
     * 获取当前project下所有module
     *
     * @return
     */
    public Module[] getAllModule(Project project) {
        if (project == null) {
            project = getProject();
        }
        return ModuleManager.getInstance(project).getModules();
    }

    /**
     * 根据moduleName获得module
     *
     * @param moduleName
     * @return
     */
    public Module getModule(String moduleName) {
        Project project = getProject();
        return ModuleManager.getInstance(project).findModuleByName(moduleName);
    }

    /**
     * 获取工程library
     *
     * @param project
     * @return
     */
    public Library[] getProjectLibraries(Project project) throws BusinessException {
        List<String> list = ClassPathConstantUtil.getNCLibrary();
        List<Library> libraries = new ArrayList<>();
        LibraryTable libraryTable = getLibraryTable(project);
        for (String libName : list) {
            Library library = libraryTable.getLibraryByName(libName);
            if (library == null) {
                throw new BusinessException("class path : \"" + libName + "\" is not exist\nplease set project's Libraries");
            }
            libraries.add(library);
        }
        return libraries.toArray(new Library[0]);
    }

    /**
     * 获取LibraryTable
     *
     * @param project
     * @return
     */
    private LibraryTable getLibraryTable(Project project) {
        if (project == null) {
            project = getProject();
        }
        LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        return libraryTable;
    }

    /**
     * 设置module的library
     *
     * @param module
     */
    public void setModuleLibrary(Project project,Module module) throws BusinessException {
        Library[] libraries = getProjectLibraries(project);
        if (libraries == null || libraries.length == 0) {
            throw new BusinessException("this project is not set libraries!");
        }
        for (Library library : libraries) {
            if (ModuleRootManager.getInstance(module).getModifiableModel().findLibraryOrderEntry(library) == null) {
                ModuleRootModificationUtil.addDependency(module, library);
            }
        }
    }

    public void setAllModuleLibrary() throws BusinessException {
        Project project = getProject();
        Module[] modules = getAllModule(project);
        Library[] libraries = getProjectLibraries(project);
        for (Module module : modules) {
            if (module.getModuleFile() == null) {
                continue;
            }
            String modulePath = module.getModuleFile().getParent().getPath();
            //通过module.xml文件是否存在判定是不是nc module,只个nc的module设置类路径
            if (new File(modulePath + File.separator + "META-INF" + File.separator + "module.xml").exists()) {
                for (Library library : libraries) {
                    if (ModuleRootManager.getInstance(module).getModifiableModel().findLibraryOrderEntry(library) == null) {
                        ModuleRootModificationUtil.addDependency(module, library);
                    }
                }
            } else {
                //去除非nc module上的nc类路径，暂时先不实现（没找到工具类）
            }
        }
    }
}
