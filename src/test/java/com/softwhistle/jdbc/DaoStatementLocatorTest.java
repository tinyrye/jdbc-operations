package com.softwhistle.jdbc;

import org.junit.Assert;
import org.junit.Test;

public class DaoStatementLocatorTest
{
    @Test
    public void testGet() {
    	DaoStatementLocator testedObject = new DaoStatementLocator(DaoStatementLocatorTest.class, "select-test-statement");
        Assert.assertEquals("SELECT id, name, value FROM foobar WHERE column_x = 'zippity_dooda'", testedObject.get());
    }
}
