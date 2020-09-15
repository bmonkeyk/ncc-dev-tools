package com.yingling.util;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;
import org.apache.commons.lang.ArrayUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 数据源数据查询工具
 */
public class DBDataUtil {

    private DataSourceMeta meta;

    public DBDataUtil(DataSourceMeta meta){
        this.meta = meta;
    }

    /**
     * 执行sql查询
     *
     * @param sql
     * @param fieldArr
     * @return
     */
    public List<Map<String, String>> getResult(String sql, String[] fieldArr) {

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
                    if (ArrayUtils.isEmpty(fieldArr)) {
                        ResultSetMetaData resultSetMetaData = rs.getMetaData();
                        int count = resultSetMetaData.getColumnCount();
                        for (int i = 0; i < count; i++) {
                            map.put(resultSetMetaData.getColumnName(i+1),rs.getString(i+1));
                        }
                    } else {
                        for (String key : fieldArr) {
                            map.put(key, rs.getString(key));
                        }
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
