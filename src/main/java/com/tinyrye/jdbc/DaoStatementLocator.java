package com.tinyrye.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.tinyrye.io.InputStreamToString;

public class DaoStatementLocator
{
    private final Class daoClass;
    
    public DaoStatementLocator(Class daoClass) {
        this.daoClass = daoClass;
    }
    
    public String get(String statementName) {
        return new InputStreamToString(daoClass.getResourceAsStream(pathTo(statementName))).runToString();
    }
    
    public String pathTo(String statementName) {
        return String.format("%s.%s.sql", daoClass.getSimpleName(), statementName);
    }
}