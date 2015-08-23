package com.tinyrye.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ResultHandler
{
    public static void process(ResultSet resultSet, Consumer<ResultSet> rowProcessor) {
        try { new ResultSetIterator(resultSet).forEachRemaining(rowProcessor); }
        finally { SQLOperation.closeQuietly(resultSet); }
    }
    
    public static <T> List<T> map(ResultSet resultSet, Function<ResultSet,T> rowConverter)
    {
        try {
            List<T> rowResults = new ArrayList<T>();
            new ResultSetIterator(resultSet).forEachRemaining(resultSetOfIteration -> rowResults.add(rowConverter.apply(resultSetOfIteration)));
            return rowResults;
        } finally { SQLOperation.closeQuietly(resultSet); }
    }
    
    private final ResultSet resultSet;
    private ResultSetValueConverter columnReader = new ResultSetValueConverter();
    
    public ResultHandler(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public void process(Consumer<ResultSet> rowProcessor) {
        process(resultSet, rowProcessor);
    }
    
    public void process(BiConsumer<ResultSet,ResultSetValueConverter> rowProcessor) {
        process(resultSet, (Consumer<ResultSet>) (resultSetOfIteration ->
                    rowProcessor.accept(resultSetOfIteration, columnReader)));
    }
    
    public <T> List<T> map(Function<ResultSet,T> rowConverter) {
        return map(resultSet, rowConverter);
    }
    
    public <T> List<T> map(BiFunction<ResultSet,ResultSetValueConverter,T> rowConverter) {
        return map(resultSet, (Function<ResultSet,T>) (resultSetOfIteration ->
                        rowConverter.apply(resultSetOfIteration, columnReader)));
    }
    
    public <T> List<T> map(RowConverter<T> rowConverter) {
        return map(resultSet, (Function<ResultSet,T>) (resultSet ->
                        rowConverter.convertRow(resultSet, columnReader)));
    }
    
    public <T> Optional<T> first(Function<ResultSet,T> rowConverter) {
        return first(map(rowConverter));
    }
    
    public <T> Optional<T> first(BiFunction<ResultSet,ResultSetValueConverter,T> rowConverter) {
        return first(map(rowConverter));
    }
    
    public <T> Optional<T> first(RowConverter<T> rowConverter) {
        return first(map(rowConverter));
    }
    
    public ResultHandler columnReader(ResultSetValueConverter columnReader) {
        this.columnReader = columnReader;
        return this;
    }

    protected static <T> Optional<T> first(List<T> resultSetTransform) {
        return resultSetTransform.stream().findFirst();
    }
}