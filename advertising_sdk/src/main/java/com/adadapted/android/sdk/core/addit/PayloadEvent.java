package com.adadapted.android.sdk.core.addit;

import java.util.Date;

public class PayloadEvent {
    private final String payloadId;
    private final String status;
    private final long timestamp;

    PayloadEvent(final String payloadId,
                 final String status) {
        this.payloadId = payloadId;
        this.status = status;
        this.timestamp = (new Date()).getTime();
    }

    public String getPayloadId() {
        return payloadId;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
