package com.adadapted.android.sdk.core.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chrisweeden on 12/15/16.
 */

public class Retry {
    private final int maxRetries;
    private final AtomicInteger currentRetries;

    public Retry(int maxRetries) {
        this.maxRetries = maxRetries;
        this. currentRetries = new AtomicInteger(0);
    }

    public int retriesRemaining() {
        return maxRetries - currentRetries.intValue();
    }

    public boolean hasRemainingRetries() {
        return currentRetries.intValue() < maxRetries;
    }

    public void incrementRetry() {
        currentRetries.incrementAndGet();
    }
}
