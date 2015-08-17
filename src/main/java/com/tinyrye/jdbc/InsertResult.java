package com.tinyrye.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertResult
{
    public List<List<Integer>> generatedKeys = new ArrayList<List<Integer>>();

    public InsertResult generatedKeys(List<List<Integer>> generatedKeys) { this.generatedKeys = generatedKeys; return this; }
    public InsertResult addRowGeneratedKeys(List<Integer> rowGeneratedKeys) { generatedKeys.add(rowGeneratedKeys); return this; }
    public InsertResult addRowGeneratedKey(Integer rowGeneratedKey) { generatedKeys.add(Arrays.asList(rowGeneratedKey)); return this; }
}