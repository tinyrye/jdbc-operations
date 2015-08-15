package com.tinyrye.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultHandler
{
	void process(ResultSet resultSet) throws SQLException;
}