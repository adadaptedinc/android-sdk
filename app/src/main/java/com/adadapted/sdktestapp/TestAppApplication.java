package com.adadapted.sdktestapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.addit.Content;
import com.adadapted.android.sdk.ui.messaging.AaSdkAdditContentListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkSessionListener;

import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.core.todo.TodoListManager;
import com.adadapted.sdktestapp.ui.todo.activity.TodoListsActivity;

import com.flurry.android.FlurryAgent;

import com.newrelic.agent.android.NewRelic;

import java.util.List;

/**
 * Created by chrisweeden on 3/16/15.
 */
public class TestAppApplication extends Application {
    private static final String TAG = TestAppApplication.class.getName();

    public TestAppApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, getString(R.string.flurry_api_id));

        NewRelic.withApplicationToken(getString(R.string.newrelic_api_id)).start(this);

        String apiKey = getResources().getString(R.string.adadapted_api_key);

        AdAdapted.init()
            .withAppId(apiKey)
            .inEnv(AdAdapted.Env.DEV)
            .setSdkSessionListener(new AaSdkSessionListener() {
                @Override
                public void onHasAdsToServe(boolean hasAds) {
                    Log.i(TAG, "Has Ads To Serve: " + hasAds);
                }
            })
            .setSdkEventListener(new AaSdkEventListener() {
                @Override
                public void onNextAdEvent(String zoneId, String eventType) {
                    Log.i(TAG, "Ad " + eventType + " for Zone " + zoneId);
                }
            })
            .setSdkAdditContentListener(new AaSdkAdditContentListener() {
                @Override
                public void onContentAvailable(Content payload) {
                    try {
                        List<AddToListItem> listItems = payload.getPayload();

                        TodoList list = TodoListManager.getInstance(TestAppApplication.this).getDefaultList();

                        for (AddToListItem item : listItems) {
                            TodoListManager
                                    .getInstance(TestAppApplication.this)
                                    .addItemToList(list.getId(), item.getTitle());
                        }

                        payload.acknowledge();

                        final Intent intent = new Intent(getApplicationContext(), TodoListsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                    }
                    catch(Exception ex) {
                        Log.e(TAG, "Error handling Addit payload", ex);
                        payload.failed("Client Error handling Addit payload");
                    }
                }
            })
            .start(this);
    }
}
