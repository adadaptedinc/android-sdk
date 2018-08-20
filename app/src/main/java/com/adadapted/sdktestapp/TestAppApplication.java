package com.adadapted.sdktestapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.addit.AdditContent;
import com.adadapted.android.sdk.core.atl.AddToListContent;
import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.ui.messaging.AaSdkAdditContentListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkSessionListener;

import com.adadapted.android.sdk.core.ad.AdContent;
import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.core.todo.TodoListManager;
import com.adadapted.sdktestapp.ui.todo.activity.TodoListsActivity;

import com.flurry.android.FlurryAgent;

import com.squareup.leakcanary.LeakCanary;

import java.util.List;
import java.util.Locale;

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

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, getString(R.string.flurry_api_id));

        //NewRelic.withApplicationToken(getString(R.string.newrelic_api_id)).start(this);

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
                public void onContentAvailable(final AddToListContent content) {
                    try {
                        final List<AddToListItem> listItems = content.getItems();

                        final TodoList list = TodoListManager.getInstance(TestAppApplication.this).getDefaultList();

                        for (final AddToListItem item : listItems) {
                            TodoListManager
                                .getInstance(TestAppApplication.this)
                                .addItemToList(list.getId(), item.getTitle());

                            content.itemAcknowledge(item);
                        }

                        content.acknowledge();

                        if (content.getSource().equals(AddToListContent.Sources.DEEPLINK)) {
                            final Intent intent = new Intent(getApplicationContext(), TodoListsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                        } else {
                            Toast.makeText(
                                TestAppApplication.this,
                                String.format(Locale.ENGLISH, "%d item(s) added to Default List", listItems.size()),
                                Toast.LENGTH_LONG).show();
                        }
                    }
                    catch(Exception ex) {
                        Log.e(TAG, "Error handling Addit payload", ex);
                        content.failed("Client Error handling Addit payload");
                    }
                }
            })
            .start(this);

        AdAdapted.init()
            .withAppId(apiKey)
            .inEnv(AdAdapted.Env.DEV)
            .start(this);
    }
}
