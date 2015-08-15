package com.tinyrye.jdbc.getters;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GetResultLongByName extends WasNullCheckThenReturn<Long>
{
	@Override
	protected Long getInitial(ResultSet row, String name) throws SQLException {
		return (Long) row.getLong(name);
	}
}