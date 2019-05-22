package com.adadapted.android.sdk.core.ad;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ImpressionIdCounter {
    private static ImpressionIdCounter instance;

    public static ImpressionIdCounter getsInstance() {
        if(instance == null) {
            instance = new ImpressionIdCounter();
        }

        return instance;
    }

    private static final int INITIAL_VAL = 1;

    private final Map<String, Integer> idCounts;
    private final Lock counterLock = new ReentrantLock();

    private ImpressionIdCounter() {
        idCounts = new HashMap<>();
    }

    private void initCountFor(final String impressionId) {
        idCounts.put(impressionId, INITIAL_VAL);
    }

    public synchronized int getIncrementedCountFor(final String impressionId) {
        counterLock.lock();
        try {
            if (idCounts.containsKey(impressionId)) {
                int val = idCounts.get(impressionId);
                val++;
                idCounts.put(impressionId, val);

                return val;
            } else {
                initCountFor(impressionId);
                return idCounts.get(impressionId);
            }
        }
        finally {
            counterLock.unlock();
        }
    }

    public synchronized int getCurrentCountFor(final String impressionId) {
        counterLock.lock();
        try {
            if (idCounts.containsKey(impressionId)) {
                return idCounts.get(impressionId);
            } else {
                initCountFor(impressionId);
                return idCounts.get(impressionId);
            }
        }
        finally {
            counterLock.unlock();
        }
    }
}
