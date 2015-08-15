package com.tinyrye.jdbc.getters;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.tinyrye.jdbc.ResultByNameGetter;

public class GetResultStringByName implements ResultByNameGetter
{
	@Override
	public String get(ResultSet row, String name) throws SQLException {
		return row.getString(name);
	}
}