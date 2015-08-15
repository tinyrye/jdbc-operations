package com.tinyrye.jdbc.getters;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GetResultDoubleByName extends WasNullCheckThenReturn<Double>
{
	@Override
	protected Double getInitial(ResultSet row, String name) throws SQLException {
		return (Double) row.getDouble(name);
	}
}