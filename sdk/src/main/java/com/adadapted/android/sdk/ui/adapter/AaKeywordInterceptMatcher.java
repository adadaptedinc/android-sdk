package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptManager;
import com.adadapted.android.sdk.core.keywordintercept.model.AutoFill;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;
import com.adadapted.android.sdk.core.session.SessionListener;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.factory.DeviceInfoFactory;
import com.adadapted.android.sdk.ext.factory.KeywordInterceptManagerFactory;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ui.model.SuggestionPayload;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 6/25/15
 */
public class AaKeywordInterceptMatcher implements SessionListener, KeywordInterceptManager.Listener {
    private static final String LOGTAG = AaKeywordInterceptMatcher.class.getName();

    private final KeywordInterceptManager mManager;
    private final AaSuggestionTracker mSuggestionTracker;

    private KeywordIntercept mKeywordIntercept;
    private boolean mLoaded = false;
    private Session mSession;

    public AaKeywordInterceptMatcher() {
        DeviceInfo deviceInfo = DeviceInfoFactory.getsDeviceInfo();

        mManager = KeywordInterceptManagerFactory.createKeywordInterceptManager(deviceInfo);
        mManager.setListener(this);

        mSuggestionTracker = new AaSuggestionTracker(mManager);

        SessionManagerFactory.addListener(this);
    }

    public SuggestionPayload match(CharSequence constraint) {
        Set<String> suggestions = new HashSet<>();

        if((isLoaded() && constraint != null && constraint.length() >= mKeywordIntercept.getMinMatchLength())) {
            for (String item : mKeywordIntercept.getAutoFill().keySet()) {
                if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                    AutoFill autofill = mKeywordIntercept.getAutoFill().get(item);
                    suggestions.add(autofill.getReplacement());

                    mSuggestionTracker.suggestionMatched(mSession, item, autofill.getReplacement(), constraint.toString());
                }
            }
        }

        return new SuggestionPayload(mSuggestionTracker, suggestions);
    }

    public boolean suggestionSelected(String suggestion) {
        return mSuggestionTracker.suggestionSelected(suggestion);
    }

    private boolean isLoaded() {
        return mLoaded;
    }

    @Override
    public void onKeywordInterceptInitSuccess(KeywordIntercept keywordIntercept) {
        mKeywordIntercept = keywordIntercept;
        mLoaded = true;
    }

    @Override
    public void onSessionInitialized(Session session) {
        mSession = session;
        mManager.init(session);
    }

    @Override
    public void onSessionInitFailed() {}

    @Override
    public void onNewAdsAvailable(Session session) {}
}
