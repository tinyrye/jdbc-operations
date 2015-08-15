package com.tinyrye.jdbc;

import org.apache.commons.dbcp.BasicDataSource;

public class DataSourceFactory
{
	public BasicDataSource newBasicDbcpInstance(String url, String driverClassName) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(url);
		dataSource.setDriverClassName(driverClassName);
		return dataSource;
	}
}