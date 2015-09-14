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
    /**
     * A function meant for a single {@link ConversionTypeMapping}
     */
    @FunctionalInterface
    public interface Conversion<T>
    {
        T convert(ResultSet resultSet, int columnNumber) throws SQLException;
    }

    private Map<ConversionTypeMapping<?>,Conversion<?>> conversions = new HashMap<ConversionTypeMapping<?>,Conversion<?>>();
    
    public ResultSetValueConverter()
    {
        addConversions(String.class, (rs, colNum) -> rs.getString(colNum), Types.VARCHAR, Types.CHAR);
        addConversions(Integer.class, (rs, colNum) -> optional(rs, (Integer) rs.getInt(colNum)).orElse(null), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Long.class, (rs, colNum) -> optional(rs, (Long) rs.getLong(colNum)).orElse(null), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Long.class, (rs, colNum) -> optional(rs, rs.getTimestamp(colNum)).map(colVal -> colVal.getTime()).orElse(null), Types.TIMESTAMP);
        addConversions(Double.class, (rs, colNum) -> optional(rs, (Double) rs.getDouble(colNum)).orElse(null), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addConversions(Float.class, (rs, colNum) -> optional(rs, (Float) rs.getFloat(colNum)).orElse(null), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addConversions(BigDecimal.class, (rs, colNum) -> optional(rs, (Double) rs.getDouble(colNum)).map(colVal -> new BigDecimal(colVal)).orElse(null), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addConversions(BigDecimal.class, (rs, colNum) -> optional(rs, (Long) rs.getLong(colNum)).map(colVal -> new BigDecimal(colVal)).orElse(null), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Boolean.class, (rs, colNum) -> optional(rs, (Boolean) rs.getBoolean(colNum)).orElse(null), Types.BOOLEAN, Types.BIT);
          // ^^^ ASSUMPTION: the Driver's ResultSet.getBoolean handles BIT column type. ^^^
        addConversions(Instant.class, (rs, colNum) -> optional(rs, (Long) rs.getLong(colNum)).map(colVal -> Instant.ofEpochMilli(colVal)).orElse(null), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Instant.class, (rs, colNum) -> optional(rs, rs.getTimestamp(colNum)).map(colVal -> Instant.ofEpochMilli(colVal.getTime())).orElse(null), Types.TIMESTAMP);
        addConversions(OffsetDateTime.class, (rs, colNum) -> optional(rs, rs.getTimestamp(colNum)).map(colVal -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(colVal.getTime()), ZoneId.of("UTC"))).orElse(null), Types.TIMESTAMP);
        addConversions(OffsetDateTime.class, (rs, colNum) -> optional(rs, (Long) rs.getLong(colNum)).map(colVal -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(colVal), ZoneId.of("UTC"))).orElse(null), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addConversions(Byte[].class, (rs, colNum) -> new Byte[0], Types.BINARY, Types.BLOB, Types.BIT);
        addConversions(InputStream.class, (rs, colNum) -> rs.getBinaryStream(colNum), Types.BINARY, Types.BLOB, Types.BIT);
        addConversions(List.class, (rs, colNum) -> optional(rs, rs.getString(colNum)).map(colVal -> new ArrayList<String>(Arrays.asList(colVal.split(",")))).orElse(new ArrayList<String>()), Types.VARCHAR);
    }
    
    public <T> ResultSetValueConverter addConversions(Class<T> objectType, Conversion<T> conversion, int ... columnTypes)
    {
        for (int columnType: columnTypes) {
            conversions.put(ConversionTypeMapping.of(objectType, columnType), conversion);
        }
        return this;
    }
    
    public <T> T convert(ResultSet resultSet, Class<T> target, String columnName) {
        return convert(resultSet, target, columnNumberByName(resultSet, columnName));
    }

    public <T> T convert(ResultSet resultSet, Class<T> target, int columnNumber)
    {
        final int columnType = sqlType(resultSet, columnNumber);
        final Conversion<?> standardConversion = conversions.get(ConversionTypeMapping.of(target, columnType));
        if (standardConversion != null) {
            return convertWith((Conversion<T>) standardConversion, resultSet, columnNumber);
        }
        else if (Enum.class.isAssignableFrom(target)) {
            return (T) convertToEnum(resultSet, columnNumber, (Class<Enum>) target);
        }
        else {
            throw new NoSuchElementException("No natural conversion found.");
        }
    }

    public <T> Optional<T> convertToOptional(ResultSet resultSet, Class<T> target, String columnName) {
        return Optional.ofNullable(convert(resultSet, target, columnName));
    }
    
    protected <T> T convertWith(Conversion<T> conversion, ResultSet resultSet, int columnNumber) {
        try { return conversion.convert(resultSet, columnNumber); }
        catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    
    protected <E extends Enum<E>> E convertToEnum(ResultSet resultSet, int columnNumber, Class<E> enumClass) {
        try { return convertToEnum(resultSet.getString(columnNumber), enumClass); }
        catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    
    protected <E extends Enum<E>> E convertToEnum(String value, Class<E> enumClass) {
        return Enum.valueOf(enumClass, value);
    }

    protected int columnNumberByName(ResultSet resultSet, String columnName) {
        try { return resultSet.findColumn(columnName); }
        catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    protected int sqlType(ResultSet resultSet, int columnNumber) {
        try { return resultSet.getMetaData().getColumnType(columnNumber); }
        catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    protected <T> Optional<T> optional(ResultSet resultSet, T conversionValue) {
        try { if (resultSet.wasNull()) conversionValue = null; }
        catch (SQLException ex) { throw new RuntimeException(ex); }
        return Optional.ofNullable(conversionValue);
    }
}