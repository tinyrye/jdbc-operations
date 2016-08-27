package com.softwhistle.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParametersBuilder implements OperationValues
{
    private List values = new ArrayList();

    public ParametersBuilder add(Object value) {
        values.add(value);
        return this;
    }

    public ParametersBuilder add(Optional value) {
        value.ifPresent(v -> values.add(v));
        return this;
    }

    @Override
    public List values() {
        return values;
    }
}