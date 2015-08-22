package com.tinyrye.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Supplier;

import com.tinyrye.io.InputStreamToString;

public class DaoStatementLocator implements Supplier<String>
{
    private final Class daoClass;
    protected final String statementName;

    public DaoStatementLocator(Class daoClass, String statementName) {
        this.daoClass = daoClass;
        this.statementName = statementName;
    }
    
    @Override
    public String get() {
        return new InputStreamToString(inputStreamTo()).runToString();
    }
    
    public InputStream inputStreamTo() {
        return daoClass.getResourceAsStream(pathTo());
    }

    public String pathTo() {
        return String.format("%s.%s.sql", daoClass.getSimpleName(), statementName);
    }
}