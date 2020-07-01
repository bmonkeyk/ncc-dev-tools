package com.yingling.util;

import com.pub.exception.BusinessException;

import java.io.*;

public class ConfigureFileUtil {
    /**
     * 读取输入模版
     *
     * @param fileName
     * @return
     */
    public String readTemplate(String fileName) throws BusinessException {
        InputStream in = this.getClass().getResourceAsStream("../../../template/" + fileName);
        return readTemplate(in);
    }

    /**
     * 读取输入模板
     *
     * @param file
     * @return
     * @throws BusinessException
     */
    public String readTemplate(File file) throws BusinessException {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return readTemplate(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    private String readTemplate(InputStream in) throws BusinessException {
        StringBuilder tempBuilder = new StringBuilder();
        try {
            InputStreamReader isr = new InputStreamReader(in, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String lineTxt;
            while (true) {
                if (!((lineTxt = br.readLine()) != null)) break;
                tempBuilder.append(lineTxt);
                tempBuilder.append("\r\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
        return tempBuilder.toString();
    }

    /**
     * 输出文件
     *
     * @param file
     * @param content
     */
    public void outFile(File file, String content, String charset, boolean bomFlag) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter dos = null;
            if (bomFlag) {
                fos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            }
            dos = new OutputStreamWriter(fos, charset);
            dos.write(content);
            dos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
