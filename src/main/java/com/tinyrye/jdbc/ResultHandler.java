package com.softwhistle.jdbc;

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
import java.util.function.Supplier;

/**
 * TODO: return a Stream object?
 */
public class ResultHandler
{
    private final ResultSet resultSet;
    private ResultSetValueConverter columnReader = new ResultSetValueConverter();
    private OperationResourceManager closeableManager;

    public ResultHandler(ResultSet resultSet) {
        this.resultSet = resultSet;
        closeableManager = new OperationResourceManager(resultSet);
    }

    public ResultHandler takeoverFor(OperationResourceManager closeableManager) {
        this.closeableManager.takeoverFor(closeableManager);
        return this;
    }
    
    public void process(Consumer<ResultSet> rowProcessor) {
        try { new ResultSetIterator(resultSet).forEachRemaining(rowProcessor); }
        finally { closeableManager.close(); }
    }
    
    public void process(BiConsumer<ResultSet,ResultSetValueConverter> rowProcessor) {
        process((Consumer<ResultSet>) (resultSetOfIteration ->
            rowProcessor.accept(resultSetOfIteration, columnReader)));
    }
    
    public <T> List<T> map(Function<ResultSet,T> rowConverter)
    {
        try {
            List<T> rowResults = new ArrayList<T>();
            new ResultSetIterator(resultSet).forEachRemaining(resultSetOfIteration -> rowResults.add(rowConverter.apply(resultSetOfIteration)));
            return rowResults;
        } finally { closeableManager.close(); }
    }
    
    public <T> List<T> map(RowConverter<T> rowConverter) {
        return map((Function<ResultSet,T>) (resultSet ->
                        rowConverter.convertRow(resultSet, columnReader)));
    }
    
    public <T,O> O mapInto(Supplier<O> liasonSupplier, Function<ResultSet,T> rowConverter, BiConsumer<T,O> rowMerger) {
        O liason = liasonSupplier.get();
        process((rs) -> rowMerger.accept(rowConverter.apply(rs), liason));
        return liason;
    }
    
    public <T,O> O mapInto(Supplier<O> liasonSupplier, RowConverter<T> rowConverter, BiConsumer<T,O> rowMerger) {
        O liason = liasonSupplier.get();
        process((rs) -> rowMerger.accept(rowConverter.convertRow(rs, columnReader), liason));
        return liason;
    }
    
    public <T> Optional<T> first(Function<ResultSet,T> rowConverter) {
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
