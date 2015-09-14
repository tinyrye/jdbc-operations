package com.tinyrye.jdbc;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.junit.Assert;
import org.junit.Test;

public class InsertResultTest
{
    @Test
    public void testFirstRowKey_Optional() {
        InsertResult testedObject = new InsertResult();
        Optional<Integer> actualValue = null;
        actualValue = testedObject.firstRowKey();
        Assert.assertFalse(actualValue.isPresent());
        testedObject.addRowGeneratedKey((Integer) 123);
        actualValue = testedObject.firstRowKey();
        Assert.assertTrue(actualValue.isPresent());
        Assert.assertEquals((Integer) 123, actualValue.get());
    }

    @Test
    public void testFirstRowKey_EntitySetter() {
        TestEntity testEntity = new TestEntity();
        InsertResult testedObject = new InsertResult();
        testedObject.firstRowKey(testEntity, TestEntity::id);
        Assert.assertNull(testEntity.id);
        testedObject.addRowGeneratedKey((Integer) 123);
        testedObject.firstRowKey(testEntity, TestEntity::id);
        Assert.assertEquals((Integer) 123, testEntity.id);
    }

    protected static class TestEntity {
        public Integer id;
        public TestEntity id(Integer id) { this.id = id; return this; }
    }
}