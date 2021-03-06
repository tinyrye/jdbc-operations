package com.softwhistle.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLQuery extends SQLOperation<ResultHandler>
{
    private static final Logger LOG = LoggerFactory.getLogger(SQLQuery.class);

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
        OperationResourceManager closeables) throws SQLException
    {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String sql = this.sql.get();
        LOG.debug("Executing query: query={}; parameterValues={}", new Object[] {
            sql, values
        });
        statement = connection.prepareStatement(sql);
        closeables.add(statement);
        parameterSetter.setValues(values, statement);
        resultSet = statement.executeQuery();
        OperationResourceManager deferredCloseables = new OperationResourceManager(closeables);
        // ResultHandler usage must close the statement and result set
        return new ResultHandler(resultSet).takeoverFor(deferredCloseables);
    }
}