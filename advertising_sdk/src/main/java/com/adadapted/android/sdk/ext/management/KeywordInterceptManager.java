package com.adadapted.android.sdk.ext.management;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.keywordintercept.InitializeKeywordInterceptCommand;
import com.adadapted.android.sdk.core.keywordintercept.InitializeKeywordInterceptInteractor;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptAdapter;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.ext.http.HttpKeywordInterceptAdapter;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptBuilder;
import com.adadapted.android.sdk.ext.json.JsonKeywordInterceptRequestBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class KeywordInterceptManager
        implements DeviceInfoManager.Callback,
        SessionManager.Callback,
        InitializeKeywordInterceptInteractor.Callback {
    private static KeywordInterceptManager sInstance;
    private static KeywordIntercept sKeywordIntercept;

    private static synchronized KeywordInterceptManager getInstance() {
        if(sInstance == null) {
            sInstance = new KeywordInterceptManager();
        }

        return sInstance;
    }

    public static synchronized void initialize(final Callback callback) {
        if(getInstance() != null) {
            getInstance().init(callback);
        }
    }

    public static synchronized KeywordIntercept getKeywordIntercept() {
        return sKeywordIntercept;
    }

    private static synchronized  void setKeywordIntercept(final KeywordIntercept keywordIntercept) {
        sKeywordIntercept = keywordIntercept;
    }


    private final Set<Callback> callbacks = new HashSet<>();
    private KeywordInterceptAdapter adapter;

    private KeywordInterceptManager() {
        DeviceInfoManager.getInstance().getDeviceInfo(this);
    }

    private String determineInitEndpoint(final DeviceInfo deviceInfo) {
        if(deviceInfo.isProd()) {
            return Config.Prod.URL_KI_INIT;
        }

        return Config.Sand.URL_KI_INIT;
    }

    private void init(final Callback callback) {
        if(sKeywordIntercept == null) {
            callbacks.add(callback);
            SessionManager.getSession(this);
        }
        else {
            callback.onKeywordInterceptInitSuccess(sKeywordIntercept);
        }
    }

    @Override
    public void onSessionAvailable(final Session session) {
        final InitializeKeywordInterceptCommand command = new InitializeKeywordInterceptCommand(
                session,
                new JsonKeywordInterceptRequestBuilder());
        final Interactor interactor = new InitializeKeywordInterceptInteractor(command, adapter, this);

        ThreadPoolInteractorExecuter.getInstance().executeInBackground(interactor);
    }

    @Override
    public void onNewAdsAvailable(final Session session) {}

    @Override
    public void onKeywordInterceptSessionInitialized(final KeywordIntercept keywordIntercept) {
        setKeywordIntercept(keywordIntercept);
        notifyOnKeywordInterceptInitSuccess(keywordIntercept);
    }

    private void notifyOnKeywordInterceptInitSuccess(final KeywordIntercept keywordIntercept) {
        final Set<Callback> currentCallbacks = new HashSet<>(callbacks);
        for(final Callback c: currentCallbacks) {
            c.onKeywordInterceptInitSuccess(keywordIntercept);
        }
    }

    @Override
    public void onDeviceInfoCollected(final DeviceInfo deviceInfo) {
        final String endpoint = determineInitEndpoint(deviceInfo);
        adapter = new HttpKeywordInterceptAdapter(endpoint, new JsonKeywordInterceptBuilder());
    }

    public interface Callback {
        void onKeywordInterceptInitSuccess(KeywordIntercept keywordIntercept);
    }
}
