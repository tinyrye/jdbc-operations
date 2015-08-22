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
import java.util.Optional;

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
        addConversions(Integer.class, (rs, colNum) -> rs.getObject(colNum, Integer.class), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Long.class, (rs, colNum) -> rs.getObject(colNum, Long.class), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Long.class, (rs, colNum) -> rs.getTimestamp(colNum).getTime(), Types.TIMESTAMP);
        addConversions(Double.class, (rs, colNum) -> rs.getObject(colNum, Double.class), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addConversions(Float.class, (rs, colNum) -> rs.getObject(colNum, Float.class), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addConversions(BigDecimal.class, (rs, colNum) -> optional(rs.getObject(colNum, Double.class)).map(colVal -> new BigDecimal(colVal)).orElse(null), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addConversions(BigDecimal.class, (rs, colNum) -> optional(rs.getObject(colNum, Long.class)).map(colVal -> new BigDecimal(colVal)).orElse(null), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(String.class, (rs, colNum) -> rs.getString(colNum), Types.VARCHAR, Types.CHAR);
        addConversions(Boolean.class, (rs, colNum) -> rs.getBoolean(colNum), Types.BOOLEAN, Types.BIT);
          // ^^^ ASSUMPTION: the Driver's ResultSet.getBoolean handles BIT column type. ^^^
        addConversions(Instant.class, (rs, colNum) -> optional(rs.getObject(colNum, Long.class)).map(colVal -> Instant.ofEpochMilli(colVal)).orElse(null), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Instant.class, (rs, colNum) -> optional(rs.getTimestamp(colNum)).map(colVal -> Instant.ofEpochMilli(colVal.getTime())).orElse(null), Types.TIMESTAMP);
        addConversions(OffsetDateTime.class, (rs, colNum) -> optional(rs.getTimestamp(colNum)).map(colVal -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(colVal.getTime()), ZoneId.of("UTC"))).orElse(null), Types.TIMESTAMP);
        addConversions(OffsetDateTime.class, (rs, colNum) -> optional(rs.getObject(colNum, Long.class)).map(colVal -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(colVal), ZoneId.of("UTC"))).orElse(null), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Byte[].class, (rs, colNum) -> new Byte[0], Types.BINARY, Types.BLOB, Types.BIT);
        addConversions(InputStream.class, (rs, colNum) -> rs.getBinaryStream(colNum), Types.BINARY, Types.BLOB, Types.BIT);
        addConversions(List.class, (rs, colNum) -> optional(rs.getString(colNum)).map(colVal -> new ArrayList<String>(Arrays.asList(colVal.split(",")))).orElse(new ArrayList<String>()), Types.VARCHAR);
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
    
    public <T> T convert(ResultSet rs, Class<T> target, String columnName)
    {
        final int columnNumber;
        final int columnType;
        try { columnNumber = rs.findColumn(columnName); } catch (SQLException ex) { throw new RuntimeException(ex); }
        try { columnType = sqlType(rs, columnNumber); } catch (SQLException ex) { throw new RuntimeException(ex); }
        Conversion<?> standardConversion = conversions.get(conversionMapping(target, columnType));
        if (standardConversion != null) {
            try { return ((Conversion<T>) standardConversion).convert(rs, columnNumber); }
            catch (SQLException ex) { throw new RuntimeException(ex); }
        }
        else
        {
            if (Enum.class.isAssignableFrom(target)) {
                try { return (T) convertToEnum(rs, columnNumber, (Class<Enum>) target); }
                catch (SQLException ex) { throw new RuntimeException(ex); }
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
    
    protected <T> Optional<T> optional(T columnValue) {
        return Optional.ofNullable(columnValue);
    }
}