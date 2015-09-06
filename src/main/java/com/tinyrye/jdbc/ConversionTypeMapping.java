package com.tinyrye.jdbc;

import java.util.Arrays;

public class ConversionTypeMapping<T>
{
    public static <T> ConversionTypeMapping<T> of(Class<T> objectType, int columnType) {
        ConversionTypeMapping<T> mapping = new ConversionTypeMapping<T>();
        mapping.objectType = objectType;
        mapping.columnType = columnType;
        return mapping;
    }

    public Class<T> objectType;
    public int columnType;

    @Override
    public boolean equals(Object that) {
        if (that == null) return false;
        else if (that == this) return true;
        else if (! (that instanceof ConversionTypeMapping)) return false;
        else return equals((ConversionTypeMapping<T>) that);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { objectType, columnType });
    }
    
    public boolean equals(ConversionTypeMapping<T> that) {
        return this.objectType.equals(that.objectType)
                    && this.columnType == that.columnType;
    }
}
