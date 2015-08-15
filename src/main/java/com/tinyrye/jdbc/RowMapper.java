package com.tinyrye.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.tinyrye.jdbc.getters.GetResultStringByName;
import com.tinyrye.jdbc.getters.StandardGetters;

public class RowMapper extends RowConverter<Map<String,Object>>
{
	private static final ResultByNameGetter PROPERTY_BY_STRING = new GetResultStringByName();

	private final List<String> columnPropertyNames;
	private final Map<String,Object> propertyGetters;

	public RowMapper(List<String> columnPropertyNames, Map<String,Object> propertyGetters) {
		this.columnPropertyNames = columnPropertyNames;
		this.propertyGetters = propertyGetters;
	}

	@Override
	public Map<String,Object> convertRow(ResultSet row) throws SQLException
	{
		Map<String,Object> rowMap = new HashMap<String,Object>();
		for (int i = 0; i < columnPropertyNames.size(); i++) {
			String columnPropertyName = columnPropertyNames.get(i);
			ResultByNameGetter getter = resolvePropertyGetter(propertyGetters.get(columnPropertyName), columnPropertyName);
			rowMap.put(columnPropertyName, getter.get(row, columnPropertyName));
		}
		return rowMap;
	}
			
	public ResultByNameGetter resolvePropertyGetter(Object propertyGetter, String propertyName) {
		if (propertyGetter instanceof ResultByNameGetter) return (ResultByNameGetter) propertyGetter;
		else if (propertyGetter instanceof Class) return StandardGetters.getByNameGetter((Class) propertyGetter);
		else if (propertyGetter != null) throw new IllegalArgumentException(String.format("Property getter has to be a type or closure: %s", propertyName));
		else return PROPERTY_BY_STRING;
	}
}