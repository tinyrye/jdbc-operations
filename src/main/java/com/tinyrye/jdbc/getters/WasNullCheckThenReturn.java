package com.tinyrye.jdbc.getters;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.tinyrye.jdbc.ResultByNameGetter;

public abstract class WasNullCheckThenReturn<T> implements ResultByNameGetter<T>
{
	@Override
	public T get(ResultSet row, String name) throws SQLException {
		T initialValue = getInitial(row, name);
		if (! row.wasNull()) return initialValue;
		else return (T) null;
	}
	
	protected abstract T getInitial(ResultSet row, String name) throws SQLException;
}