package com.softwhistle.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public abstract class SQLOperation<T>
{
    protected final DataSource connectionProvider;
	protected ParameterSetter parameterSetter = new ParameterSetter();
    
    public SQLOperation(DataSource connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
	
	public SQLOperation<T> parameterSetter(ParameterSetter parameterSetter) { this.parameterSetter = parameterSetter; return this; }
    
    public T call(OperationValues values)
    {
    	OperationResourceManager closeables = new OperationResourceManager();
    	T result = null;
        try {
            Connection connection = connectionProvider.getConnection();
            closeables.add(connection);
            result = performOperation(connection, values.values(), closeables);
        } catch (SQLException ex) { throw new RuntimeException(ex); }
        finally { closeables.close(); }
        return result;
    }
    
    /**
     * Run the operation.
     * @param closeables add your resources to reclaim to this list.
     */
    protected abstract T performOperation(Connection connection,
    	List values,
    	OperationResourceManager closeables)
    		throws SQLException;
}