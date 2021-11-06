package com.yingling.upm.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.base.NccEnvSettingService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EjbConfCopyUtil {
    public void copyToNCHome(AnActionEvent event) throws Exception {

        String homePath = NccEnvSettingService.getInstance().getNcHomePath();
        //当前工程
        Project project = event.getProject();
        VirtualFile[] selectFile = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

        Map<Module, List<VirtualFile>> moduleMap = new HashMap<>();

        //按模块区分
        for (VirtualFile virtualFile : selectFile) {
            Module module = ModuleUtil.findModuleForFile(virtualFile, project);
            List<VirtualFile> list = moduleMap.get(module);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(virtualFile);
            moduleMap.put(module, list);
        }

        //按模块递归复制
        for (Module module : moduleMap.keySet()) {
            List<String> list = getUpmList(moduleMap.get(module));
            String ncModuleName = getNCModuleName(module);
            for (String s : list) {
                String toPath = homePath + File.separator + "modules" + File.separator + ncModuleName + File.separator + "META-INF" + File.separator + new File(s).getName();
                FileUtil.copy(new File(s), new File(toPath));
            }
        }
    }

    /**
     * 筛选upm文件
     *
     * @param virtualFileList
     * @return
     */
    private List<String> getUpmList(List<VirtualFile> virtualFileList) {
        Set<String> set = new HashSet<>();
        if (null != virtualFileList) {
            for (VirtualFile v : virtualFileList) {
                getFileUrl(v.getPath(), set);
            }
        }
        return new ArrayList<>(set);
    }

    /**
     * 递归路径获取可导出的文件
     *
     * @param filePath
     * @param fileUrlSet
     */
    private void getFileUrl(String filePath, Set<String> fileUrlSet) {

        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] childrenFile = file.listFiles();
            for (File childFile : childrenFile) {
                getFileUrl(childFile.getPath(), fileUrlSet);
            }
        } else {
            if ((filePath.endsWith(".rest") || filePath.endsWith(".upm")) && new File(filePath).getParent().endsWith("META-INF")) {
                fileUrlSet.add(filePath);
            }
        }

    }

    /**
     * 获取nc模块名称
     *
     * @param module
     * @return
     */
    private String getNCModuleName(Module module) {

        String ncModuleName = null;
        String modulePath = module.getModuleFile().getParent().getPath();
        try {
            File file = new File(modulePath + File.separator + "META-INF" + File.separator + "module.xml");
            if (file.exists()) {
                InputStream in = new FileInputStream(file);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(in);
                Element root = doc.getDocumentElement();
                ncModuleName = root.getAttribute("name");
            }
        } catch (Exception e) {
            //抛错就认为不是nc项目
        }
        return ncModuleName;
    }
}
