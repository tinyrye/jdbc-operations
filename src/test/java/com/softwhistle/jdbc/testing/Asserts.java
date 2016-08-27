package com.softwhistle.jdbc.testing;

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;

public class Asserts
{
    public static <T> void assertOrderedEquivalent(List<T> l1, List<T> l2) {
        Assert.assertEquals(l1.size(), l2.size());
        Iterator<T> i1 = l1.iterator();
        Iterator<T> i2 = l2.iterator();
        while (i1.hasNext()) Assert.assertEquals(i1.next(), i2.next());
    }
}
