package com.softwhistle.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.softwhistle.jdbc.testing.Asserts;

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
    public void testConvert() throws Exception
    {
        ResultSetValueConverter testedObject = new ResultSetValueConverter();
        Mockito.when(resultSet.findColumn("status")).thenReturn(4);
        Mockito.when(resultSet.findColumn("loginCount")).thenReturn(5);
        Mockito.when(resultSet.findColumn("loginIds")).thenReturn(6);
        Mockito.when(resultSet.findColumn("passwordHints")).thenReturn(7);
        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSetMetaData.getColumnType(4)).thenReturn(Types.VARCHAR);
        Mockito.when(resultSetMetaData.getColumnType(5)).thenReturn(Types.INTEGER);
        Mockito.when(resultSetMetaData.getColumnType(6)).thenReturn(Types.VARCHAR);
        Mockito.when(resultSetMetaData.getColumnType(7)).thenReturn(Types.VARCHAR);
        Mockito.when(resultSet.getString(4)).thenReturn("EENIE");
        Mockito.when(resultSet.getObject(5, Integer.class)).thenReturn(123456);
        Mockito.when(resultSet.getInt(5)).thenReturn(123456);
        Mockito.when(resultSet.getString(6)).thenReturn("abc,def,ghi");
        Mockito.when(resultSet.getString(7)).thenReturn(null);

        Assert.assertEquals(TestStatus.EENIE, testedObject.convert(resultSet, TestStatus.class, "status"));
        Assert.assertEquals((Integer) 123456, testedObject.convert(resultSet, Integer.class, "loginCount"));
        Asserts.assertOrderedEquivalent(Arrays.asList("abc", "def", "ghi"), testedObject.convert(resultSet, List.class, "loginIds"));
        Asserts.assertOrderedEquivalent(new ArrayList<String>(), testedObject.convert(resultSet, List.class, "passwordHints"));
    }
}
