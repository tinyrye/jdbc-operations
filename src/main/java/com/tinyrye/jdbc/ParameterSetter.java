package com.tinyrye.jdbc;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

public class ParameterSetter
{
    public final void setValues(List values, PreparedStatement statement)
        throws SQLException
    {
        for (int i = 0; i < values.size(); i++) {
            setValue(i + 1, values.get(i), statement);
        }
    }
    
    public void setValue(int index, Object value, PreparedStatement statement)
        throws SQLException
    {
        if (value == null) {
            statement.setObject(index, value);
        }
        else
        {
            if (value.getClass().equals(Integer.class)) {
                statement.setInt(index, ((Integer) value).intValue());
            }
            else if (value.getClass().equals(Long.class)) {
                statement.setLong(index, ((Long) value).longValue());
            }
            else if (value.getClass().equals(Float.class)) {
                statement.setFloat(index, ((Float) value).floatValue());
            }
            else if (value.getClass().equals(Double.class)) {
                statement.setDouble(index, ((Double) value).doubleValue());
            }
            else if (value.getClass().equals(BigDecimal.class)) {
                statement.setDouble(index, ((BigDecimal) value).doubleValue());
            }
            else if (value.getClass().equals(Boolean.class)) {
                statement.setBoolean(index, (Boolean) value);
            }
            else if (value.getClass().equals(InputStream.class)) {
                statement.setBlob(index, (InputStream) value);
            }
            else if (value.getClass().equals(String.class)) {
                statement.setString(index, (String) value);
            }
            else if (value.getClass().equals(Instant.class)) {
                statement.setTimestamp(index, new Timestamp(((Instant) value).toEpochMilli()));
            }
            else if (value.getClass().equals(OffsetDateTime.class)) {
                statement.setTimestamp(index, new Timestamp(((OffsetDateTime) value).toEpochSecond() * 1000L));
            }
            else if (value instanceof Enum) {
                statement.setString(index, ((Enum) value).name());
            }
            else {
                statement.setObject(index, value);
            }
        }
    }
}