package com.tinyrye.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Consumer;
import javax.sql.DataSource;

public class SQLInsert extends SQLOperation<InsertResult>
{
    private String sql;
    
    public SQLInsert(DataSource connectionProvider) {
        super(connectionProvider);
    }
    
    public SQLInsert sql(String sql) { this.sql = sql; return this; }
    public SQLInsert parameterSetter(ParameterSetter parameterSetter) { super.parameterSetter(parameterSetter); return this; }

    public void callForFirstGeneratedKeys(OperationValues values, Consumer<List<Integer>> generatedKeysConsumer)
    {
        InsertResult insertResult = call(values);
        if ((insertResult != null) && (insertResult.generatedKeys.size() > 0)) {
            generatedKeysConsumer.accept(insertResult.generatedKeys.get(0));
        }
    }

    @Override
    protected InsertResult performOperation(Connection connection,
        List values,
        List<AutoCloseable> closeables) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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