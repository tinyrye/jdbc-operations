package com.softwhistle.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.BiFunction;
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

    @Override
    protected InsertResult performOperation(Connection connection,
        List values,
        OperationResourceManager closeables) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement(sql.get(), Statement.RETURN_GENERATED_KEYS);
        closeables.add(statement);
        parameterSetter.setValues(values, statement);
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        closeables.add(generatedKeys);
        return new ResultHandler(generatedKeys).map((rs, valUtil) ->
            Arrays.asList(valUtil.convert(rs, Integer.class, 1)))
                .stream().collect(() -> new InsertResult(),
                    (ir, rowKeys) -> ir.addRowGeneratedKeys(rowKeys),
                    (ir1, ir2) -> ir1.combine(ir2));
    }
}
