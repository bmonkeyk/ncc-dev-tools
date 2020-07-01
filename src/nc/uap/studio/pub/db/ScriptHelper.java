package nc.uap.studio.pub.db;

import nc.uap.studio.pub.db.model.IColumn;
import nc.uap.studio.pub.db.model.ITable;
import nc.uap.studio.pub.db.script.export.IScriptExportStratege;
import nc.uap.studio.pub.db.script.export.InitDataExportStratege2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptHelper {
    public static String getSelectTable(String sql) {
        String regex = "";
        if (Pattern.matches("^.*\\swhere\\s.*$", sql)) {
            regex = "(from)(.+?)(where)";
        } else {
            regex = "(from)(.+)$";
        }
        Pattern pattern = Pattern.compile(regex, 2);
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find())
            return matcher.group(2);
        return null;
    }

    public static String convert2DeleteSql(ITable table, Map<String, Object> result, String separator) {
        List<IColumn> pkColumns = table.getPkConstraint().getColumns();
        boolean bHasPK = (pkColumns != null && pkColumns.size() > 0);
        StringBuilder sql = new StringBuilder();
        if (bHasPK) {
            sql.append("delete from ");
            sql.append(table.getName());
            sql.append(" where ");
            for (int i = 0; i < pkColumns.size(); i++) {
                IColumn column = (IColumn) pkColumns.get(i);
                String txtValue = SqlUtil.formatSql(
                        result.get(column.getName()), column.getDataType());
                sql.append(column.getName());
                sql.append("=");
                sql.append(txtValue);
                if (i == pkColumns.size() - 1) {
                    sql.append(separator);
                } else {
                    sql.append(" and ");
                }
            }
        }
        return sql.toString();
    }

    public static String convert2InsertSqls(ITable table, Map<String, Object> result, String separator, IScriptExportStratege stratege) {
        List<IColumn> pkColumns = table.getPkConstraint().getColumns();
        boolean bHasPK = (pkColumns != null && pkColumns.size() > 0);
        List<String> lstPKColumnName = null;
        StringBuilder sbPKColumns = new StringBuilder();
        if (bHasPK) {
            lstPKColumnName = new ArrayList<String>(pkColumns.size());
            for (Object element : pkColumns) {
                IColumn column = (IColumn) element;
                if (column == null)
                    continue;
                String columnName = column.getName();
                lstPKColumnName.add(columnName);
                sbPKColumns.append(columnName).append(",");
            }
        }
        StringBuilder cols = new StringBuilder(), values = new StringBuilder();
        StringBuilder singleSql = (new StringBuilder("insert into ")).append(table.getName()).append("(");
        StringBuilder sbPKValues = new StringBuilder();
        if (bHasPK)
            for (Object element : lstPKColumnName) {
                String pkColumnName = (String) element;
                IColumn col = table.getColumnByName(pkColumnName);
                sbPKValues.append(
                        SqlUtil.formatSql(result.get(pkColumnName),
                                col.getDataType())).append(",");
            }
        List<String> multColumnList = new LinkedList<String>();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            String columnName = (String) entry.getKey();
            if (bHasPK && lstPKColumnName.contains(columnName))
                continue;
            IColumn col = table.getColumnByName(columnName);
            if (isBlobColumn(col)) {
                cols.append(columnName).append(",");
                values.append("null,");
                continue;
            }
            if (isMultColumn(col, table, stratege) || multColumnList.contains(columnName)) {
                char endChar = columnName.charAt(columnName.length() - 1);
                if (!Character.isDigit(endChar)) {
                    cols.append(columnName).append(",");
                    values.append("'~'").append(",");
                    multColumnList.clear();
                    multColumnList.add(String.valueOf(columnName) + "2");
                    multColumnList.add(String.valueOf(columnName) + "3");
                    multColumnList.add(String.valueOf(columnName) + "4");
                    multColumnList.add(String.valueOf(columnName) + "5");
                    multColumnList.add(String.valueOf(columnName) + "6");
                }
                continue;
            }
            cols.append(columnName).append(",");
            values.append(
                    SqlUtil.formatSql(entry.getValue(), col.getDataType()))
                    .append(",");
        }
        cols.deleteCharAt(cols.length() - 1);
        values.deleteCharAt(values.length() - 1);
        if (bHasPK)
            singleSql.append(sbPKColumns);
        singleSql.append(cols.toString()).append(") values").append("(");
        if (bHasPK)
            singleSql.append(sbPKValues.toString());
        singleSql.append(values.toString()).append(")").append(separator);
        return singleSql.toString();
    }

    public static boolean isBlobColumn(IColumn col) {
        if (!col.getTypeName().equalsIgnoreCase("image") &&
                !col.getTypeName().equalsIgnoreCase("blob") &&
                !col.getTypeName().equalsIgnoreCase("blob(128m)"))
            return false;
        return true;
    }

    public static boolean isMultColumn(IColumn col, ITable table, IScriptExportStratege stratege) {
        if (stratege instanceof InitDataExportStratege2) {
            Map<String, List<String>> mlTableInfo = ((InitDataExportStratege2) stratege)
                    .getMlTableInfo();
            List<String> list = (List) mlTableInfo.get(table.getName());
            if (list != null)
                return list.contains(col.getName());
        }
        return false;
    }
}
