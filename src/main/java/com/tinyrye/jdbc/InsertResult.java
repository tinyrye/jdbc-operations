package com.softwhistle.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class InsertResult
{
    public List<List<Integer>> generatedKeys = new ArrayList<List<Integer>>();
    
    public Optional<Integer> firstRowKey() {
        return generatedKeys.stream().findFirst().map(firstRowKeys ->
            firstRowKeys.stream().findFirst().orElse(null));
    }

    public <T> InsertResult firstRowKey(T entity, BiConsumer<T,Integer> keyEntitySetter) {
        firstRowKey().ifPresent(key -> keyEntitySetter.accept(entity, key));
        return this;
    }

    public InsertResult combine(InsertResult that) {
        this.generatedKeys.addAll(that.generatedKeys);
        return this;
    }

    public InsertResult generatedKeys(List<List<Integer>> generatedKeys) { this.generatedKeys = generatedKeys; return this; }
    public InsertResult addRowGeneratedKeys(List<Integer> rowGeneratedKeys) { generatedKeys.add(rowGeneratedKeys); return this; }
    public InsertResult addRowGeneratedKey(Integer rowGeneratedKey) { generatedKeys.add(Arrays.asList(rowGeneratedKey)); return this; }
}