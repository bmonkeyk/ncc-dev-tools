package com.yingling.patcher.util;

import com.pub.exception.BusinessException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 补丁包压缩工具
 */
public class ZipUtil {

    private static final int BUFFER = 8192;


    public static String toZip(String exportPath, String patchName) throws BusinessException {

        String path = new File(exportPath).getPath();
        String basePath = new File(exportPath).getPath();
        String[] strings = path.split(Matcher.quoteReplacement(File.separator));
        String fileName = strings[strings.length - 1];
        path = path.replace(fileName, "");
        File file = new File(exportPath);

        String zipName = path + fileName + "_" + patchName + ".zip";
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipName);
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
                    new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            compress(file, out, basePath);
            out.close();
        } catch (Exception e) {
            throw new BusinessException("zip failed : " + e.getMessage());
        }
        return zipName;
    }

    private static void compress(File file, ZipOutputStream out, String basePath) {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            compressDirectory(file, out, basePath);
        } else {
            compressFile(file, out, basePath);
        }
    }

    /**
     * 压缩目录
     *
     * @param dir
     * @param out
     * @param basePath
     */
    private static void compressDirectory(File dir, ZipOutputStream out, String basePath) {
        if (!dir.exists())
            return;

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compress(files[i], out, basePath);
        }
    }

    /**
     * 压缩文件
     *
     * @param file
     * @param out
     * @param basePath
     */
    private static void compressFile(File file, ZipOutputStream out, String basePath) {
        if (!file.exists()) {
            return;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            String filePath = file.getPath().replace(basePath + File.separator, "");
            ZipEntry entry = new ZipEntry(filePath);
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
