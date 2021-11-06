package com.yingling.script.studio.connection.rsp;


import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObjectListResultSetProcessor<T> extends Object implements IResultSetProcessor<List<T>> {
    //    protected static Logger logger = LoggerFactory.getLogger(ObjectListResultSetProcessor.class.getName());
    private Class<T> clazz;

    public ObjectListResultSetProcessor(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> process(ResultSet rs) throws SQLException {
        List<T> list = new ArrayList<T>();
        ResultSetMetaData metaData = rs.getMetaData();
        while (rs.next()) {
            try {
                T obj = (T) this.clazz.newInstance();
                Field[] fields = this.clazz.getDeclaredFields();
                byte b;
                int j;
                Field[] arrayOfField = new Field[fields.length];
                for (b = 0; b < arrayOfField.length; b++) {
                    Field field = arrayOfField[b];
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        String columnName = metaData.getColumnName(i);
                        if (field.getName().toLowerCase()
                                .equals(columnName.toLowerCase())) {
                            Object value = rs.getObject(i);
                            setProperty(obj, field, value,
                                    metaData.getColumnType(i));
                            break;
                        }
                    }
                }
                list.add(obj);
            } catch (InstantiationException | IllegalAccessException e) {
//                logger.error(e.getMessage(), e);
            }
        }
        return list;
    }

    private void setProperty(T obj, Field field, Object value, int columnType) {
        if (value instanceof String && ((String) value).equals("~"))
            value = null;
        Class<?> fieldType = field.getType();
        if ((fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) && (
                columnType == 1 || columnType == 12 ||
                        columnType == -9 || columnType == -15)) {
            boolean boolValue = !(value == null ||
                    value.toString().trim().toUpperCase().equals("N"));
            ReflectionUtil.setProperty(obj, field.getName(), Boolean.valueOf(boolValue));
        } else if (fieldType.equals(Integer.class) ||
                fieldType.equals(int.class)) {
            try {
                if (value == null) {
                    if (fieldType.equals(Integer.class))
                        ReflectionUtil.setProperty(obj, field.getName(), value);
                } else {
                    Integer intValue = Integer.valueOf(value.toString());
                    ReflectionUtil.setProperty(obj, field.getName(), intValue);
                }
            } catch (NumberFormatException e) {
//                logger.error(e.getMessage(), e);
            }
        } else {
            ReflectionUtil.setProperty(obj, field.getName(), value);
        }
    }
}
