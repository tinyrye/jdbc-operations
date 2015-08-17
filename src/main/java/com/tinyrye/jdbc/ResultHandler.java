package com.tinyrye.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * On hold for now.
 */
@FunctionalInterface
public interface ResultHandler
{
	void process(ResultSet resultSet) throws SQLException;
}