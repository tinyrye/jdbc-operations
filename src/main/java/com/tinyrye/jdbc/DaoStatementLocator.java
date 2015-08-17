package com.tinyrye.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DaoStatementLocator
{
    private final Class daoClass;

    public DaoStatementLocator(Class daoClass) {
        this.daoClass = daoClass;
    }
    
    public String get(String statementName) {
        try { return toString(daoClass.getResourceAsStream(pathTo(statementName))); }
        catch (IOException ex) { throw new RuntimeException(ex); }
    }

    public String pathTo(String statementName) {
        return String.format("%s.%s.sql", daoClass.getSimpleName(), statementName);
    }

    protected String toString(InputStream inputStream) throws IOException
    {
        InputStreamReader charReader = new InputStreamReader(inputStream);
        try
        {
            StringBuilder fullRead = new StringBuilder();
            char[] readBuf = new char[1024];
            int readCount = 0;
            while ((readCount = charReader.read(readBuf)) > -1) {
                fullRead.append(readBuf, 0, readCount);
            }
            return fullRead.toString();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        finally {
            try { charReader.close(); }
            catch(IOException ex) { /* ignore */ }
        }
    }
}