package com.softwhistle.jdbc;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParameterSetterTest
{
    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ParameterMetaData mockParameterMetaData;

    @Test
    public void testSetValue() throws Exception
    {
        ParameterSetter testedObject = new ParameterSetter();
        Integer testIntegerParamValue = (Integer) 123456;
        String testStringParamValue = "foobar";
        Long testLongParamValue = (Long) System.currentTimeMillis();
        Boolean testBooleanParamValue = Boolean.TRUE;

        Mockito.when(mockStatement.getParameterMetaData()).thenReturn(mockParameterMetaData);

        Mockito.when(mockParameterMetaData.getParameterType(1)).thenReturn(Types.INTEGER);
        testedObject.setValue(mockStatement, 1, testIntegerParamValue);
        Mockito.when(mockParameterMetaData.getParameterType(1)).thenReturn(Types.BIGINT);
        testedObject.setValue(mockStatement, 1, testIntegerParamValue);
        Mockito.when(mockParameterMetaData.getParameterType(1)).thenReturn(Types.SMALLINT);
        testedObject.setValue(mockStatement, 1, testIntegerParamValue);
        Mockito.when(mockParameterMetaData.getParameterType(1)).thenReturn(Types.TINYINT);
        testedObject.setValue(mockStatement, 1, testIntegerParamValue);
        Mockito.verify(mockStatement, Mockito.times(4)).setInt(1, testIntegerParamValue.intValue());

        Mockito.when(mockParameterMetaData.getParameterType(2)).thenReturn(Types.VARCHAR);
        testedObject.setValue(mockStatement, 2, testStringParamValue);
        Mockito.verify(mockStatement, Mockito.times(1)).setString(2, testStringParamValue);

        Mockito.when(mockParameterMetaData.getParameterType(3)).thenReturn(Types.INTEGER);
        testedObject.setValue(mockStatement, 3, testLongParamValue);
        Mockito.when(mockParameterMetaData.getParameterType(3)).thenReturn(Types.BIGINT);
        testedObject.setValue(mockStatement, 3, testLongParamValue);
        Mockito.when(mockParameterMetaData.getParameterType(3)).thenReturn(Types.SMALLINT);
        testedObject.setValue(mockStatement, 3, testLongParamValue);
        Mockito.when(mockParameterMetaData.getParameterType(3)).thenReturn(Types.TINYINT);
        testedObject.setValue(mockStatement, 3, testLongParamValue);
        Mockito.when(mockParameterMetaData.getParameterType(3)).thenReturn(Types.TIMESTAMP);
        testedObject.setValue(mockStatement, 3, testLongParamValue);
        Mockito.verify(mockStatement, Mockito.times(4)).setLong(3, testLongParamValue.longValue());
        Mockito.verify(mockStatement, Mockito.times(1)).setTimestamp(3, new Timestamp(testLongParamValue));

        Mockito.when(mockParameterMetaData.getParameterType(4)).thenReturn(Types.BOOLEAN);
        testedObject.setValue(mockStatement, 4, testBooleanParamValue);
        Mockito.verify(mockStatement, Mockito.times(1)).setBoolean(4, testBooleanParamValue.booleanValue());
    }
}
