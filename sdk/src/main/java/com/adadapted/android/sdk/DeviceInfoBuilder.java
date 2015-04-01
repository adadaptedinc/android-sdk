package com.adadapted.android.sdk;

import android.os.AsyncTask;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/26/15.
 */
public class DeviceInfoBuilder extends AsyncTask<BuildDeviceInfoParam, Void, DeviceInfo> {
    private static final String TAG = DeviceInfoBuilder.class.getName();

    interface Listener {
        void onDeviceInfoCollected(DeviceInfo deviceInfo);
    }

    private Set<Listener> listeners;

    DeviceInfoBuilder() {
        listeners = new HashSet<>();
    }

    @Override
    protected DeviceInfo doInBackground(BuildDeviceInfoParam... params) {
        if(params.length == 1) {
            return DeviceInfo.captureDeviceInfo(params[0].getContext(),
                    params[0].getAppId(), params[0].getZones());
        }

        Log.e(TAG, "Only expects a single parameter.");
        return null;
    }

    protected void onPostExecute(DeviceInfo deviceInfo) {
        notifyDeviceInfoCollected(deviceInfo);
    }

    void addListener(Listener listener) {
        listeners.add(listener);
    }

    void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyDeviceInfoCollected(DeviceInfo deviceInfo) {
        for(Listener listener : listeners) {
            listener.onDeviceInfoCollected(deviceInfo);
        }
    }
}
