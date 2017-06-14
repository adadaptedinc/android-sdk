package com.adadapted.android.sdk.core.addit.payload;

import android.util.Log;

import com.adadapted.android.sdk.core.addit.Content;
import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.model.AppEventSource;
import com.adadapted.android.sdk.ext.management.AppEventTrackingManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by chrisweeden on 2/9/17.
 */
public class PickupPayloadInteractor implements Interactor {
    private static final String LOGTAG = PickupPayloadInteractor.class.getName();

    private final PickupPayloadCommand command;
    private final PayloadAdapter adapter;
    private final Callback callback;

    public PickupPayloadInteractor(final PickupPayloadCommand command,
                                   final PayloadAdapter adapter,
                                   final Callback callback) {
        this.command = command;
        this.adapter = adapter;
        this.callback = callback;
    }

    @Override
    public void execute() {
        if(command == null || adapter == null || callback == null) {
            return;
        }

        AppEventTrackingManager.registerEvent(AppEventSource.SDK, "payload_pickup_attempt");

        final DeviceInfo di = command.getDeviceInfo();

        final JSONObject request = new JSONObject();
        try {
            request.put("app_id", di.getAppId());
            request.put("udid", di.getUdid());
            request.put("bundle_id", di.getBundleId());
            request.put("bundle_version", di.getBundleVersion());
            request.put("os", di.getOs());
            request.put("osv", di.getOsv());
            request.put("device", di.getDevice());
            request.put("sdk_version", di.getSdkVersion());
            request.put("timestamp", new Date().getTime());
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Event JSON");
        }

        adapter.pickup(request, new PayloadAdapter.Callback() {
            @Override
            public void onSuccess(final List<Content> content) {
                callback.onPayloadAvailable(content);
            }

            @Override
            public void onFailure(final String message) {
                callback.onError();
            }
        });
    }

    public interface Callback {
        void onPayloadAvailable(List<Content> content);
        void onError();
    }
}
