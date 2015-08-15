package com.tinyrye.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultByNameGetter<T>
{
	T get(ResultSet row, String columnName) throws SQLException;
}