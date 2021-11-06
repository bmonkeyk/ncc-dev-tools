package com.yingling.base;

import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ModuleFileUtil {


    /**
     * 文件复制
     *
     * @param srcFile
     * @param toFile
     */
    private static void copy(File srcFile, File toFile) {
        //判断源目录是不是一个目录
        if (!srcFile.isDirectory()) {
            //如果不是目录那就不复制
            return;
        }
        //如果目的目录不存在
        if (!toFile.exists()) {
            //创建目的目录
            toFile.mkdir();
        }
        //获取源目录下的File对象列表
        File[] files = srcFile.listFiles();
        for (File file : files) {
            //拼接新的fromDir(fromFile)和toDir(toFile)的路径
            File strFrom = new File(srcFile.getPath() + File.separator + file.getName());
            File strTo = new File(toFile.getPath() + File.separator + file.getName());
            //判断File对象是目录还是文件
            //判断是否是目录
            if (file.isDirectory()) {
                //递归调用复制目录的方法
                copy(strFrom, strTo);
            }
            //判断是否是文件
            if (file.isFile()) {
                try {
                    if (!strTo.exists()) {
                        FileUtil.copy(file, strTo);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }
    }

    /**
     * 必选模块
     * 目前这么定义，具体的有待考究
     *
     * @return
     */
    public static Set<String> getModuleSet() {
        Set<String> moduleSet = new HashSet();
        //公共
        moduleSet.add("baseapp");
        moduleSet.add("iuap");
        moduleSet.add("opm");

        moduleSet.add("platform");
        moduleSet.add("pubapp");
        moduleSet.add("pubapputil");

        //框架
        moduleSet.add("riaaam");
        moduleSet.add("riaadp");
        moduleSet.add("riaam");
        moduleSet.add("riacc");
        moduleSet.add("riadc");
        moduleSet.add("riamm");
        moduleSet.add("riaorg");
        moduleSet.add("riart");
        moduleSet.add("riasm");
        moduleSet.add("riawf");
        //uap
        moduleSet.add("uapbd");
        moduleSet.add("uapbs");
        moduleSet.add("uapec");
        moduleSet.add("uapfw");
        moduleSet.add("uapfwjca");
        moduleSet.add("uapmw");
        moduleSet.add("uapportal");
        moduleSet.add("uapsc");
        moduleSet.add("uapss");
        //ncc
        moduleSet.add("workbench");
        return moduleSet;
    }
}
