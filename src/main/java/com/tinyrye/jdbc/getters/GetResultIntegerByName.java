package com.tinyrye.jdbc.getters;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GetResultIntegerByName extends WasNullCheckThenReturn<Integer>
{
	@Override
	protected Integer getInitial(ResultSet row, String name) throws SQLException {
		return (Integer) row.getInt(name);
	}
}