package com.adadapted.android.sdk.core.ad;

import org.junit.Test;

import static org.junit.Assert.*;

public class ImpressionIdCounterTest {

    @Test
    public void getIncrementedCountFor() {
        final ImpressionIdCounter counter = ImpressionIdCounter.getInstance();
        final int count = counter.getIncrementedCountFor("test_impression1");

        assertEquals(1, count);
    }

    @Test
    public void getCurrentCountFor_WithNewCounter() {
        final ImpressionIdCounter counter = ImpressionIdCounter.getInstance();
        final int count = counter.getCurrentCountFor("test_impression2");

        assertEquals(1, count);
    }

    @Test
    public void getCurrentCountFor_WithExistingCounter() {
        final ImpressionIdCounter counter = ImpressionIdCounter.getInstance();
        counter.getIncrementedCountFor("test_impression3");

        final int count = counter.getCurrentCountFor("test_impression3");

        assertEquals(1, count);
    }
}