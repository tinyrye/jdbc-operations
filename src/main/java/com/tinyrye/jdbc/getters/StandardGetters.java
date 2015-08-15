package com.tinyrye.jdbc.getters;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.tinyrye.jdbc.ResultByNameGetter;

public class StandardGetters
{
	public static Map<Class,ResultByNameGetter> BY_NAME_GETTER_MAP_BY_TYPE = new HashMap<Class,ResultByNameGetter>();

	static
	{
		BY_NAME_GETTER_MAP_BY_TYPE.put(Integer.class, new GetResultIntegerByName());
		BY_NAME_GETTER_MAP_BY_TYPE.put(Long.class, new GetResultLongByName());
		BY_NAME_GETTER_MAP_BY_TYPE.put(Float.class, new GetResultFloatByName());
		BY_NAME_GETTER_MAP_BY_TYPE.put(Double.class, new GetResultDoubleByName());
		BY_NAME_GETTER_MAP_BY_TYPE.put(Boolean.class, new GetResultBooleanByName());
		BY_NAME_GETTER_MAP_BY_TYPE.put(DateTime.class, new GetResultTimestampByName());
	}

	public static ResultByNameGetter getByNameGetter(Class type) {
		return BY_NAME_GETTER_MAP_BY_TYPE.get(type);
	}
}