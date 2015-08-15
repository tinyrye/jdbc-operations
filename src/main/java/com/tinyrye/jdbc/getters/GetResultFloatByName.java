package com.tinyrye.jdbc.getters;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GetResultFloatByName extends WasNullCheckThenReturn<Float>
{
	@Override
	protected Float getInitial(ResultSet row, String name) throws SQLException {
		return (Float) row.getFloat(name);
	}
}