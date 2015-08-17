package com.tinyrye.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class SQLQuery<T> extends SQLOperation<List<T>>
{
    private String sql;
    private RowConverter<T> rowConverter;

    public SQLQuery(DataSource connectionProvider) {
        super(connectionProvider);
    }
    
    public SQLQuery<T> sql(String sql) { this.sql = sql; return this; }
    public SQLQuery<T> parameterSetter(ParameterSetter parameterSetter) { super.parameterSetter(parameterSetter); return this; }
    public SQLQuery<T> rowConverter(RowConverter<T> rowConverter) { this.rowConverter = rowConverter; return this; }
    
    public T callForFirst(OperationValues values)
    {
        List<T> results = call(values);
        if (results != null) {
            if (results.size() == 1) return results.get(0);
            else throw new RuntimeException("More than one result.");
        }
        else return null;
    }
    
    @Override
    protected List<T> performOperation(Connection connection,
        List values,
        List<AutoCloseable> closeables) throws SQLException
    {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<T> resultValues = new ArrayList<T>();
        statement = connection.prepareStatement(sql);
        closeables.add(statement);
        parameterSetter.setValues(values, statement);
        resultSet = statement.executeQuery();
        closeables.add(resultSet);
        while (resultSet.next()) {
            resultValues.add(rowConverter.convertRow(resultSet));
        }
        return resultValues;
    }
}