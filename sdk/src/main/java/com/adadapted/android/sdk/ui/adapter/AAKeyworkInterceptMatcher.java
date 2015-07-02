package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptManager;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.factory.KeywordInterceptManagerFactory;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 6/25/15.
 */
public class AAKeyworkInterceptMatcher implements SessionManager.Listener, KeywordInterceptManager.Listener {
    private static final String TAG = AAKeyworkInterceptMatcher.class.getName();

    private final KeywordInterceptManager manager;
    
    private KeywordIntercept keywordIntercept;
    private boolean loaded = false;
    private Session session;

    public AAKeyworkInterceptMatcher(Context context) {
        manager = KeywordInterceptManagerFactory.getInstance(context).createKeywordInterceptManager();
        manager.setListener(this);

        SessionManagerFactory.getInstance(context).createSessionManager().addListener(this);
    }

    public Set<String> match(CharSequence constraint) {
        Set<String> suggestions = new HashSet<>();

        if(constraint != null && isLoaded()) {
            for (String item : keywordIntercept.getAutofill().keySet()) {
                if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                    suggestions.add(keywordIntercept.getAutofill().get(item));
                    manager.trackPresented(session, item, constraint.toString());
                }
            }
        }

        return suggestions;
    }

    private boolean isLoaded() {
        return loaded;
    }

    @Override
    public void onKeywordInterceptInitSuccess(KeywordIntercept keywordIntercept) {
        Log.d(TAG, "Keyword Matcher Initialized.");
        this.keywordIntercept = keywordIntercept;
        this.loaded = true;
    }

    @Override
    public void onSessionInitialized(Session session) {
        this.session = session;
        manager.init(session, AdAdapted.getInstance().getDeviceInfo());
    }

    @Override
    public void onSessionInitFailed() {}

    @Override
    public void onSessionNotReinitialized() {}
}
