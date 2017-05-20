package com.softwhistle.jdbc;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;

import com.softwhistle.io.Operations;

public class ParameterSetter
{
    private static final Logger LOG = LoggerFactory.getLogger(ParameterSetter.class);

    @FunctionalInterface
    public static interface Setter<T> {
        public void setValue(PreparedStatement statement, int columnNumber, T value) throws SQLException;
    }
    
    private final Map<ConversionTypeMapping<?>,Setter<?>> setters = new HashMap<ConversionTypeMapping<?>,Setter<?>>();

    public ParameterSetter() {
        addSetter(String.class, (stmt, colNum, val) -> stmt.setString(colNum, val), Types.VARCHAR, Types.CHAR);
        addSetter(Integer.class, (stmt, colNum, val) -> stmt.setInt(colNum, val.intValue()), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addSetter(Long.class, (stmt, colNum, val) -> stmt.setLong(colNum, val.longValue()), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addSetter(Long.class, (stmt, colNum, val) -> stmt.setTimestamp(colNum, new Timestamp(val)), Types.TIMESTAMP);
        addSetter(Double.class, (stmt, colNum, val) -> stmt.setDouble(colNum, val.doubleValue()), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addSetter(Float.class, (stmt, colNum, val) -> stmt.setFloat(colNum, val.floatValue()), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addSetter(BigDecimal.class, (stmt, colNum, val) -> stmt.setDouble(colNum, val.doubleValue()), Types.REAL, Types.NUMERIC, Types.DOUBLE, Types.FLOAT, Types.DECIMAL);
        addSetter(BigDecimal.class, (stmt, colNum, val) -> stmt.setLong(colNum, val.longValue()), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addSetter(Boolean.class, (stmt, colNum, val) -> stmt.setBoolean(colNum, val.booleanValue()), Types.BOOLEAN, Types.BIT);
          // ^^^ ASSUMPTION: the Driver's ResultSet.getBoolean handles BIT column type. ^^^
        addSetter(Instant.class, (stmt, colNum, val) -> stmt.setLong(colNum, val.getEpochSecond() * 1000L), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addSetter(Instant.class, (stmt, colNum, val) -> stmt.setTimestamp(colNum, new Timestamp(val.getEpochSecond() * 1000L)), Types.TIMESTAMP);
        addSetter(OffsetDateTime.class, (stmt, colNum, val) -> stmt.setTimestamp(colNum, new Timestamp(val.toInstant().getEpochSecond() * 1000L)), Types.TIMESTAMP);
        addSetter(OffsetDateTime.class, (stmt, colNum, val) -> stmt.setLong(colNum, val.toInstant().getEpochSecond() * 1000L), Types.BIGINT, Types.SMALLINT, Types.TINYINT, Types.INTEGER);
        addSetter(byte[].class, (stmt, colNum, val) -> stmt.setBytes(colNum, val), Types.BINARY, Types.BLOB, Types.BIT);
        addSetter(InputStream.class, (stmt, colNum, val) -> stmt.setBytes(colNum, toByteArray(val)), Types.BINARY, Types.BLOB, Types.BIT);
        addSetter(List.class, (stmt, colNum, val) -> stmt.setString(colNum, Joiner.on(",").join(val)), Types.VARCHAR);
    }
    
    public <T> ParameterSetter addSetter(Class<T> objectType, Setter<T> setter, int ... columnTypes)
    {
        for (int columnType: columnTypes) {
            setters.put(ConversionTypeMapping.of(objectType, columnType), setter);
        }
        return this;
    }
    
    public final void setValues(List values, PreparedStatement statement) {
        for (int columnNumber = 1; columnNumber <= values.size(); columnNumber++) {
            setValue(statement, columnNumber, values.get(columnNumber - 1));
        }
    }
    
    public void setValue(PreparedStatement statement, int columnNumber, Object value)
    {
        if (value == null) {
            setNullValue(statement, columnNumber);
        }
        else
        {
            final int columnType = sqlType(statement, columnNumber);
            LOG.debug("Setting parameter value: columnNumber={}; valueType={}; columnType={}", new Object[] {
                columnNumber, value.getClass().getName(), sqlTypeCode(columnType)
            });
            final Setter<?> standardSetter = setters.get(ConversionTypeMapping.of(value.getClass(), columnType));
            if (standardSetter != null) {
                setValueWith(standardSetter, statement, value, columnNumber);
            }
            else if (value instanceof Enum) {
                setEnumValue(statement, (Enum) value, columnNumber);
            }
            else {
                throw new NoSuchElementException("No natural setter found.");
            }
        }
    }
    
    protected void setNullValue(PreparedStatement statement, int columnNumber) {
        try { statement.setObject(columnNumber, (Object) null); }
        catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    
    protected void setValueWith(Setter setter, PreparedStatement statement, Object value, int columnNumber) {
        try { setter.setValue(statement, columnNumber, value); }
        catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    
    protected <E extends Enum<E>> void setEnumValue(PreparedStatement statement, E value, int columnNumber) {
        try { statement.setString(columnNumber, Optional.of(value).map(val -> val.name()).orElse(null)); }
        catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    protected int sqlType(PreparedStatement statement, int columnNumber) {
        try { return statement.getParameterMetaData().getParameterType(columnNumber); }
        catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    protected String sqlTypeCode(int sqlType) {
        if (sqlType == Types.ARRAY) return "ARRAY";
        else if (sqlType == Types.BIGINT) return "BIGINT";
        else if (sqlType == Types.BINARY) return "BINARY";
        else if (sqlType == Types.BIT) return "BIT";
        else if (sqlType == Types.BLOB) return "BLOB";
        else if (sqlType == Types.BOOLEAN) return "BOOLEAN";
        else if (sqlType == Types.CHAR) return "CHAR";
        else if (sqlType == Types.CLOB) return "CLOB";
        else if (sqlType == Types.DATALINK) return "DATALINK";
        else if (sqlType == Types.DATE) return "DATE";
        else if (sqlType == Types.DECIMAL) return "DECIMAL";
        else if (sqlType == Types.DISTINCT) return "DISTINCT";
        else if (sqlType == Types.DOUBLE) return "DOUBLE";
        else if (sqlType == Types.FLOAT) return "FLOAT";
        else if (sqlType == Types.INTEGER) return "INTEGER";
        else if (sqlType == Types.JAVA_OBJECT) return "JAVA_OBJECT";
        else if (sqlType == Types.LONGNVARCHAR) return "LONGNVARCHAR";
        else if (sqlType == Types.LONGVARBINARY) return "LONGVARBINARY";
        else if (sqlType == Types.LONGVARCHAR) return "LONGVARCHAR";
        else if (sqlType == Types.NCHAR) return "NCHAR";
        else if (sqlType == Types.NCLOB) return "NCLOB";
        else if (sqlType == Types.NULL) return "NULL";
        else if (sqlType == Types.NUMERIC) return "NUMERIC";
        else if (sqlType == Types.NVARCHAR) return "NVARCHAR";
        else if (sqlType == Types.OTHER) return "OTHER";
        else if (sqlType == Types.REAL) return "REAL";
        else if (sqlType == Types.REF) return "REF";
        else if (sqlType == Types.REF_CURSOR) return "REF_CURSOR";
        else if (sqlType == Types.ROWID) return "ROWID";
        else if (sqlType == Types.SMALLINT) return "SMALLINT";
        else if (sqlType == Types.SQLXML) return "SQLXML";
        else if (sqlType == Types.STRUCT) return "STRUCT";
        else if (sqlType == Types.TIME) return "TIME";
        else if (sqlType == Types.TIME_WITH_TIMEZONE) return "TIME_WITH_TIMEZONE";
        else if (sqlType == Types.TIMESTAMP) return "TIMESTAMP";
        else if (sqlType == Types.TIMESTAMP_WITH_TIMEZONE) return "TIMESTAMP_WITH_TIMEZONE";
        else if (sqlType == Types.TINYINT) return "TINYINT";
        else if (sqlType == Types.VARBINARY) return "VARBINARY";
        else if (sqlType == Types.VARCHAR) return "VARCHAR";
        else return "UNKNOWN";
    }

    protected byte[] toByteArray(InputStream inputStream) {
        return Operations.with(inputStream).apply((str) -> ByteStreams.toByteArray(str));
    }
}