package com.adadapted.android.sdk.core.device;

import android.os.AsyncTask;
import android.util.Log;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/26/15.
 */
public class DeviceInfoBuilder extends AsyncTask<BuildDeviceInfoParam, Void, DeviceInfo> {
    private static final String TAG = DeviceInfoBuilder.class.getName();

    public interface Listener {
        void onDeviceInfoCollected(DeviceInfo deviceInfo);
    }

    private final Set<Listener> listeners;

    public DeviceInfoBuilder() {
        listeners = new HashSet<>();
    }

    @Override
    protected DeviceInfo doInBackground(final BuildDeviceInfoParam... params) {
        if(params.length == 1) {
            return DeviceInfo.captureDeviceInfo(
                    params[0].getContext(),
                    params[0].getAppId(),
                    params[0].isProd(),
                    params[0].getSdkVersion());
        }

        Log.e(TAG, "Only expects a single parameter.");
        return null;
    }

    protected void onPostExecute(final DeviceInfo deviceInfo) {
        notifyDeviceInfoCollected(deviceInfo);
    }

    public void addListener(final Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(final Listener listener) {
        listeners.remove(listener);
    }

    private void notifyDeviceInfoCollected(final DeviceInfo deviceInfo) {
        for(Listener listener : listeners) {
            listener.onDeviceInfoCollected(deviceInfo);
        }
    }
}
