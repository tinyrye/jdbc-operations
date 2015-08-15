package com.tinyrye.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

public abstract class RowConverter<T>
{
	public abstract T convertRow(ResultSet rows) throws SQLException;

	public ResultHandler pipeResultsTo(final List<T> rowList) {
		return new ResultHandler() {
			@Override public void process(ResultSet rows) throws SQLException {
				while (rows.next()) {
					rowList.add(convertRow(rows));
				}
			}
		};
	}
}