package com.adadapted.android.sdk.core.addit;

import android.util.Log;

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.device.DeviceInfoClient;
import com.adadapted.android.sdk.core.event.AppEventClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayloadClient {
    private static final String LOGTAG = PayloadClient.class.getName();

    private static PayloadClient instance;
    private static boolean deeplinkInProgress = false;

    public static synchronized PayloadClient createInstance(final PayloadAdapter adapter) {
        if(instance == null) {
            instance = new PayloadClient(adapter);
        }

        return instance;
    }

    private static synchronized PayloadClient getInstance() {
        return instance;
    }

    public static synchronized void pickupPayloads() {
        if(instance == null) {
            return;
        }

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().performPickupPayload();
            }
        });
    }

    public static synchronized void deeplinkInProgress() {
        deeplinkInProgress = true;
    }

    public static synchronized void deeplinkCompleted() {
        deeplinkInProgress = false;
    }

    public static synchronized void markContentAcknowledged(final Content content) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                final List<AddToListItem> payload = content.getPayload();
                for (AddToListItem item : payload) {
                    final Map<String, String> eventParams = new HashMap<>();
                    eventParams.put("payload_id", content.getPayloadId());
                    eventParams.put("tracking_id", item.getTrackingId());
                    eventParams.put("item_name", item.getTitle());
                    eventParams.put("source", content.getSource());

                    AppEventClient.trackSdkEvent("addit_added_to_list", eventParams);

                    if(content.isPayloadSource()) {
                        getInstance().trackPayload(content, "delivered");
                    }
                }
            }
        });
    }

    public static synchronized void markContentDuplicate(final Content content) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> eventParams = new HashMap<>();
                eventParams.put("payload_id", content.getPayloadId());

                AppEventClient.trackSdkEvent("addit_duplicate_payload", eventParams);

                if(content.isPayloadSource()) {
                    getInstance().trackPayload(content, "duplicate");
                }
            }
        });


    }

    public static synchronized void markContentFailed(final Content content, final String message) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> eventParams = new HashMap<>();
                eventParams.put("payload_id", content.getPayloadId());

                AppEventClient.trackError("ADDIT_CONTENT_FAILED", message, eventParams);

                if(content.isPayloadSource()) {
                    getInstance().trackPayload(content, "rejected");
                }
            }
        });
    }

    private final PayloadAdapter adapter;

    private PayloadClient(final PayloadAdapter adapter) {
        this.adapter = adapter;
    }

    private void performPickupPayload() {
        DeviceInfoClient.getDeviceInfo(new DeviceInfoClient.Callback() {
            @Override
            public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
                AppEventClient.trackSdkEvent("payload_pickup_attempt");

                final JSONObject request = new JSONObject();
                try {
                    request.put("app_id", deviceInfo.getAppId());
                    request.put("udid", deviceInfo.getUdid());
                    request.put("bundle_id", deviceInfo.getBundleId());
                    request.put("bundle_version", deviceInfo.getBundleVersion());
                    request.put("os", deviceInfo.getOs());
                    request.put("osv", deviceInfo.getOsv());
                    request.put("device", deviceInfo.getDevice());
                    request.put("sdk_version", deviceInfo.getSdkVersion());
                    request.put("timestamp", new Date().getTime());
                }
                catch(JSONException ex) {
                    Log.w(LOGTAG, "Problem building App Event JSON");
                }

                adapter.pickup(request, new PayloadAdapter.Callback() {
                    @Override
                    public void onSuccess(final List<Content> content) {
                        //callback.onPayloadAvailable(content);
                    }

                    @Override
                    public void onFailure(final String message) {
                        //callback.onError();
                    }
                });
            }
        });
    }

    private JSONObject buildPayloadTrackingWrapper(final DeviceInfo deviceInfo) {
        final JSONObject wrapper = new JSONObject();

        if(deviceInfo != null) {
            try {
                wrapper.put("app_id", deviceInfo.getAppId());
                wrapper.put("udid", deviceInfo.getUdid());
                wrapper.put("bundle_id", deviceInfo.getBundleId());
                wrapper.put("bundle_version", deviceInfo.getBundleVersion());
                wrapper.put("os", deviceInfo.getOs());
                wrapper.put("osv", deviceInfo.getOsv());
                wrapper.put("device", deviceInfo.getDevice());
                wrapper.put("sdk_version", deviceInfo.getSdkVersion());
            } catch (JSONException ex) {
                Log.w(LOGTAG, "Problem building Payload Tracking Wrapper JSON");
            }
        }

        return wrapper;
    }

    public void trackPayload(final Content content,
                             final String result) {
        try {
            final JSONObject event = new JSONObject();
            event.put("payload_id", content.getPayloadId());
            event.put("status", result);
            event.put("event_timestamp", new Date().getTime());

            final JSONArray tracking = new JSONArray();
            tracking.put(event);

            //final JSONObject json = new JSONObject(wrapper.toString());
            final JSONObject json = new JSONObject();
            json.put("tracking", tracking);

            adapter.publishEvent(json);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building Payload Event JSON");
        }
    }
}
