package com.adadapted.android.sdk.core.session;

import android.content.Context;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.ad.AdFetcherListener;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.scheduler.AdRefreshScheduler;
import com.adadapted.android.sdk.ext.scheduler.EventFlushScheduler;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class SessionManager {
    private static final String LOGTAG = SessionManager.class.getName();

    private final Set<SessionListener> mListeners;

    private final Context mContext;
    private final SessionAdapter mSessionAdapter;
    private final SessionRequestBuilder mRequestBuilder;
    private final SessionBuilder mSessionBuilder;

    private AdRefreshScheduler mAdRefreshScheduler;
    private EventFlushScheduler mEventFlushScheduler;

    private Session mCurrentSession;
    private boolean mSessionLoaded = false;

    private final AdFetcherListener adFetcherListener = new AdFetcherListener() {
        @Override
        public void onSuccess(final Map<String, Zone> object) {
            notifyNewAdsAvailable();
        }

        @Override
        public void onFailure() {}
    };

    public SessionManager(final Context context,
                          final SessionAdapter sessionAdapter,
                          final SessionRequestBuilder requestBuilder,
                          final SessionBuilder sessionBuilder) {
        mListeners = new HashSet<>();

        mContext = context;
        mSessionAdapter = sessionAdapter;
        mRequestBuilder = requestBuilder;
        mSessionBuilder = sessionBuilder;

        mCurrentSession = new Session();
    }

    public Session getCurrentSession() {
        return mCurrentSession;
    }

    public AdFetcherListener getAdFetcherListener() {
        return adFetcherListener;
    }

    public void initialize(final DeviceInfo deviceInfo) {
        final JSONObject request = mRequestBuilder.buildSessionInitRequest(deviceInfo);
        mSessionAdapter.sendInit(request, new SessionAdapterListener() {
            @Override
            public void onSuccess(JSONObject json) {
                mCurrentSession = mSessionBuilder.buildSession(deviceInfo, json);
                mSessionLoaded = true;

                mAdRefreshScheduler = new AdRefreshScheduler(mContext, mCurrentSession);
                mEventFlushScheduler = new EventFlushScheduler(mCurrentSession);

                scheduleAdRefresh();
                mEventFlushScheduler.start(Config.DEFAULT_EVENT_POLLING);

                notifySessionInitialized();
            }

            @Override
            public void onFailure() {
                notifySessionInitFailed();
            }
        });
    }

    public void scheduleAdRefresh() {
        mAdRefreshScheduler.schedule(getCurrentSession());
    }

    public void addListener(final SessionListener listener) {
        if(mSessionLoaded) {
            listener.onSessionInitialized(mCurrentSession);
        }

        mListeners.add(listener);
    }

    public void removeListener(final SessionListener listener) {
        mListeners.remove(listener);
    }

    private void notifySessionInitialized() {
        for(final SessionListener listener: mListeners) {
           listener.onSessionInitialized(mCurrentSession);
        }
    }

    private void notifySessionInitFailed() {
        for(final SessionListener listener: mListeners) {
            listener.onSessionInitFailed();
        }
    }

    private void notifyNewAdsAvailable() {
        for(final SessionListener listener: mListeners) {
            listener.onNewAdsAvailable(mCurrentSession);
        }
    }
}
