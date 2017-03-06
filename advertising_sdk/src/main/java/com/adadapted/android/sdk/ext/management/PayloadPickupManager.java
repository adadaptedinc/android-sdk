package com.adadapted.android.sdk.ext.management;


import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.addit.Content;
import com.adadapted.android.sdk.core.addit.payload.PayloadAdapter;
import com.adadapted.android.sdk.core.addit.payload.PickupPayloadCommand;
import com.adadapted.android.sdk.core.addit.payload.PickupPayloadInteractor;
import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.ext.http.HttpPayloadAdapter;
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher;

import java.util.List;

/**
 * Created by chrisweeden on 2/9/17.
 */
public class PayloadPickupManager implements PickupPayloadInteractor.Callback {
    private static final String LOGTAG = PayloadPickupManager.class.getName();

    private static PayloadPickupManager sInstance;
    private static boolean blocked = false;
    private static Interactor queuedInteractor = null;

    private PayloadAdapter adapter;

    private PayloadPickupManager() {}

    private void performPickupPayloads(final DeviceInfo deviceInfo) {
        if(deviceInfo == null) {
            return;
        }

        if(this.adapter == null) {
            this.adapter = new HttpPayloadAdapter(determineEndpoint(deviceInfo));
        }

        final PickupPayloadCommand command = new PickupPayloadCommand(deviceInfo);
        final PickupPayloadInteractor interactor = new PickupPayloadInteractor(command, adapter, this);

        if(!blocked) {
            ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
        }
        else {
            queuedInteractor = interactor;
        }
    }

    @Override
    public void onPayloadAvailable(final List<Content> content) {
        if(content == null) {
            return;
        }

        for(final Content c : content) {
            AdditContentPublisher.getInstance().publishContent(c);
        }
    }

    @Override
    public void onError() {}

    private String determineEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_APP_PAYLOAD_PICKUP;
        }

        return Config.Sand.URL_APP_PAYLOAD_PICKUP;
    }

    private static PayloadPickupManager getInstance() {
        if(sInstance == null) {
            sInstance = new PayloadPickupManager();
        }

        return sInstance;
    }

    public synchronized static void deeplinkInProgress() {
        blocked = true;
    }

    public synchronized static void deeplinkCompleted() {
        blocked = false;

        if(queuedInteractor != null) {
            ThreadPoolInteractorExecuter.getInstance().executeInBackground(queuedInteractor);
            queuedInteractor = null;
        }
    }

    public synchronized static void pickupPayloads() {
        DeviceInfoManager.getInstance().getDeviceInfo(new DeviceInfoManager.Callback() {
            @Override
            public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
                getInstance().performPickupPayloads(deviceInfo);
            }
        });
    }

    public synchronized static void pickupPayloads(final DeviceInfo deviceInfo) {
        getInstance().performPickupPayloads(deviceInfo);
    }
}
