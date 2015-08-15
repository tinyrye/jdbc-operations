package com.tinyrye.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLQuery extends SQLOperation
{
	private String querySql;
	private ParameterSetter parameterSetter;
	private ResultHandler rowHandler;
	private PreparedStatement statement;
	private ResultSet resultSet;

	public SQLQuery(Connection connection) {
		super(connection);
	}

	public SQLQuery querySql(String querySql) { this.querySql = querySql; return this; }
	public SQLQuery parameterSetter(ParameterSetter parameterSetter) { this.parameterSetter = parameterSetter; return this; }

	@Override
	public void performOperation() throws SQLException {
		statement = connection.prepareStatement(querySql);
		if (parameterSetter != null) parameterSetter.setValues(statement);
		resultSet = statement.executeQuery();
		rowHandler.process(resultSet);
	}

	@Override
	public void close() throws SQLException {
		statement = close(statement);
		resultSet = close(resultSet);
	}
	
	public <T> List<T> queryRows(RowConverter<T> rowConverter) throws SQLException {
		List<T> rowList = new ArrayList<T>();
		rowHandler = rowConverter.pipeResultsTo(rowList);
		run();
		return rowList;
	}
}