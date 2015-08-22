package com.tinyrye.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.sql.DataSource;

public class SQLInsert extends SQLOperation<InsertResult>
{
    private Supplier<String> sql;
    
    public SQLInsert(DataSource connectionProvider) {
        super(connectionProvider);
    }
    
    public SQLInsert sql(String sql) { this.sql = (() -> sql); return this; }
    public SQLInsert sql(Supplier<String> sql) { this.sql = sql; return this; }
    public SQLInsert parameterSetter(ParameterSetter parameterSetter) { super.parameterSetter(parameterSetter); return this; }

    /**
     * Majority use-case is insert single record into table that has one PK that auto-generates.
     * Perform insert with parameter values from <code>values</code>.
     * @return The auto-generated key of the first row
     */
    public Optional<Integer> callForFirstGeneratedKey(OperationValues values)
    {
        InsertResult insertResult = call(values);
        if ((insertResult != null) && (insertResult.generatedKeys.size() > 0)
                && (insertResult.generatedKeys.get(0).size() > 0))
        {
            return Optional.of(insertResult.generatedKeys.get(0).get(0));
        }
        else {
            return Optional.of((Integer) null);
        }
    }

    /**
     * Return a list of auto-generated keys from a single record insert.  Whereas
     * {@link callForFirstGeneratedKey} assumes only one auto-generated key exists
     * as a result of insert, this method will return all keys.
     * @return all auto-generated keys of first record inserted
     */
    public Optional<List<Integer>> callForFirstGeneratedKeys(OperationValues values)
    {
        InsertResult insertResult = call(values);
        if ((insertResult != null) && (insertResult.generatedKeys.size() > 0)) {
            return Optional.of(insertResult.generatedKeys.get(0));
        }
        else {
            return Optional.of((List<Integer>) null);
        }
    }

    @Override
    protected InsertResult performOperation(Connection connection,
        List values,
        List<AutoCloseable> closeables) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement(sql.get(), Statement.RETURN_GENERATED_KEYS);
        closeables.add(statement);
        parameterSetter.setValues(values, statement);
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        closeables.add(generatedKeys);
        return extractGeneratedKeys(new InsertResult(), generatedKeys);
    }

    protected InsertResult extractGeneratedKeys(InsertResult result, ResultSet generatedKeys) throws SQLException
    {
        while (generatedKeys.next()) {
            result.addRowGeneratedKey((Integer) generatedKeys.getInt(1));
        }
        return result;
    }
}