package com.yingling.extend.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.extensions.service.NccEnvSettingService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public class ExtendCopyUtil {

    public void copyToNCHome(AnActionEvent event) throws Exception {

        String homePath = NccEnvSettingService.getInstance().getNcHomePath();

        VirtualFile[] selectFile = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        List<String> authFileList = getAuthFileList(selectFile);

        for (String file : authFileList) {
            String toPath = homePath + File.separator + "hotwebs" + File.separator + "nccloud" + File.separator + "WEB-INF" + File.separator
                    + "extend" + File.separator + "yyconfig" + File.separator + "modules";
            String filePath = file.split(Matcher.quoteReplacement("yyconfig"+File.separator+"modules"))[1];
            toPath += filePath;
            FileUtil.copy(new File(file), new File(toPath));
        }
    }

    /**
     * 获取鉴权文件
     *
     * @param selectFile
     * @return
     */
    private List<String> getAuthFileList(VirtualFile[] selectFile) {
        List<String> list = new ArrayList<>();
        Set<String> fileUrlSet = new HashSet<>();
        if (null != selectFile) {
            for (VirtualFile file : selectFile) {
                getFileUrl(file.getPath(), fileUrlSet);
            }
        }
        list.addAll(fileUrlSet);
        return list;
    }

    /**
     * 递归路径获取可导出的文件
     *
     * @param filePath
     * @param fileUrlSet
     */
    private void getFileUrl(String filePath, Set<String> fileUrlSet) {

        if (filePath.contains("src")) {
            File file = new File(filePath);
            if (file.isDirectory()) {
                File[] childrenFile = file.listFiles();
                for (File childFile : childrenFile) {
                    getFileUrl(childFile.getPath(), fileUrlSet);
                }
            } else {
                if (filePath.endsWith("xml")) {
                    filePath = new File(filePath).getPath();
                    String tag1 = "client"+File.separator+"yyconfig"+File.separator+"modules";
                    String tag2 = "config"+File.separator+"action";
                    String tag3 = "config"+File.separator+"authorize";
                    if ((filePath.contains(tag1) && filePath.contains(tag2))
                            || (filePath.contains(tag1) && filePath.contains(tag3))) {
                        fileUrlSet.add(file.getPath());
                    }
                }
            }
        }
    }
}
