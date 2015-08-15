package com.tinyrye.jdbc.getters;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GetResultBooleanByName extends WasNullCheckThenReturn<Boolean>
{
	@Override
	protected Boolean getInitial(ResultSet row, String name) throws SQLException {
		return (Boolean) row.getBoolean(name);
	}
}