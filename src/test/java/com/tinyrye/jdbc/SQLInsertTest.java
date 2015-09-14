package com.tinyrye.jdbc;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;

import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SQLInsertTest
{
    public static class TestEntity {
        public Integer id;
        public String name;
        public Boolean value;
        public TestEntity id(Integer id) { this.id = id; return this; }
        public TestEntity name(String name) { this.name = name; return this; }
        public TestEntity value(Boolean value) { this.value = value; return this; }
    }

    @Mock
    private DataSource mockDataSource;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ParameterMetaData mockParameterMetaData;

    @Mock
    private ResultSet mockInsertGeneratedKeys;

    @Mock
    private ResultSetMetaData mockInsertGeneratedKeysMetaData;

    @Test
    public void testCall() throws Exception
    {
        String testSql = "INSERT INTO foobar (name, value) VALUES (?, ?)";

        SQLInsert testedObject = new SQLInsert(mockDataSource);
        testedObject.sql(() -> testSql);
        Mockito.when(mockDataSource.getConnection()).thenReturn(mockConnection);
        Mockito.when(mockConnection.prepareStatement(testSql, Statement.RETURN_GENERATED_KEYS)).thenReturn(mockStatement);
        Mockito.when(mockStatement.getParameterMetaData()).thenReturn(mockParameterMetaData);
        Mockito.when(mockParameterMetaData.getParameterType(1)).thenReturn(Types.VARCHAR);
        Mockito.when(mockParameterMetaData.getParameterType(2)).thenReturn(Types.BOOLEAN);
        Mockito.when(mockStatement.getGeneratedKeys()).thenReturn(mockInsertGeneratedKeys);
        Mockito.when(mockInsertGeneratedKeys.getMetaData()).thenReturn(mockInsertGeneratedKeysMetaData);
        Mockito.when(mockInsertGeneratedKeysMetaData.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(mockInsertGeneratedKeys.next()).thenReturn(Boolean.TRUE).thenReturn(Boolean.FALSE);
        Mockito.when(mockInsertGeneratedKeys.getInt(1)).thenReturn((Integer) 123456);

        TestEntity entity = new TestEntity();
        OperationValues insertValues = () -> Arrays.asList("hello world", Boolean.TRUE);

        testedObject.call(insertValues).firstRowKey(entity, TestEntity::id);
        Mockito.verify(mockStatement).setString(1, "hello world");
        Mockito.verify(mockStatement).setBoolean(2, Boolean.TRUE);

        Assert.assertEquals((Integer) 123456, entity.id);
    }
}