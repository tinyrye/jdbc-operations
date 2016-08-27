package com.softwhistle.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

/**
 * Use RowConverter to take advantage of built-in/passed ResultSetValueConverter.
 * Not required - essentially an extension for convenience and convention.
 * 
 * Implementing {@link convertRow} means you will use the valueUtility to
 * extract your values so that it becomes the valueUtility's responsibility
 * to decide the best compatibility and equivalence between the column and
 * the Java object value.
 */
@FunctionalInterface
public interface RowConverter<T>
{
    /**
     * @param valueUtility ResultHandler defines and provides this utility so that
     * the converter defers supportability to this base valueUtility for the full
     * spectrum of the application DAO's column/properties types.
     */
	T convertRow(ResultSet rows, ResultSetValueConverter valueUtility);
}
