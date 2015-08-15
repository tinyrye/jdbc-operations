package com.tinyrye.jdbc.getters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.joda.time.DateTime;

import com.tinyrye.jdbc.ResultByNameGetter;

public class GetResultTimestampByName implements ResultByNameGetter<DateTime>
{
	@Override
	public DateTime get(ResultSet row, String name) throws SQLException {
		Timestamp timestamp = row.getTimestamp(name);
		if (timestamp != null) return new DateTime(timestamp.getTime());
		else return null;
	}
}