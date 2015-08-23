package com.tinyrye.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.sql.DataSource;

public class SQLQuery extends SQLOperation<ResultHandler>
{
    private Supplier<String> sql;

    public SQLQuery(DataSource connectionProvider) {
        super(connectionProvider);
    }
    
    public SQLQuery sql(String sql) { this.sql = (() -> sql); return this; }
    public SQLQuery sql(Supplier<String> sql) { this.sql = sql; return this; }
    public SQLQuery parameterSetter(ParameterSetter parameterSetter) { super.parameterSetter(parameterSetter); return this; }
    
    @Override
    protected ResultHandler performOperation(Connection connection,
        List values,
        List<AutoCloseable> closeables) throws SQLException
    {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        statement = connection.prepareStatement(sql.get());
        // closeables.add(statement);
        parameterSetter.setValues(values, statement);
        resultSet = statement.executeQuery();
        // ResultHandler usage will close the result set
        return new ResultHandler(resultSet);
    }
}