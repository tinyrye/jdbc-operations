package com.tinyrye.jdbc;

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
    
    public T call(OperationValues values) {
    	List<AutoCloseable> closeables = new ArrayList<AutoCloseable>();
    	T result = null;
        try { result = performOperation(connectionProvider.getConnection(), values.values(), closeables); }
        catch (SQLException ex) { throw new RuntimeException(ex); }
        finally { closeables.stream().forEach(closeable -> closeQuietly(closeable)); }
        return result;
    }
    
    /**
     * Run the operation.
     * @param closeables add your resources to reclaim to this list.
     */
    protected abstract T performOperation(Connection connection,
    	List values,
    	List<AutoCloseable> closeables)
    		throws SQLException;
    
    /**
     * Support method for you if you have Statements, ResultSets, etc. to close.
     * Usage is typically:
     * <code>
     *   statement = close(statement);
     * </code>
     * @return <code>null</code> unconditially so you can set your resource to null
     * after closure
     */
    protected void closeQuietly(AutoCloseable resource) {
        try { if (resource != null) resource.close(); }
        catch (Exception ex) { /* shhhh! */ }
    }
}