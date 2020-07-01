package com.yonyou.common.database.powerdesigner.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.yonyou.common.database.powerdesigner.core.Pdm;
import com.yonyou.common.database.powerdesigner.exception.PDMParseRuntimeException;
import nc.uap.studio.pub.db.model.IColumn;
import nc.uap.studio.pub.db.model.impl.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdmUtil {
    protected static Logger logger = LoggerFactory.getLogger(DataDictionaryGeneratorFactory.class.getName());
    private static final String PD_FILE_TYPE = "PDM_DATA_MODEL_XML";

    private static final String DB_SQLSERVER_2005 = "Microsoft SQL Server 2005";

    private static final String PD_VERSION = "12.0.0.1700";

    private static final String TRUE_VALUE = "1";

    public static void validatePdm(VirtualFile pdmFile) {
        BufferedReader br = null;
        boolean success = false;
        String fileName = pdmFile.getPath();
        try {
            br = new BufferedReader(new InputStreamReader(pdmFile.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                int indexOfPdStartTag = -1;
                if ((indexOfPdStartTag = line.indexOf("<?PowerDesigner ")) != -1) {
                    int indexOfPdEndTag = line.indexOf(">", indexOfPdStartTag);
                    String pdContent = line.substring(indexOfPdStartTag + "<?PowerDesigner ".length(), indexOfPdEndTag);
                    Pattern pattern = Pattern.compile("signature=\"([^\"]*)\"");
                    Matcher matcher = pattern.matcher(pdContent);
                    if (!matcher.find() || !"PDM_DATA_MODEL_XML".equalsIgnoreCase(matcher.group(1)))
                        throw new PDMParseRuntimeException("PDM(" + fileName + ")文件格式不对, 目前支持的文件类型为" + "PDM_DATA_MODEL_XML");
                    pattern = Pattern.compile("Target=\"([^\"]*)\"");
                    matcher = pattern.matcher(pdContent);
                    if (!matcher.find() || !"Microsoft SQL Server 2005".equalsIgnoreCase(matcher.group(1)))
                        throw new PDMParseRuntimeException("PDM(" + fileName + ")文件中当前数据库类型不符, 目前支持的数据库类型为" + "Microsoft SQL Server 2005");
                    pattern = Pattern.compile("version=\"([^\"]*)\"");
                    matcher = pattern.matcher(pdContent);
                    if (!matcher.find() || !"12.0.0.1700".equalsIgnoreCase(matcher.group(1)))
                        throw new PDMParseRuntimeException("PDM(" + fileName + ")文件中PowerDesigner版本不对, 目前支持的版本为" + "12.0.0.1700");
                    success = true;
                    break;
                }
            }
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            String errorMsg = "PDM(" + fileName + ")编码失败。";
            throw new PDMParseRuntimeException(errorMsg);
        } catch (FileNotFoundException fileNotFoundException) {
            String errorMsg = "PDM(" + fileName + ")不存在。";
            throw new PDMParseRuntimeException(errorMsg);
        } catch (IOException iOException) {
            String errorMsg = "PDM(" + fileName + ")读取失败。";
            throw new PDMParseRuntimeException(errorMsg);
        } finally {
            IOUtils.closeQuietly(br);
        }
        if (!success)
            throw new PDMParseRuntimeException("PDM(" + fileName + ")中文件有误。无法解析出数据库类型、版本等信息。");
    }

    public static Pdm parsePdm(VirtualFile pdmFile, boolean parseReference) {
        long start = System.currentTimeMillis();
        String fileName = pdmFile.getPath();
        Document dom = getDocument(pdmFile);
        Element rootEle = dom.getDocumentElement();
        Pdm pdm = new Pdm();
        pdm.setVersion("12.0.0.1700");
        Element modelEle = (Element) DomUtil.findNestedChild(rootEle, "o:RootObject/c:Children/o:Model");
        List<Node> nameNodes = DomUtil.findNestedChilds(modelEle, "a:Name");
        if (nameNodes.size() != 1)
            throw new PDMParseRuntimeException("PDM(" + fileName + ")文件中包含非法数量的PDM");
        pdm.setPdmDesc(((Node) nameNodes.get(0)).getFirstChild().getNodeValue());
        pdm.setPdmName(DomUtil.findChildContent(modelEle, "a:Code"));
        Map<String, Table> idTableMap = new LinkedHashMap<String, Table>();
        Map<String, Column> idColumnMap = new HashMap<String, Column>();
        Map<String, Index> idIndexMap = new LinkedHashMap<String, Index>();
        parseTables(modelEle, fileName, idTableMap, idColumnMap, idIndexMap);
        pdm.getTables().addAll(idTableMap.values());
        pdm.getIndexs().addAll(idIndexMap.values());
        if (parseReference) {
            List<FkConstraint> fkConstraints = parseReferences(modelEle, pdmFile.getPath(), idTableMap, idColumnMap);
            pdm.getFkConstraints().addAll(fkConstraints);
        }
        List<Pdm.ViewInfo> viewInfos = parseViews(modelEle, fileName);
        pdm.getViews().addAll(viewInfos);
        logger.error("Total millis cost at parsing pdm(" + fileName + "): " + (System.currentTimeMillis() - start));
        return pdm;
    }

    private static void parseTables(Element modelEle, String pdmFileName, Map<String, Table> idTableMap, Map<String, Column> idColumnMap, Map<String, Index> idIndexMap) {
        long start = System.currentTimeMillis();
        List<Node> tableNodes = DomUtil.findNestedChilds(modelEle, "c:Tables/o:Table");
        if (tableNodes.isEmpty())
            throw new PDMParseRuntimeException("PDM(" + pdmFileName + ")中没有找到表，请检查PDM中是否在顶层定义了package。");
        for (Node tableNode : tableNodes) {
            String tableId = ((Element) tableNode).getAttribute("Id");
            Table table = new Table();
            table.setDesc(DomUtil.findChildContent(tableNode, "a:Name"));
            table.setName(DomUtil.findChildContent(tableNode, "a:Code"));
            List<Node> colNodes = DomUtil.findNestedChilds(tableNode, "c:Columns/o:Column");
            if (colNodes.isEmpty()) {
                logger.error("PDM(" + pdmFileName + ")中表(" + table.getName() + ")未定义列。");
                continue;
            }
            boolean success = true;
            for (Node colNode : colNodes) {
                String colId = ((Element) colNode).getAttribute("Id");
                Column col = new Column();
                col.setDesc(DomUtil.findChildContent(colNode, "a:Name"));
                col.setName(DomUtil.findChildContent(colNode, "a:Code"));
                if (StringUtils.equalsIgnoreCase(col.getName(), "ts") ||
                        StringUtils.equalsIgnoreCase(col.getName(), "dr")) {
                    logger.error("PDM(" + pdmFileName + ")中表(" +
                            table.getName() + ")的列(" + col.getName() +
                            ")无需设置。");
                    continue;
                }
                col.setTypeName(DomUtil.findChildContent(colNode, "a:DataType"));
                if (StringUtils.isBlank(col.getTypeName())) {
                    success = false;
                    String msg = "PDM(" + pdmFileName + ")中表(" +
                            table.getName() + ")的列(" + col.getName() +
                            ")未设置数据类型。";
                    logger.error(msg);
                    continue;
                }
                String length = DomUtil.findChildContent(colNode, "a:Length");
                if (StringUtils.isNotBlank(length))
                    col.setLength(Integer.valueOf(length).intValue());
                String precision = DomUtil.findChildContent(colNode, "a:Precision");
                if (StringUtils.isNotBlank(precision))
                    col.setPrecise(Integer.valueOf(precision).intValue());
                col.setNullable(!"1".equals(DomUtil.findChildContent(colNode, "a:Mandatory")));
                String defaultValue = DomUtil.findChildContent(colNode, "a:DefaultValue");
                if (StringUtils.isNotBlank(defaultValue)) {
                    if (col.getTypeName() != null && col.getTypeName().toLowerCase().lastIndexOf("char") != -1 &&
                            defaultValue.lastIndexOf("'") == -1)
                        defaultValue = "'" + defaultValue + "'";
                    col.setDefaultValue(defaultValue);
                }
                String stereoType = DomUtil.findChildContent(colNode, "a:Stereotype");
                if (StringUtils.isNotBlank(stereoType))
                    col.setStereotype(stereoType);
                table.getAllColumns().add(col);
                idColumnMap.put(colId, col);
            }
            if (success) {
                idTableMap.put(tableId, table);
                List<Node> indexNodes = DomUtil.findNestedChilds(tableNode, "c:Indexes/o:Index");
                for (Node indexNode : indexNodes) {
                    String indexName = DomUtil.findChildContent(indexNode, "a:Code");
                    List<Node> indexColNodes = DomUtil.findNestedChilds(indexNode, "c:IndexColumns/o:IndexColumn/c:Column/o:Column");
                    if (indexColNodes.isEmpty()) {
                        logger.error("PDM(" + pdmFileName + ")中表(" +
                                table.getName() + ")的索引(" + indexName + ")未定义列。");
                        continue;
                    }
                    String indexId = ((Element) indexNode).getAttribute("Id");
                    Index index = new Index();
                    index.setTable(table);
                    index.setUnique("1".equals(DomUtil.findChildContent(indexNode, "a:Unique")));
                    index.setName(indexName);
                    index.setDesc(DomUtil.findChildContent(indexNode, "a:Name"));
                    for (Node indexColNode : indexColNodes) {
                        Element indexColEle = (Element) indexColNode;
                        index.getColumns().add((IColumn) idColumnMap.get(indexColEle.getAttribute("Ref")));
                    }
                    idIndexMap.put(indexId, index);
                }
                String primaryKeyRef = DomUtil.findNestedChildAttr(tableNode, "c:PrimaryKey/o:Key", "Ref");
                if (StringUtils.isBlank(primaryKeyRef)) {
                    logger.error("PDM(" + pdmFileName + ")中表(" + table.getName() + ")无主键。");
                } else {
                    Element okeyEle = (Element) DomUtil.findNestedChild(tableNode, "c:Keys/o:Key", "Id", primaryKeyRef);
                    if (okeyEle != null) {
                        List<Node> pkColNodes = DomUtil.findNestedChilds(okeyEle, "c:Key.Columns/o:Column");
                        if (!pkColNodes.isEmpty()) {
                            PkConstraint pkConstraint = new PkConstraint();
                            String constraintName = DomUtil.findChildContent(okeyEle, "a:ConstraintName");
                            if (StringUtils.isBlank(constraintName))
                                constraintName = "pk_" + table.getName();
                            pkConstraint.setName(constraintName.toLowerCase());
                            success = true;
                            for (Node pkColNode : pkColNodes) {
                                String colRef = ((Element) pkColNode).getAttribute("Ref");
                                Column col = (Column) idColumnMap.get(colRef);
                                if (col == null) {
                                    success = false;
                                    break;
                                }
                                pkConstraint.getColumns().add(col);
                            }
                            if (success) {
                                table.setPkConstraint(pkConstraint);
                            } else {
                                logger.error("PDM(" + pdmFileName + ")中表(" + table.getName() + ")无主键。");
                            }
                        }
                    }
                }
                String clusteredIndexId = DomUtil.findNestedChildAttr(tableNode, "c:ClusterObject/o:Index", "Ref");
                if (StringUtils.isNotBlank(clusteredIndexId)) {
                    Index index = (Index) idIndexMap.get(clusteredIndexId);
                    if (index != null)
                        index.setClustered(true);
                }
            }
        }
        logger.error("Millis cost at parsing (" + pdmFileName +
                ") with " + tableNodes.size() + " tables: " + (
                System.currentTimeMillis() - start));
    }

    private static List<FkConstraint> parseReferences(Element modelEle, String pdmFileName, Map<String, Table> idTableMap, Map<String, Column> idColumnMap) {
        long startMillis = System.currentTimeMillis();
        List<FkConstraint> fkConstraints = new ArrayList<FkConstraint>();
        List<Node> referenceNodes = DomUtil.findNestedChilds(modelEle, "c:References/o:Reference");
        if (referenceNodes.isEmpty()) {
            logger.error("PDM(" + pdmFileName + ")中未配置外键引用。");
            return fkConstraints;
        }
        for (Node referenceNode : referenceNodes) {
            String referName = DomUtil.findChildContent(referenceNode, "a:Code");
            String referDesc = DomUtil.findChildContent(referenceNode, "a:Name");
            String mainTableId = DomUtil.findNestedChildAttr(referenceNode, "c:Object1/o:Table", "Ref");
            String subTableId = DomUtil.findNestedChildAttr(referenceNode, "c:Object2/o:Table", "Ref");
            Table mainTable = (Table) idTableMap.get(mainTableId);
            Table subTable = (Table) idTableMap.get(subTableId);
            if (mainTable == null || subTable == null) {
                logger.error((new StringBuilder("PDM(")).append(pdmFileName).append(")中外键引用(")
                        .append(referDesc).append(")引用关联的表无效。").toString());
                continue;
            }
            Node referenceJoinNode = DomUtil.findNestedChild(referenceNode, "c:Joins/o:ReferenceJoin");
            if (referenceJoinNode != null) {
                String mainTableColId = DomUtil.findNestedChildAttr(referenceJoinNode, "c:Object1/o:Column", "Ref");
                String subTableColId = DomUtil.findNestedChildAttr(referenceJoinNode, "c:Object2/o:Column", "Ref");
                Column mainTableCol = (Column) idColumnMap.get(mainTableColId);
                Column subTableCol = (Column) idColumnMap.get(subTableColId);
                if (mainTableCol != null || subTableCol != null) {
                    FkConstraint fkConstraint = new FkConstraint();
                    fkConstraint.setTable(subTable);
                    fkConstraint.setRefTable(mainTable);
                    fkConstraint.getColumns().add(subTableCol);
                    fkConstraint.getRefColumns().add(mainTableCol);
                    fkConstraint.setName(referName);
                    fkConstraints.add(fkConstraint);
                    subTable.getFkConstraints().add(fkConstraint);
                    continue;
                }
                logger.error((new StringBuilder("PDM(")).append(pdmFileName).append(")中外键引用(")
                        .append(referDesc).append(")引用关联的表列无效。").toString());
                continue;
            }
            logger.error((new StringBuilder("PDM(")).append(pdmFileName).append(")中外键引用(")
                    .append(referDesc).append(")引用关联的表列无效。").toString());
        }
        logger.error("Millis cost at parsing " + referenceNodes.size() + " references is: " + (
                System.currentTimeMillis() - startMillis));
        return fkConstraints;
    }

    private static List<Pdm.ViewInfo> parseViews(Element modelEle, String pdmFileName) {
        long startMillis = System.currentTimeMillis();
        List<Node> viewNodes = DomUtil.findNestedChilds(modelEle, "c:Views/o:View");
        if (viewNodes.isEmpty()) {

            logger.error("PDM(" + pdmFileName + ")中未配置视图。");
            return Collections.EMPTY_LIST;
        }
        List<Pdm.ViewInfo> viewInfos = new ArrayList<Pdm.ViewInfo>();
        for (Node viewNode : viewNodes) {
            String viewName = DomUtil.findChildContent(viewNode, "a:Code");
            String viewDesc = DomUtil.findChildContent(viewNode, "a:Name");
            List<Node> viewColNodes = DomUtil.findNestedChilds(viewNode, "c:Columns/o:ViewColumn");
            if (viewColNodes.isEmpty()) {
                logger.error("PDM(" + pdmFileName + ")中视图(" + viewDesc + ")未设置查询列。");
                continue;
            }
            String sqlQuery = DomUtil.findChildContent(viewNode, "a:View.SQLQuery");
            if (StringUtils.isNotBlank(sqlQuery)) {
                sqlQuery = sqlQuery.replaceAll("&#39;", "'");
                Pdm.ViewInfo viewInfo = new Pdm.ViewInfo();
                viewInfo.setName(viewName);
                viewInfo.setDesc(viewDesc);
                viewInfo.setSql(sqlQuery);
                viewInfo.setDesc(DomUtil.findChildContent(viewNode, "Name"));
                viewInfos.add(viewInfo);
            }
        }
        logger.error("Millis cost at parsing " + viewNodes.size() + " view is: " + (
                System.currentTimeMillis() - startMillis));
        return viewInfos;
    }

    private static Document getDocument(VirtualFile file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(file.getInputStream());
        } catch (Exception e) {
            logger.error("解析文件" + file.getPath() + "为xml出错。", e);
            throw new PDMParseRuntimeException("解析文件" + file.getPath() + "为xml出错。");
        }
    }

    enum CaseType {
        TOLOWER, TOUPPER, RESERVE;
    }
}
