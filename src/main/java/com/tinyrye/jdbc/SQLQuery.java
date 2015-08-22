package com.tinyrye.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.sql.DataSource;

public class SQLQuery<T> extends SQLOperation<ResultHandler>
{
    private Supplier<String> sql;
    private RowConverter<T> rowConverter;

    public SQLQuery(DataSource connectionProvider) {
        super(connectionProvider);
    }
    
    public SQLQuery<T> sql(String sql) { this.sql = (() -> sql); return this; }
    public SQLQuery<T> sql(Supplier<String> sql) { this.sql = sql; return this; }
    public SQLQuery<T> parameterSetter(ParameterSetter parameterSetter) { super.parameterSetter(parameterSetter); return this; }
    
    @Override
    protected ResultHandler performOperation(Connection connection,
        List values,
        List<AutoCloseable> closeables) throws SQLException
    {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<T> resultValues = new ArrayList<T>();
        statement = connection.prepareStatement(sql.get());
        closeables.add(statement);
        parameterSetter.setValues(values, statement);
        resultSet = statement.executeQuery();
        closeables.add(resultSet);
        return new ResultHandler(resultSet);
    }
}