package com.tinyrye.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

@FunctionalInterface
public interface RowConverter<T>
{
	T convertRow(ResultSet rows) throws SQLException;
}