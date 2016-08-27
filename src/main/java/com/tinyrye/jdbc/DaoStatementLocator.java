package com.softwhistle.jdbc;

import static com.softwhistle.io.Operations.openThenWith;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.function.Supplier;

import com.google.common.io.CharStreams;

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
        return openThenWith(this::readerToResource, (rdr) -> CharStreams.toString(rdr));
    }

    public Reader readerToResource() {
        return new InputStreamReader(daoClass.getResourceAsStream(pathTo()));
    }

    public String pathTo() {
        return String.format("%s.%s.sql", daoClass.getSimpleName(), statementName);
    }
}
