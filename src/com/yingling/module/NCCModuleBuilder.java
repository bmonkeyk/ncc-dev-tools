package com.yingling.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.base.BusinessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ncc 模块创建器
 * 用于将含有src的目录转为依赖nc的可调试java module
 */
public class NCCModuleBuilder extends ModuleBuilder {
    @Override
    public ModuleType<?> getModuleType() {
        return StdModuleTypes.JAVA;
    }

    private List<Pair<String, String>> mySourcePaths;

    private Library[] libraries;

    private String myCompilerOutputPath;


    @Override
    public @Nullable
    List<Module> commit(@NotNull Project project, ModifiableModuleModel model, ModulesProvider modulesProvider) {
        if (project == null) {
            //todo 这里回头定义一个报错
            return null;
        }

        LanguageLevelProjectExtension extension = LanguageLevelProjectExtension.getInstance(ProjectManager.getInstance().getDefaultProject());
        Boolean aDefault = extension.getDefault();
        LanguageLevelProjectExtension instance = LanguageLevelProjectExtension.getInstance(project);
        if (aDefault != null && !aDefault) {
            instance.setLanguageLevel(extension.getLanguageLevel());
        } else {
            Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
            if (sdk != null) {
                JavaSdkVersion version = JavaSdk.getInstance().getVersion(sdk);
                if (version != null) {
                    instance.setLanguageLevel(version.getMaxLanguageLevel());
                    instance.setDefault(true);
                }
            }
        }

        return super.commit(project, model, modulesProvider);
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel rootModel) throws ConfigurationException {
        if (rootModel == null) {
            return;//todo 这里扔一个报错
        }

        //设置jdk
        CompilerModuleExtension compilerModuleExtension = rootModel.getModuleExtension(CompilerModuleExtension.class);
        compilerModuleExtension.setExcludeOutput(true);
        if (this.myJdk != null) {
            rootModel.setSdk(this.myJdk);
        } else {
            rootModel.inheritSdk();
        }

        //设置source目录
        ContentEntry contentEntry = this.doAddContentEntry(rootModel);
        if (contentEntry != null) {
            List<Pair<String, String>> sourcePaths = this.getSourcePaths();
            if (sourcePaths != null) {
                for (Pair<String, String> path : sourcePaths) {
                    String moduleLibraryPath = path.getFirst();
                    VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(FileUtil.toSystemIndependentName(moduleLibraryPath));
                    if (sourceRoot != null) {
                        contentEntry.addSourceFolder(sourceRoot, false, path.getSecond());
                    }
                }
            }
        }

        //设置输出目录
        if (this.myCompilerOutputPath != null) {
            String canonicalPath;
            try {
                canonicalPath = FileUtil.resolveShortWindowsName(this.myCompilerOutputPath);
            } catch (IOException var11) {
                canonicalPath = this.myCompilerOutputPath;
            }

            compilerModuleExtension.setCompilerOutputPath(VfsUtilCore.pathToUrl(canonicalPath));
        } else {
            compilerModuleExtension.inheritCompilerOutputPath(true);
        }

        //设置依赖

        if (this.libraries != null) {
            for (Library library : libraries) {
                rootModel.addLibraryEntry(library);
            }
        }
//        LibraryTable libraryTable = rootModel.getModuleLibraryTable();
//        Library library = libraryTable.createLibrary();
//        Library.ModifiableModel modifiableModel = library.getModifiableModel();
//        for (Iterator<Pair<String, String>> item = this.myModuleLibraries.iterator(); item.hasNext(); modifiableModel.commit()) {
//            Pair<String, String> path = item.next();
//            String moduleLibraryPath = path.getFirst();
//            String sourceLibraryPath = path.getSecond();
//            modifiableModel.addRoot(getUrlByPath(moduleLibraryPath), OrderRootType.CLASSES);
//            if (StringUtils.isNotBlank(sourceLibraryPath)) {
//                modifiableModel.addRoot(getUrlByPath(sourceLibraryPath), OrderRootType.SOURCES);
//            }
//        }

    }

    public List<Pair<String, String>> getSourcePaths() {
        if (this.mySourcePaths == null) {
            List<Pair<String, String>> paths = new ArrayList();
            String entryPath = this.getContentEntryPath();
            String path = entryPath + File.separator + "src";
            (new File(path)).mkdirs();
            paths.add(Pair.create(path, ""));
            return paths;
        } else {
            return this.mySourcePaths;
        }
    }

    public void setSourcePaths(List<Pair<String, String>> sourcePaths) {
        this.mySourcePaths = sourcePaths != null ? new ArrayList(sourcePaths) : null;
    }

    public void addSourcePath(Pair<String, String> sourcePathInfo) {
        this.mySourcePaths.add(sourcePathInfo);
    }

    public void setLibraries(Library[] libraries) {
        if (libraries == null) {
            try {
                libraries = com.yingling.base.ProjectManager.getInstance().getProjectLibraries(com.yingling.base.ProjectManager.getInstance().getProject());
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }
        this.libraries = libraries;
    }

    public final void setCompilerOutputPath(String compilerOutputPath) {
        this.myCompilerOutputPath = acceptParameter(compilerOutputPath);
    }

    private static String getUrlByPath(String path) {
        return VfsUtil.getUrlForLibraryRoot(new File(path));
    }

}
