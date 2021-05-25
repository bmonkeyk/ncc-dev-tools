package com.yonyou.common.database.powerdesigner.impl;

import com.yonyou.uap.studio.connection.rsp.ReflectionUtil;



import java.text.MessageFormat;
import java.util.Comparator;

public class PropertyComparator<T> extends Object implements Comparator<T> {
//    protected static Logger logger = LoggerFactory.getLogger(PropertyComparator.class.getName());
    private String property;

    private boolean ascending;

    public PropertyComparator(String property) {
        this(property, true);
    }

    public PropertyComparator(String property, boolean ascending) {
        this.property = property;
        this.ascending = ascending;
    }

    public int compare(T o1, T o2) {
        if (o1 == null)
            return (o2 == null) ? 0 : (this.ascending ? -1 : 1);
        if (o2 == null)
            return this.ascending ? 1 : -1;
        Object value1 = null;
        Object value2 = null;
        try {
            value1 = ReflectionUtil.getProperty(o1, this.property);
            value2 = ReflectionUtil.getProperty(o2, this.property);
        } catch (Exception e) {
            String msg = MessageFormat.format(
                    "Failed to get property {0} of Class: {1}", new Object[]{this.property,
                            o1.getClass().getName()});
//            logger.error(msg, e);
        }
        int temp = ((Comparable) value1).compareTo((Comparable) value2);
        return this.ascending ? temp : (0 - temp);
    }
}
