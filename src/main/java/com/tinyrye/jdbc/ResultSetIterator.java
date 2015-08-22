package com.tinyrye.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ResultSetIterator implements Iterator<ResultSet>
{
    private final ResultSet resultSet;
    
    public ResultSetIterator(ResultSet resultSet) {
        this.resultSet = resultSet;
    }
    
    @Override
    public boolean hasNext() {
        try { return resultSet.next(); } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    
    @Override
    public ResultSet next() {
        if (hasNext()) return resultSet;
        else throw new NoSuchElementException("Passed end of result set.");
    }
}