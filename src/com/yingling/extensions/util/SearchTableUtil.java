package com.yingling.extensions.util;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;
import org.apache.commons.lang.StringUtils;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 数据字典 查询工具
 */
public class SearchTableUtil {

    private DataSourceMeta meta;

    public SearchTableUtil(DataSourceMeta meta) {
        this.meta = meta;
    }


    public SearchTableUtil() {

    }

    /**
     * 模糊查询表列表
     *
     * @param searchKey
     * @return
     */
    public DefaultTableModel getTableList(String searchKey) {

        String[] fieldArr = new String[]{"classid", "id", "tablename", "modulename", "parentmodulename"};
        DefaultTableModel model = null;
        if (meta == null || StringUtils.isBlank(searchKey)) {
            model = getTableModel(fieldArr, null);
        } else {
            String sql = "SELECT cl.id classid,t.id,t.DISPLAYNAME tablename,mo.displayname modulename, " +
                    "(SELECT DISPLAYNAME FROM MD_MODULE WHERE id=mo.PARENTMODULEID) parentmodulename " +
                    "FROM MD_TABLE t LEFT JOIN MD_CLASS cl ON t.id = cl.DEFAULTTABLENAME " +
                    "LEFT JOIN MD_COMPONENT co ON co.id = cl.COMPONENTID " +
                    "LEFT JOIN md_module mo ON mo.ID = co.OWNMODULE " +
                    "WHERE t.id like '%" + searchKey + "%' OR t.displayname LIKE '%" + searchKey + "%'";

            List<Map<String, String>> rs = getResult(sql, fieldArr);

            model = getTableModel(fieldArr, rs);
        }

        return model;
    }

    public String getMdInfo(String classId) {

        //TODO 获取元数据信息
        return "";
    }

    /**
     * 查询标详情
     *
     * @param classId
     * @param tableId
     * @return
     */
    public DefaultTableModel getTableInfo(String classId, String tableId) {
        String[] columnTitleArr = new String[]{"code", "name", "sqldatetype", "columnlength", "description", "datatypename", "datatype", "enumvalue", "classtype"};

        DefaultTableModel model = null;
        try {
            if (meta == null || StringUtils.isBlank(classId) || StringUtils.isBlank(tableId)) {
                model = getTableModel(columnTitleArr, null);
            } else {

                //根据表名查询列信息
                String[] columnFieldArr = new String[]{"id", "name", "sqldatetype", "columnlength"};
                String sqlColumnSql = "SELECT id,name , SQLDATETYPE,COLUMNLENGTH FROM MD_COLUMN WHERE TABLEID = '" + tableId + "'";
                List<Map<String, String>> columnRs = getResult(sqlColumnSql, columnFieldArr);
                Map<String, Map<String, String>> columnMap = new HashMap<>();

                if (columnRs != null) {
                    for (Map<String, String> map : columnRs) {
                        for (String key : columnFieldArr) {
                            map.put(key, map.get(key));
                        }
                        columnMap.put(map.get("id"), map);
                    }
                }

                //根据元数据映射关系查询显示名称和参照关系
                String sql = "SELECT " +
                        "mo.columnid code, " +
                        "mp.displayname name, " +
                        "'' sqldatetype," +
                        "'' columnlength, " +
                        "(SELECT DESCRIPTION FROM md_class WHERE id = mp.datatype) description, " +
                        "(SELECT DISPLAYNAME FROM md_class WHERE id = mp.datatype) datatypename, " +
                        "mp.datatype, " +
                        "'' enumvalue, " +
                        "(SELECT CLASSTYPE FROM md_class WHERE id = mp.datatype) classtype " +
                        "FROM MD_PROPERTY mp " +
                        "LEFT JOIN MD_ORMAP mo ON mo.ATTRIBUTEID = mp.id " +
                        "WHERE " +
                        "mo.CLASSID = '" + classId + "'" +
                        "AND mo.TABLEID = '" + tableId + "'" +
                        "ORDER BY mp.name";

                List<Map<String, String>> list = new ArrayList<>();
                List<Map<String, String>> tableInfo = getResult(sql, columnTitleArr);
                Map<Integer, String> indexMap = new HashMap<>();//记录枚举项下标
                int i = 0;
                for (Map<String, String> map : tableInfo) {
                    Map<String, String> m = new HashMap<>();
                    for (String key : columnTitleArr) {
                        String v = map.get(key);
                        String id = map.get("code");
                        if ("code".equals(key)) {
                            v = columnMap.get(id).get("name");
                        }
                        if ("sqldatetype".contains(key)) {
                            v = columnMap.get(id).get("sqldatetype");
                        }
                        if ("columnlength".contains(key)) {
                            v = columnMap.get(id).get("columnlength");
                        }

                        m.put(key, v);
                    }
                    if ("203".equals(map.get("classtype"))) {
                        indexMap.put(i, map.get("datatype"));
                        i++;
                    }
                    list.add(m);
                }

                //处理枚举
                if (!indexMap.isEmpty()) {
                    String where = "";
                    String[] typeArr = indexMap.values().toArray(new String[0]);
                    for (String type : typeArr) {
                        where += ",'" + type + "'";
                    }
                    where = "(" + where.substring(1) + ")";
                    Map<String, String> enumMap = new HashMap<>();
                    List<Map<String, String>> enumSet = getResult("SELECT id,value,name FROM MD_ENUMVALUE WHERE id IN " + where + " order by value", new String[]{"id", "value", "name"});
                    for (Map<String, String> map : enumSet) {
                        String id = map.get("id");
                        String value = enumMap.get(id);
                        if (StringUtils.isBlank(value)) {
                            value = map.get("value") + ":" + map.get("name");
                        } else {
                            value += "," + map.get("value") + ":" + map.get("name");
                        }
                        enumMap.put(id, value);
                    }
                    for (Map<String, String> v : list) {
                        String dataType = v.get("datatype");
                        if (enumMap.keySet().contains(dataType)) {
                            v.put("enumvalue", enumMap.get(dataType));
                        }
                    }
                }

                model = getTableModel(columnTitleArr, list);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return model;
    }


    /**
     * 生成table model
     *
     * @param fieldArr
     * @param rs
     * @return
     */
    private DefaultTableModel getTableModel(String[] fieldArr, List<Map<String, String>> rs) {

        DefaultTableModel model = new DefaultTableModel(null, fieldArr) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (rs != null) {
            for (Map<String, String> map : rs) {
                Vector v = new Vector();
                for (String key : fieldArr) {
                    v.add(map.get(key));
                }
                model.addRow(v);
            }
        }
        return model;
    }


    /**
     * 执行sql查询
     *
     * @param sql
     * @param fieldArr
     * @return
     */
    private List<Map<String, String>> getResult(String sql, String[] fieldArr) {
        List<Map<String, String>> listMap = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            Class.forName(meta.getDriverClassName(), true, BoneCPConfig.class.getClassLoader());
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(meta.getDatabaseUrl());
            config.setUsername(meta.getUser());
            config.setPassword(meta.getPassword());
            BoneCP connPool = new BoneCP(config);
            conn = connPool.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();


            if (rs != null) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap();
                    for (String key : fieldArr) {
                        map.put(key, rs.getString(key));
                    }
                    listMap.add(map);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {

            }
        }
        return listMap;
    }

}
