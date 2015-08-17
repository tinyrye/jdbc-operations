package com.tinyrye.jdbc;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class ResultSetValueConverter
{
    protected class ConversionTypeMapping<T>
    {
        public Class<T> targetType;
        public int columnType;
        @Override public boolean equals(Object that) {
            if (that == null) return false;
            else if (that == this) return true;
            else if (! (that instanceof ConversionTypeMapping)) return false;
            else return equals((ConversionTypeMapping<T>) that);
        }
        @Override public int hashCode() {
            return Arrays.hashCode(new Object[] { targetType, columnType });
        }
        public boolean equals(ConversionTypeMapping<T> that) {
            return this.targetType.equals(that.targetType)
                        && this.columnType == that.columnType;
        }
    }

    /**
     * A function meant for a single {@link ConversionTypeMapping}
     */
    @FunctionalInterface
    public interface Conversion<T> {
        T convert(ResultSet rs, int columnNumber) throws SQLException;
    }

    private Map<ConversionTypeMapping<?>,Conversion<?>> conversions = new HashMap<ConversionTypeMapping<?>,Conversion<?>>();
    
    public ResultSetValueConverter()
    {
        addConversions(Integer.class, (rs, colNum) -> rs.getInt(colNum), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Long.class, (rs, colNum) -> rs.getLong(colNum), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Long.class, (rs, colNum) -> rs.getTimestamp(colNum).getTime(), Types.TIMESTAMP);
        addConversions(Double.class, (rs, colNum) -> new Double(rs.getDouble(colNum)), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addConversions(Float.class, (rs, colNum) -> new Float(rs.getDouble(colNum)), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addConversions(BigDecimal.class, (rs, colNum) -> new BigDecimal(rs.getLong(colNum)), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(String.class, (rs, colNum) -> rs.getString(colNum), Types.VARCHAR, Types.CHAR);
        addConversions(Boolean.class, (rs, colNum) -> rs.getBoolean(colNum), Types.BOOLEAN, Types.BIT);
          // ASSUMPTION: the Driver's ResultSet.getBoolean handles BIT column type.
        addConversions(Instant.class, (rs, colNum) -> Instant.ofEpochMilli(rs.getLong(colNum)), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Instant.class, (rs, colNum) -> Instant.ofEpochMilli(rs.getTimestamp(colNum).getTime()), Types.TIMESTAMP);
        addConversions(OffsetDateTime.class, (rs, colNum) -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp(colNum).getTime()), ZoneId.of("UTC")), Types.TIMESTAMP);
        addConversions(OffsetDateTime.class, (rs, colNum) -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(rs.getLong(colNum)), ZoneId.of("UTC")), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Byte[].class, (rs, colNum) -> new Byte[0], Types.BINARY, Types.BLOB, Types.BIT);
        addConversions(InputStream.class, (rs, colNum) -> rs.getBinaryStream(colNum), Types.BINARY, Types.BLOB, Types.BIT);
        addConversions(List.class, (rs, colNum) -> {
                String csv = rs.getString(colNum);
                if (csv != null) return new ArrayList<String>(Arrays.asList(csv.split(",")));
                else return new ArrayList<String>();
            }, Types.VARCHAR);
    }
    
    public <T> ResultSetValueConverter addConversions(Class<T> targetType, Conversion<T> conversion, int ... columnTypes)
    {
        for (int columnType: columnTypes) {
            conversions.put(conversionMapping(targetType, columnType), conversion);
        }
        return this;
    }
    
    public <T> ConversionTypeMapping<T> conversionMapping(Class<T> targetType, int columnType) {
        ConversionTypeMapping<T> mapping = new ConversionTypeMapping<T>();
        mapping.targetType = targetType;
        mapping.columnType = columnType;
        return mapping;
    }

    public ConversionTypeMapping<?> getMapping(Class<?> targetType, int columnType) {
        return conversions.keySet().stream().filter(mapping -> mapping.targetType.equals(targetType)
                    && (mapping.columnType == columnType)).findFirst().get();
    }
    
    public <T> T convert(ResultSet rs, Class<T> target, String columnName) throws SQLException
    {
        int columnNumber = rs.findColumn(columnName);
        int columnType = sqlType(rs, columnNumber);
        Conversion<?> standardConversion = conversions.get(conversionMapping(target, columnType));
        if (standardConversion != null) {
            return ((Conversion<T>) standardConversion).convert(rs, columnNumber);
        }
        else
        {
            if (conversions.containsKey(conversionMapping(target, columnType))) {
                System.out.println("WTF mate");
            }
            if (Enum.class.isAssignableFrom(target)) {
                return (T) convertToEnum(rs, columnNumber, (Class<Enum>) target);
            }
            else {
                throw new NoSuchElementException("No natural conversion found.");
            }
        }
    }
    
    protected <E extends Enum<E>> E convertToEnum(ResultSet rs, int columnNumber, Class<E> enumClass) throws SQLException {
        return convertToEnum(rs.getString(columnNumber), enumClass);
    }
    
    protected <E extends Enum<E>> E convertToEnum(String value, Class<E> enumClass) {
        return Enum.valueOf(enumClass, value);
    }

    protected int sqlType(ResultSet rs, int columnNumber) throws SQLException {
        return rs.getMetaData().getColumnType(columnNumber);
    }

    protected void logConversionMatch(Class<?> target, int columnType, Conversion<?> conversionByGet, ConversionTypeMapping<?> mappingByGetMapping) {
        if (conversionByGet == null && mappingByGetMapping != null) {
            System.out.println("Preemptive WTF mate");
        }
    }
}