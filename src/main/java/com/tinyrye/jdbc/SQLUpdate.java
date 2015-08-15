package com.tinyrye.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLUpdate extends SQLOperation
{
	private String updateSql;
	private ParameterSetter parameterSetter;
	private PreparedStatement statement;
	private int updateResult;

	public SQLUpdate(Connection connection) {
		super(connection);
	}

	public SQLUpdate updateSql(String updateSql) { this.updateSql = updateSql; return this; }
	public SQLUpdate parameterSetter(ParameterSetter parameterSetter) { this.parameterSetter = parameterSetter; return this; }
	
	@Override
	public void performOperation() throws SQLException {
		statement = connection.prepareStatement(updateSql);
		if (parameterSetter != null) parameterSetter.setValues(statement);
		updateResult = statement.executeUpdate();
	}
	
	@Override
	public void close() throws SQLException {
		statement = close(statement);
	}
	
	public int update() throws SQLException {
		run();
		return getUpdateResult();
	}
	
	public int getUpdateResult() {
		return updateResult;
	}
}
