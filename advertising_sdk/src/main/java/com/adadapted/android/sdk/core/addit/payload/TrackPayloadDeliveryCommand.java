package com.adadapted.android.sdk.core.addit.payload;

import com.adadapted.android.sdk.core.common.Command;

public class TrackPayloadDeliveryCommand extends Command {
    private final String payloadId;
    private final String result;

    public TrackPayloadDeliveryCommand(final String payloadId, final String result) {
        this.payloadId = payloadId;
        this.result = result;
    }

    public String getPayloadId() {
        return payloadId;
    }

    public String getResult() {
        return result;
    }
}
