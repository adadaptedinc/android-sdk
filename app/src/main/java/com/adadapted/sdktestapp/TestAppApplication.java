package com.adadapted.sdktestapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.addit.AaSdkAdditContentListener;
import com.adadapted.android.sdk.addit.AdditContentPayload;
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener;
import com.adadapted.android.sdk.ui.messaging.AaSdkSessionListener;
import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.core.todo.TodoListManager;
import com.adadapted.sdktestapp.ui.todo.activity.TodoListDetailActivity;
import com.adadapted.sdktestapp.ui.todo.activity.TodoListsActivity;
import com.flurry.android.FlurryAgent;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

        AdAdapted.init(this)
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
                public void onContentAvailable(AdditContentPayload payload) {
                    try {
                        JSONArray listItems = payload.getPayload().getJSONArray("add_to_list_items");

                        TodoList list = TodoListManager.getInstance(TestAppApplication.this).getDefaultList();

                        int itemCount = listItems.length();
                        for (int i = 0; i < itemCount; i++) {
                            JSONObject item = (JSONObject) listItems.get(i);
                            TodoListManager
                                    .getInstance(TestAppApplication.this)
                                    .addItemToList(list.getId(), item.getString("product_title"));
                        }

                        payload.acknowledge();

                        final Intent intent = new Intent(payload.getActivity(), TodoListsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        payload.getActivity().finish();
                        startActivity(intent);
                    }
                    catch(JSONException ex) {
                        Log.e(TAG, "Error handling Addit payload", ex);
                    }
                }
            })
            .start();

        AdAdapted.registerEvent("app_opened");
    }
}
