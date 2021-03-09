package com.yingling.patcher.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.pub.exception.BusinessException;
import com.yingling.util.ConfigureFileUtil;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;

/**
 * 补丁导出工具类
 */
public class ExportPatcherUtil {


    /**
     * 路径常量
     **/
    private final String PATH_CLIENT = File.separator + "client";
    private final String PATH_PUBLIC = File.separator + "public";
    private final String PATH_PRIVATE = File.separator + "private";
    private final String PATH_WEB_INF = File.separator + "WEB-INF";
    private final String PATH_CLASSES = File.separator + "classes";
    private final String PATH_MAPPER = File.separator + "mapper";
    private final String PATH_MODULES = File.separator + "modules";
    private final String PATH_META_INF = File.separator + "META-INF";
    private final String PATH_METADATA = File.separator + "METADATA";
    private final String PATH_RESOURCES = File.separator + "resources";
    private final String PATH_MAIN = File.separator + "main";
    private final String PATH_EXTEND = File.separator + "extend";
    private final String PATH_SRC = File.separator + "src";
    private final String TYPE_JAVA = ".java";
    private final String TYPE_CLASS = ".class";
    private final String TYPE_XML = ".xml";
    private final String TYPE_UPM = ".upm";
    private final String FILE_MODULE = "module.xml";
    private final String NAME_MODULE = "name";
    private final String PATH_HOTWEBS = File.separator + "hotwebs";
    private final String PATH_REPLACEMENT = File.separator + "replacement";
    private final String TYPE_BMF = ".bmf";
    private final String TYPE_BPF = ".bpf";
    private final String TYPE_PROPERTIES = ".properties";
    private final String FILE_COMPONENT = "component.xml";
    private final String TYPE_REST = ".rest";
    private final String TYPE_OPENAPI_MD = ".md";
    private final String PATH_OPENAPI = File.separator + "openapi";


    /**
     * 导出变量
     **/
    private String patchName;
    private String exportPath;
    private AnActionEvent event;
    private String webServerName = File.separator + "nccloud";
    private String zipName = "";
    private boolean srcFlag = false;
    private boolean cloudFlag = false;

    /**
     * 补丁工具类构造方法
     *
     * @param patchName
     * @param webServerName
     * @param exportPath
     * @param srcFlag
     * @param event
     */
    public ExportPatcherUtil(String patchName, String webServerName, String exportPath, boolean srcFlag, boolean cloudFlag, AnActionEvent event) {
        this.event = event;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String dateStr = simpleDateFormat.format(new Date());
        this.exportPath = exportPath + File.separator + "patch_" + dateStr;
        this.patchName = patchName;
        this.srcFlag = srcFlag;
        this.cloudFlag = cloudFlag;
        if (StringUtils.isNotBlank(webServerName)) {
            if (!webServerName.startsWith(File.separator)) {
                webServerName = File.separator + webServerName;
            }
            this.webServerName = webServerName;
        }
    }

    /**
     * 导出补丁
     *
     * @throws IOException
     */
    public void exportPatcher(JProgressBar progressBar) throws Exception {

        //当前工程
        Project project = event.getProject();

        //选中的文件
        VirtualFile[] selectFile = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

        //所有module
        Map<String, Module> moduleMap = new HashMap<>();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            moduleMap.put(module.getName(), module);
        }

        //选取文件路径和模块之间的关系
        Map<String, Set<String>> modulePathMap = new HashMap();

        //处理所有选中文件路径
        for (VirtualFile file : selectFile) {
            String path = new File(file.getPath()).getPath();
            //从文件路径中截取module名
            String moduleName = ModuleUtil.findModuleForFile(file, project).getName();
            //判断如是选择了模块或者组件，则获取所有可导出的目录
            List<String> fileList = getAllFileList(path, moduleMap.get(moduleName));
            Set<String> fileUrlSet = modulePathMap.get(moduleName);
            if (fileUrlSet == null) {
                fileUrlSet = new HashSet<>();
                modulePathMap.put(moduleName, fileUrlSet);
            }
            for (String str : fileList) {
                getFileUrl(str, fileUrlSet);
            }
        }

        //进度条设定
        int allCount = 0;
        for (String key : modulePathMap.keySet()) {
            allCount += modulePathMap.get(key).size();
        }
        progressBar.setMinimum(0);
        progressBar.setMaximum(allCount);

        //收集导出的文件
        Set<String> classNameSet = new HashSet<>();
        Set<String> moduleSet = new HashSet<>();

        int count = 1;
        int moduleIndex = 1;
        System.out.println(".............statr output , module count:" + modulePathMap.keySet().size() + "............");
        for (String moduleName : modulePathMap.keySet()) {
            Module module = moduleMap.get(moduleName);
            Set<String> fileUrlSet = modulePathMap.get(moduleName);
            System.out.println("......stat the " + moduleIndex + "th module : " + moduleName + ",total : " + fileUrlSet.size() + "......");
            CompilerModuleExtension instance = CompilerModuleExtension.getInstance(module);
            VirtualFile outPath = instance.getCompilerOutputPath();
            if (outPath == null) {
                throw new BusinessException("please set output folder or rebuild module ! module:" + moduleName);
            }
            String compilerOutputUrl = outPath.getPath();
            String ncModuleName = getNCModuleName(module);

            for (String fileUrl : fileUrlSet) {
                File fromFile = new File(fileUrl);
                String fileName = fromFile.getName();
                if (fileName.endsWith(TYPE_JAVA)) {//导出java文件
                    boolean flag = exportJava(moduleName, ncModuleName, compilerOutputUrl, fromFile);
                    if (!flag) {
                        continue;
                    }
                    String classPath = fileUrl.split(Matcher.quoteReplacement(PATH_SRC))[1];
                    if (classPath.startsWith(PATH_CLIENT)) {
                        classPath = classPath.replace(PATH_CLIENT, "");
                    }
                    if (classPath.startsWith(PATH_PUBLIC)) {
                        classPath = classPath.replace(PATH_PUBLIC, "");
                    }
                    if (classPath.startsWith(PATH_PRIVATE)) {
                        classPath = classPath.replace(PATH_PRIVATE, "");
                    }
                    String className = classPath.substring(1).replaceAll(Matcher.quoteReplacement(File.separator), ".");
                    if (className.startsWith("main.java")) {
                        className = className.replace("main.java.", "");
                    }
                    classNameSet.add(className);
                } else if (fileName.endsWith(TYPE_XML)) {//导出xml文件
                    exportXml(moduleName, ncModuleName, fromFile);
                } else if (fileName.endsWith(TYPE_UPM) || fileName.endsWith(TYPE_REST)) {//upm文件
                    exportUpm(moduleName, ncModuleName, fromFile);
                } else if (fileName.endsWith(TYPE_BMF) || fileName.endsWith(TYPE_BPF)) {//导出元数据文件
                    exportMetaFile(moduleName, ncModuleName, fromFile);
                } else if (fileName.endsWith(TYPE_PROPERTIES)) {//导出多语文件
                    exportProperties(moduleName, fromFile);
                } else if (fileName.endsWith(TYPE_OPENAPI_MD)){
                    exportOpenApiMD(moduleName, fromFile);
                }
                progressBar.setValue(count);
                System.out.println("count : " + fileUrlSet.size() + ",output :" + count + " " + fileUrl);
                count++;
            }
            System.out.println("......finished,module: " + moduleName + " total:" + fileUrlSet.size() + "......");
            moduleIndex++;
            //收集修改的module
            if (StringUtils.isNotBlank(ncModuleName)) {
                moduleSet.add(ncModuleName);
            }
        }
        moduleIndex = moduleIndex - 1;
        count = count - 1;
        System.out.println("............finished all ,module count : " + moduleIndex + " total:" + count + ".............");
        //创建ncm日志文件,只有ncccloud和ncchr的代码创建
        if (webServerName.endsWith("nccloud") || webServerName.endsWith("ncchr")) {
            //修改模块包含对应的web服务
            moduleSet.add(webServerName.substring(1));
            createNMCLog(moduleSet, classNameSet);
        }

        //创建zip压缩包
        zipName = ZipUtil.toZip(exportPath, patchName);
        //删除导出的目录
        delete(new File(exportPath));
    }

    /**
     * 压缩后删除原目录
     *
     * @param file
     */
    public void delete(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (File child : children) {
                delete(child);
            }
            file.delete();
        } else {
            file.delete();
        }

    }

    /**
     * 创建nmc日志
     *
     * @param moduleSet
     * @param classNameSet
     */
    private void createNMCLog(Set<String> moduleSet, Set<String> classNameSet) throws BusinessException {

        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sd.format(date);
        String id = UUID.randomUUID().toString();

        //输出readme
        ConfigureFileUtil util = new ConfigureFileUtil();
        File readmeFile = new File(exportPath + File.separator + "readme.txt");
        String template = util.readTemplate("readme.txt");
        String content = MessageFormat.format(template, patchName, id, dateStr);
        util.outFile(readmeFile, content, "UTF-8", true);

        //输出install
        File installFile = new File(exportPath + File.separator + "installpatch.xml");
        String s = "";
        if (!cloudFlag) {
            s = "<copy>\n" +
                    "        <from>/replacement/modules/</from>\n" +
                    "        <to>/modules/</to>\n" +
                    "    </copy>\n" +
                    "    <copy>\n" +
                    "        <from>/replacement/hotwebs/</from>\n" +
                    "        <to>/hotwebs/</to>\n" +
                    "    </copy>";
        }
        template = util.readTemplate("installpatch.xml");
        content = MessageFormat.format(template, s);
        util.outFile(installFile, content, "UTF-8", false);
        //输出metadata
        String modifyClasses = "";
        for (String className : classNameSet) {
            if (className.endsWith(TYPE_JAVA)) {//暂时只记录java文件
                className = className.replace(TYPE_JAVA, "");
                modifyClasses += "," + className;
            }
        }
        if (StringUtils.isNotBlank(modifyClasses)) {
            modifyClasses = modifyClasses.substring(1);
        }
        String modifyModules = "";
        for (String moduleName : moduleSet) {
            modifyModules += "," + moduleName;
        }
        if (StringUtils.isNotBlank(modifyModules)) {
            modifyModules = modifyModules.substring(1);
        }
        File metadataFile = new File(exportPath + File.separator + "packmetadata.xml");
        template = util.readTemplate("packmetadata.xml");
        content = MessageFormat.format(template, modifyClasses, modifyModules, patchName, id, dateStr);
        util.outFile(metadataFile, content, "UTF-8", false);
    }

    /**
     * 获取nc模块名称
     *
     * @param module
     * @return
     */
    private String getNCModuleName(Module module) {
        String ncModuleName = null;
        try {
            VirtualFile moduleFile = module.getModuleFile();
            String modulePath = moduleFile == null ? new File(module.getModuleFilePath()).getParentFile().getPath() : moduleFile.getParent().getPath();
            File file = new File(modulePath + PATH_META_INF + File.separator + FILE_MODULE);
            if (file.exists()) {
                InputStream in = new FileInputStream(file);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(in);
                Element root = doc.getDocumentElement();
                ncModuleName = root.getAttribute(NAME_MODULE);
            }
        } catch (Exception e) {
            //抛错就认为不是nc项目
        }
        return ncModuleName;
    }

    /**
     * 导出xml文件
     *
     * @param moduleName
     * @param fromFile
     * @throws IOException
     */
    private void exportXml(String moduleName, String ncModuleName, File fromFile) throws Exception {
        //判断文件类型
        if (fromFile.exists()) {
            InputStream in = new FileInputStream(fromFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(in);
            Element root = doc.getDocumentElement();

            String patchPath = fromFile.getPath();
            String toPath = null;
            String className = null;
            if ("actions".equals(root.getTagName()) || "authorizes".equals(root.getTagName())) {//ncc鉴权文件
                toPath = exportPath + PATH_REPLACEMENT + PATH_HOTWEBS + webServerName + PATH_WEB_INF + PATH_EXTEND;
                className = patchPath.split(Matcher.quoteReplacement(PATH_SRC))[1].replace(PATH_CLIENT, "");
            } else if ("beans".equals(root.getTagName())) {//nc client ui 配置文件
                toPath = exportPath + PATH_REPLACEMENT + PATH_MODULES + File.separator + ncModuleName + File.separator + PATH_CLIENT + PATH_CLASSES;
                className = patchPath.split(Matcher.quoteReplacement(PATH_SRC))[1].replace(PATH_CLIENT, "");
            } else if ("mapper".equals(root.getTagName())) {//mapper文件
                String path = "";
                if (webServerName.endsWith("ncchr")) {
                    path = PATH_REPLACEMENT + PATH_HOTWEBS;
                }
                toPath = exportPath + path + webServerName + PATH_WEB_INF + PATH_CLASSES + PATH_MAPPER;
                className = patchPath.split(Matcher.quoteReplacement(PATH_SRC))[1].replace(PATH_MAIN + PATH_RESOURCES + PATH_MAPPER, "");
            }

            //输出补丁
            if (StringUtil.isNotEmpty(toPath)) {
                outPatcher(moduleName, fromFile.getPath(), toPath + className);
            }
            in.close();
        }
    }
    /**
     * 导出openapi中的md描述文件
     * @param moduleName
     * @param fromFile
     */
    private void exportOpenApiMD(String moduleName, File fromFile) throws Exception {
        String fileName = fromFile.getName();
        String fromParentPath = fromFile.getParent();
        String toPath = exportPath + PATH_REPLACEMENT + fromParentPath.split(Matcher.quoteReplacement(PATH_OPENAPI))[1];
        outPatcher(moduleName, fromFile.getPath(), toPath);
    }
    /**
     * 导出元数据文件
     *
     * @param moduleName
     * @param ncModuleName
     * @param fromFile
     */
    private void exportMetaFile(String moduleName, String ncModuleName, File fromFile) throws Exception {
        String fileName = fromFile.getName();
        String fromParentPath = fromFile.getParent();
        String toPath = exportPath + PATH_REPLACEMENT + PATH_MODULES + File.separator + ncModuleName
                + PATH_METADATA;
        String componentPath = "";
        if (fromParentPath.contains(PATH_METADATA)) {
            if (fromParentPath.split(Matcher.quoteReplacement(PATH_METADATA)).length == 2) {
                componentPath = fromParentPath.split(Matcher.quoteReplacement(PATH_METADATA))[1];
            }
        }
        toPath += componentPath + File.separator + fileName;
        outPatcher(moduleName, fromFile.getPath(), toPath);
    }

    /**
     * 导出多语文件
     *
     * @param fromFile
     */
    private void exportProperties(String moduleName, File fromFile) throws Exception {
        String fileName = fromFile.getName();
        String fromParentPath = fromFile.getParent();
        String toPath = exportPath + PATH_REPLACEMENT + PATH_RESOURCES;
        String componentPath = "";
        if (fromParentPath.contains(PATH_RESOURCES)) {
            componentPath = fromParentPath.split(Matcher.quoteReplacement(PATH_RESOURCES))[1];
        }
        toPath += componentPath + File.separator + fileName;
        outPatcher(moduleName, fromFile.getPath(), toPath);
    }

    /**
     * 导出uppm文件
     *
     * @param moduleName
     * @param ncModuleName
     * @param fromFile
     */
    private void exportUpm(String moduleName, String ncModuleName, File fromFile) throws Exception {
        String fileName = fromFile.getName();
        String toPath = exportPath + PATH_REPLACEMENT + PATH_MODULES + File.separator + ncModuleName + PATH_META_INF;
        outPatcher(moduleName, fromFile.getPath(), toPath + File.separator + fileName);
    }

    /**
     * 导出java文件
     *
     * @param moduleName
     * @param compilerOutputUrl
     * @param fromFile
     * @throws IOException
     */
    private boolean exportJava(String moduleName, String ncModuleName, String compilerOutputUrl, File fromFile) throws Exception {

        String toPath = null;
        String patchPath = fromFile.getPath();
        if (patchPath.split(Matcher.quoteReplacement(PATH_SRC)).length != 2) {
            //不在src下的不导出
            return false;
        }
        String className = patchPath.split(Matcher.quoteReplacement(PATH_SRC))[1].replace(TYPE_JAVA, TYPE_CLASS);
        String javaName = patchPath.split(Matcher.quoteReplacement(PATH_SRC))[1];
        if (StringUtils.isNotBlank(ncModuleName)) {//nc模块
            String modulePath = exportPath + PATH_REPLACEMENT + PATH_MODULES + File.separator + ncModuleName + File.separator;
            if (patchPath.contains(PATH_CLIENT)) {
                className = className.replace(PATH_CLIENT, "");
                javaName = javaName.replace(PATH_CLIENT, "");
                if (patchPath.contains(webServerName) && webServerName.contains("nccloud")) {
                    toPath = exportPath + PATH_REPLACEMENT + PATH_HOTWEBS + webServerName + PATH_WEB_INF + PATH_CLASSES;
                } else {
                    toPath = modulePath + PATH_CLIENT + PATH_CLASSES;
                }
            } else if (patchPath.contains(PATH_PUBLIC)) {
                className = className.replace(PATH_PUBLIC, "");
                javaName = javaName.replace(PATH_PUBLIC, "");
                toPath = modulePath + PATH_CLASSES;
                cloudFlag = false;//这个参数决定了installpatcher文件中有没有copy标签。
                // 经验证，只有client补丁的时候，没有copy标签。所以这里判断只要进来public和private，就认为不是云原声
            } else if (patchPath.contains(PATH_PRIVATE)) {
                className = className.replace(PATH_PRIVATE, "");
                javaName = javaName.replace(PATH_PRIVATE, "");
                toPath = modulePath + PATH_META_INF + PATH_CLASSES;
                cloudFlag = false;//这个参数决定了installpatcher文件中有没有copy标签。
                // 经验证，只有client补丁的时候，没有copy标签。所以这里判断只要进来public和private，就认为不是云原声
            }
        } else if (webServerName.contains("ncchr")) {
            //ncchr补丁，支持云管家
            String basePath = File.separator + "main" + File.separator + "java";
            className = className.replace(basePath, "");
            javaName = javaName.replace(basePath, "");
            toPath = exportPath + PATH_REPLACEMENT + PATH_HOTWEBS + webServerName + PATH_WEB_INF + PATH_CLASSES;
        } else { //普通web项目
            String basePath = File.separator + "main" + File.separator + "java";
            className = className.replace(basePath, "");
            javaName = javaName.replace(basePath, "");
            toPath = exportPath + webServerName + PATH_WEB_INF + PATH_CLASSES;
        }
        if (fromFile.lastModified() > new File(compilerOutputUrl + className).lastModified()) {
            throw new BusinessException(className.substring(1).replace(File.separator, ".") + " is old, please rebuild : " + moduleName);
        }
        //输出补丁
        outPatcher(moduleName, compilerOutputUrl + className, toPath + className);
        //导出源文件
        if (srcFlag) {
            outPatcher(moduleName, fromFile.getPath(), toPath + javaName);
        }
        return true;
    }

    /**
     * 递归路径获取可导出的文件
     *
     * @param elementPath
     * @param fileUrlSet
     */
    private void getFileUrl(String elementPath, Set<String> fileUrlSet) {

        if (elementPath.contains(PATH_SRC) || elementPath.contains(PATH_META_INF)
                || elementPath.contains(PATH_METADATA) || elementPath.contains(PATH_RESOURCES)
                || (elementPath.contains(PATH_HOTWEBS) && elementPath.contains(PATH_OPENAPI))) {
            File file = new File(elementPath);
            if (file.isDirectory()) {
                File[] childrenFile = file.listFiles();
                for (File childFile : childrenFile) {
                    getFileUrl(childFile.getPath(), fileUrlSet);
                }
            } else {
                if ((elementPath.endsWith(TYPE_JAVA) && elementPath.contains(PATH_SRC)) || elementPath.endsWith(TYPE_XML) || elementPath.endsWith(TYPE_UPM)
                        || elementPath.endsWith(TYPE_BMF) || elementPath.endsWith(TYPE_BPF) || elementPath.endsWith(TYPE_PROPERTIES)
                        || elementPath.endsWith(TYPE_REST) || elementPath.endsWith(TYPE_OPENAPI_MD)) {
                    fileUrlSet.add(elementPath);
                }
            }
        }
    }

    /**
     * 输出补丁
     *
     * @param moduleName
     * @param srcPath
     * @param toPath
     */
    private void outPatcher(String moduleName, String srcPath, String toPath) throws Exception {
        File from = new File(srcPath);
        if (!from.exists()) {
            throw new BusinessException("please build : " + moduleName);
        }
        File to = new File(toPath);

        FileUtil.copy(from, to);

        //静态类处理,静态工具了编译后会成为 类名+%1.class的样子
        String fileName = from.getName().substring(0, from.getName().length() - 6);//去掉.class
        for (File f : Objects.requireNonNull(from.getParentFile().listFiles())) {
            if (f.getName().startsWith(fileName + "$")) {
                FileUtil.copy(f, new File(to.getParent() + File.separator + f.getName()));
            }
        }
    }

    /**
     * 获取选中目录下所有可导出目录
     *
     * @param path
     * @param module
     * @return
     */
    private List<String> getAllFileList(String path, Module module) {
        List<String> fileList = new ArrayList<>();
        File moduleFile = new File(path + PATH_META_INF + File.separator + FILE_MODULE);
        //如果是模块，就向下寻找组件
        if (moduleFile.exists()) {
            File[] componentFiles = new File(path).listFiles();
            for (File component : componentFiles) {
                if (component.isFile()) {
                    continue;
                }
                File componentFile = new File(component.getPath() + File.separator + FILE_COMPONENT);
                if (componentFile.exists()) {
                    fileList.addAll(getComponentPathList(component));
                }
            }
        }
        if (fileList.size() > 0) {
            return fileList;
        }
        //如果不是模块，则判断是不是组件
        File componentFile = new File(path + File.separator + FILE_COMPONENT);
        if (componentFile.exists()) {
            fileList = getComponentPathList(new File(path));
        }
        if (fileList.size() > 0) {
            return fileList;
        }
        //如果什么都不是，则判断是不是模块下的文件或者目录
        String modulePath = module.getModuleFile().getParent().getPath();
        if (path.startsWith(new File(modulePath).getPath())) {
            fileList.add(path);
        }
        return fileList;
    }

    /**
     * 获取组件下边可导出的目录
     *
     * @param componentFile
     * @return
     */
    private List<String> getComponentPathList(File componentFile) {

        List<String> fileList = new ArrayList<>();
        if (componentFile.exists()) {
            for (File file : componentFile.listFiles()) {
                if (file.isFile()) {
                    continue;
                }
                if (file.getPath().endsWith(PATH_META_INF) || file.getPath().endsWith(PATH_METADATA)
                        || file.getPath().endsWith(PATH_RESOURCES) || file.getPath().endsWith(PATH_SRC) || file.getPath().endsWith(PATH_HOTWEBS)) {
                    fileList.add(file.getPath());
                }
            }
        }
        return fileList;
    }


    public String getExportPath() {
        return exportPath;
    }

    public String getZipName() {
        return zipName;
    }

}
