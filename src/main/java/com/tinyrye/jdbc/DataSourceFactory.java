package com.tinyrye.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;

public class DataSourceFactory
{
    @FunctionalInterface
    public static interface DataSourceInitializer {
        void configure(BasicDataSource dataSource);
    }
    
    public BasicDataSource newBasicDbcpInstance(String url, String driverClassName) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }

	public BasicDataSource newBasicDbcpInstance(String url, String driverClassName, DataSourceInitializer initializer) {
		BasicDataSource dataSource = newBasicDbcpInstance(url, driverClassName);
		initializer.configure(dataSource);
		return dataSource;
	}
}