package com.tinyrye.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResultSetValueConverterTest
{
    public static enum TestStatus {
        EENIE, MEENIE, MINIE, MO;
    }

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData resultSetMetaData;

    @Test
    public void testConvert() throws Exception {
        ResultSetValueConverter testedObject = new ResultSetValueConverter();
        Mockito.when(resultSet.findColumn("loginCount")).thenReturn(5);
        Mockito.when(resultSet.findColumn("status")).thenReturn(4);
        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSetMetaData.getColumnType(5)).thenReturn(Types.INTEGER);
        Mockito.when(resultSetMetaData.getColumnType(4)).thenReturn(Types.VARCHAR);
        Mockito.when(resultSet.getInt(5)).thenReturn(123456);
        Mockito.when(resultSet.getString(4)).thenReturn("EENIE");
        Assert.assertNotNull(testedObject.getMapping(Integer.class, Types.INTEGER));
        Assert.assertEquals((Integer) 123456, testedObject.convert(resultSet, Integer.class, "loginCount"));
        Assert.assertEquals(TestStatus.EENIE, testedObject.convert(resultSet, TestStatus.class, "status"));
    }
}