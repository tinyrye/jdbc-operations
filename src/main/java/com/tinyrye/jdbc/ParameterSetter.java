package com.tinyrye.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ParameterSetter
{
	void setValues(PreparedStatement statement) throws SQLException;
}