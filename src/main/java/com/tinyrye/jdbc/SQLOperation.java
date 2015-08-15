package com.tinyrye.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SQLOperation implements Runnable
{
	/**
	 * Operation will not close connection; only resources
     * for performing operation are closed. */
	protected final Connection connection;

	public SQLOperation(Connection connection) {
		this.connection = connection;
	}

	private boolean closeQuietly;

	public abstract void performOperation() throws SQLException;
	public abstract void close() throws SQLException;

	public SQLOperation closeQuietly() { this.closeQuietly = true; return this; }

	public void run()
	{
		try { performOperation(); }
		catch (SQLException ex) { throw new RuntimeException(ex); }
		finally
		{
			try { close(); }
			catch (SQLException ex) {
				if (! closeQuietly) throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Support method for you if you have Statements, ResultSets, etc. to close.
	 * Usage is typically:
	 * <code>
	 *   statement = close(statement);
	 * </code>
	 * @return <code>null</code> unconditially so you can set your resource to null
	 * after closure
	 */
	protected <C extends AutoCloseable> C close(ResultSet resource) throws SQLException
	{
		if (resource != null) {
			resource.close();
		}
		return null;
	}

	/**
	 * Support method for you if you have Statements, ResultSets, etc. to close.
	 * Usage is typically:
	 * <code>
	 *   statement = close(statement);
	 * </code>
	 * @return <code>null</code> unconditially so you can set your resource to null
	 * after closure
	 */
	protected <C extends AutoCloseable> C close(Statement resource) throws SQLException
	{
		if (resource != null) {
			resource.close();
		}
		return null;
	}
}