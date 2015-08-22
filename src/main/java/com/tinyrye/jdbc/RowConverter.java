package com.tinyrye.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

/**
 * Use RowConverter to take advantage of built-in/passed ResultSetValueConverter.
 * Not required - essentially an extension for convenience and convention.
 */
@FunctionalInterface
public interface RowConverter<T>
{
	T convertRow(ResultSet rows, ResultSetValueConverter valueConverter);
}