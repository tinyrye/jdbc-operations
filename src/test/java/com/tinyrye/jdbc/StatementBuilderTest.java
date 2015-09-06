package com.tinyrye.jdbc;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

public class StatementBuilderTest
{
    @Test
    public void testNewInstance()
    {
        StatementBuilder testedObject = null;
        testedObject = new StatementBuilder("SELECT * FROM foo");
        Assert.assertEquals("SELECT * FROM foo", testedObject.toString());
        testedObject = new StatementBuilder(() -> "SELECT * FROM foo");
        Assert.assertEquals("SELECT * FROM foo", testedObject.toString());
        testedObject = new StatementBuilder(new StringBuilder("SELECT * FROM foo"));
        Assert.assertEquals("SELECT * FROM foo", testedObject.toString());
    }

    @Test
    public void testAppendLine()
    {
        String amINull = "nope";
        StatementBuilder testedObject = new StatementBuilder("SELECT * FROM foo");
        testedObject.appendLine("JOIN bar ON foo.x = bar.x");
        Assert.assertEquals("SELECT * FROM foo\nJOIN bar ON foo.x = bar.x", testedObject.toString());
        testedObject = new StatementBuilder("SELECT * FROM foo");
        testedObject.appendLine(
            Optional.ofNullable(amINull).map(ignoreVal -> "JOIN bar ON foo.x = bar.x"),
            () -> "blah blah saint happenin");
        Assert.assertEquals("SELECT * FROM foo\nJOIN bar ON foo.x = bar.x", testedObject.toString());
        amINull = null;
        testedObject = new StatementBuilder("SELECT * FROM foo");
        testedObject.appendLine(
            Optional.ofNullable(amINull).map(ignoreVal -> "JOIN bar ON foo.x = bar.x"),
            () -> "LEFT OUTER JOIN mystery_table ON foo.x = mystery_table.x");
        Assert.assertEquals("SELECT * FROM foo\nLEFT OUTER JOIN mystery_table ON foo.x = mystery_table.x", testedObject.toString());
    }

    @Test
    public void testAppendWhereClauseLine()
    {
        Integer testParamValue = (Integer) 999; // at first it is not-null ...
        Supplier<String> testOrElse = () -> "foo.x IS NULL";
        StatementBuilder testedObject = new StatementBuilder("SELECT * FROM foo");
        testedObject.appendWhereClauseLine(
            Optional.ofNullable(testParamValue).map(ignoreVal -> "foo.x = 123"),
            "AND", testOrElse);
        Assert.assertEquals("SELECT * FROM foo\nWHERE foo.x = 123", testedObject.toString());
        testParamValue = null;
        testedObject = new StatementBuilder("SELECT * FROM foo");
        testedObject.appendWhereClauseLine(
            Optional.ofNullable(testParamValue).map(ignoreVal -> "foo.x = 123"),
            "AND", testOrElse);
        Assert.assertEquals("SELECT * FROM foo\nWHERE foo.x IS NULL", testedObject.toString());
        testedObject = new StatementBuilder("SELECT * FROM foo WHERE foo.y = 'la-dee-da'");
        testedObject.appendWhereClauseLine(
            Optional.ofNullable(testParamValue).map(ignoreVal -> "foo.x = 123"),
            "AND", testOrElse);
        Assert.assertEquals("SELECT * FROM foo WHERE foo.y = 'la-dee-da'\nAND foo.x IS NULL",
            testedObject.toString());
    }
}