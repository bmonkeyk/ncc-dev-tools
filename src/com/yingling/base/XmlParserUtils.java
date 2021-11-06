
package com.yingling.base;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;


/**
 * xml解析器的包装类
 *
 * @author yaboocn
 * @version 1.0 2014年5月5日
 * @since 1.7
 */
public class XmlParserUtils {
    /**
     * 取得新建的xml结构的Document对象
     *
     * @return Document对象
     * @throws Exception
     */
    public static Document getDocument() throws Exception {
        DocumentBuilder builder = getDocumentBuilder();
        if (builder == null) {
            return null;
        }
        Document doc = builder.newDocument();
        return doc;
    }

    /**
     * 解析XML文件返回Document对象
     *
     * @param file xml文件
     * @return Document对象
     * @throws Exception
     */
    public static Document getDocument(File file) throws Exception {
        DocumentBuilder builder = getDocumentBuilder();
        if (builder == null) {
            return null;
        }
        Document doc = builder.parse(file);
        return doc;
    }

    /**
     * 解析XML数据流返回Document对象
     *
     * @param in xml输入流
     * @return Document对象
     * @throws Exception
     */
    public static Document getDocument(InputStream in) throws Exception {
        DocumentBuilder builder = getDocumentBuilder();
        if (builder == null) {
            return null;
        }
        Document doc = builder.parse(in);
        return doc;
    }

    /**
     * 解析xml串返回Document对象
     *
     * @param xml xml串
     * @return Document对象
     * @throws Exception
     */
    public static Document getDocument(String xml) throws Exception {
        DocumentBuilder builder = getDocumentBuilder();
        if (builder == null) {
            return null;
        }
        InputStream is = new ByteArrayInputStream(xml.getBytes("utf-8"));
        Document doc = builder.parse(is);
        return doc;
    }

    /**
     * 取得xml解析期的Document构造器对象
     *
     * @return Document构造器对象
     * @throws Exception
     */
    public static DocumentBuilder getDocumentBuilder() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        builderFactory.setIgnoringComments(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        return builder;
    }

    /**
     * 取得SAX方式的解析器对象
     *
     * @return 解析器对象
     * @throws Exception
     */
    public static SAXParser getSAXParser() throws Exception {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = parserFactory.newSAXParser();
        return saxParser;
    }


}
