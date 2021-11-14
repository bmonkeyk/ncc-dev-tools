package com.yingling.module.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.base.BusinessException;
import com.yingling.base.ProjectManager;
import com.yingling.module.NCCModuleBuilder;
import com.yingling.module.NCCModuleType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModuleUtil {


    /**
     * 普通java模块
     **/
    public final static int MODULE_TYPE_JAVA = 0;
    /**
     * nc模块
     **/
    public final static int MODULE_TYPE_NC = 1;
    /**
     * maven模块
     **/
    public final static int MODULE_TYPE_MAVEN = 2;

    /**
     * 选中目录转换为module
     */
    public void coverToModule(Project project, String filePath) throws BusinessException {

        File file = new File(filePath);
        String modulePath = filePath;
        String moduleFileName = getModuleFileName(file);

        //0 是常规java模块，1是nc模块，2是maven模块
        int moduleType = MODULE_TYPE_JAVA;
        if (moduleFileName.startsWith("nc_")) {
            moduleFileName = moduleFileName.substring(3);
            moduleType = MODULE_TYPE_NC;
        } else if (moduleFileName.startsWith("maven_")) {
            moduleFileName = moduleFileName.substring(6);
            moduleType = MODULE_TYPE_MAVEN;
        } else {

        }

        Library[] libraries = ProjectManager.getInstance().getProjectLibraries(project);
        Module module = ProjectManager.getInstance().getModule(file.getName());
        if (module == null) {
            //创建module
            NCCModuleBuilder builder = new NCCModuleType().createModuleBuilder();
            builder.setModuleFilePath(modulePath + File.separator + moduleFileName);
            builder.setContentEntryPath(modulePath);
            builder.setName(file.getName());
            List<Pair<String, String>> list = getSourcePathList(moduleType, modulePath);
            builder.setSourcePaths(list);
            builder.setLibraries(libraries);
            builder.commitModule(project, null);
        }
//        else {
//            ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(module).getModifiableModel();
//            if(moduleType == MODULE_TYPE_MAVEN){
//                VirtualFile out = CompilerProjectExtension.getInstance(project).getCompilerOutput();
//                modifiableModel.getModuleExtension(CompilerModuleExtension.class).setCompilerOutputPath(out.getPath());
//            } else if(moduleType == MODULE_TYPE_NC){
//                ContentEntry contentEntry = modifiableModel.getContentEntries()[0];
//                List<Pair<String, String>> list = getSourcePathList(moduleType, modulePath);
//                for (Pair<String, String> str : list) {
//                    VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(FileUtil.toSystemIndependentName(str.getFirst()));
//                    contentEntry.addSourceFolder(sourceRoot, false);
//                }
//                Library[] libs = modifiableModel.getModuleLibraryTable().getLibraries();
//                if (libs == null || libs.length < libraries.length) {
//                    for (Library lib : libs) {
//                        modifiableModel.getModuleLibraryTable().removeLibrary(lib);
//                    }
//                    modifiableModel.commit();
//                    for (Library lib : libraries) {
//                        modifiableModel.addLibraryEntry(lib);
//                    }
//                    modifiableModel.commit();
//                }
//            }
//        }
    }

    /**
     * 扫描source目录
     *
     * @param moduleType
     * @param modulePath
     * @return
     */
    private List<Pair<String, String>> getSourcePathList(int moduleType, String modulePath) {
        List<Pair<String, String>> list = new ArrayList<>();
        switch (moduleType) {
            case MODULE_TYPE_NC:
                list = scanNCSourcePath(modulePath);
                break;
            case MODULE_TYPE_MAVEN:
                list.add(new Pair<>(modulePath + "/src/main/java", ""));
                break;
            default:
                list.add(new Pair<>(modulePath + "/src", ""));
        }
        return list;
    }

    private List<Pair<String, String>> scanNCSourcePath(String modulePath) {
        List<Pair<String, String>> list = new ArrayList();
        File moduleFile = new File(modulePath);
        for (File componentFile : moduleFile.listFiles()) {
            if (componentFile.isFile()) {
                continue;
            }
            File file = new File(componentFile.getPath() + File.separator + "component.xml");
            //如果模块下边组件文件存在
            if (file.exists()) {
                File srcFile = new File(file.getParent() + File.separator + "src");
                if (srcFile.exists()) {
                    for (File f : srcFile.listFiles()) {
                        if (f.getName().equals("client") || f.getName().equals("public") || f.getName().equals("private")) {
                            list.add(new Pair<>(f.getPath(), ""));
                        }
                    }
                }
            }
        }
        return list;
    }


    private String getModuleFileName(File file) {

        String moduleName = file.getName();
        String path = file.getPath();
        String ncModulePath = path + File.separator + "META-INF" + File.separator + "module.xml";
        String mavenModulePath = path + File.separator + "pom.xml";

        try {
            File mavenModuleFile = new File(mavenModulePath);
            File ncModuleFile = new File(ncModulePath);
//            if (mavenModuleFile.exists()) {
//                Document doc = XmlParserUtils.getDocument(mavenModuleFile);
//                Node node = doc.getFirstChild();
//                NodeList nodeList = node.getChildNodes();
//
//                for (int i = 0; i < nodeList.getLength(); i++) {
//                    Node item = nodeList.item(i);
//                    if ("artifactId".equalsIgnoreCase(item.getNodeName())) {
//                        moduleName = item.getTextContent();
//                        break;
//                    }
//                }
            moduleName = "maven_" + file.getName();
//            } else
            if (ncModuleFile.exists()) {
                moduleName = "nc_" + file.getName();
            } else {

            }
        } catch (Exception e) {

        }
        moduleName += ".iml";
        return moduleName;
    }
}
