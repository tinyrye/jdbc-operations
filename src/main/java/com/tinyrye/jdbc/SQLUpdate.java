package com.tinyrye.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;
import javax.sql.DataSource;

/**
 * Meant for deletes or updates.
 */
public class SQLUpdate extends SQLOperation<Integer>
{
    private Supplier<String> sql;
    
	public SQLUpdate(DataSource connectionProvider) {
		super(connectionProvider);
	}
    
	public SQLUpdate sql(String sql) { this.sql = (() -> sql); return this; }
    public SQLUpdate sql(Supplier<String> sql) { this.sql = sql; return this; }
    public SQLUpdate parameterSetter(ParameterSetter parameterSetter) { super.parameterSetter(parameterSetter); return this; }
    
	@Override
    protected Integer performOperation(Connection connection,
    	List values,
    	OperationResourceManager closeables) throws SQLException
    {
		PreparedStatement statement = connection.prepareStatement(sql.get());
        closeables.add(statement);
		parameterSetter.setValues(values, statement);
		return statement.executeUpdate();
	}
}