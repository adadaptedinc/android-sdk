package com.adadapted.sdk.demoadditapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.adadapted.sdk.addit.AdAdapted;
import com.adadapted.sdk.addit.ui.AdditContentListener;
import com.adadapted.sdk.addit.ui.AdditContent;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/26/16.
 */
public class DemoAdditApplication extends Application {
    private static final String LOGTAG = DemoAdditApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        AdAdapted.init()
                .withAppId("ADDITDEMOAPP")
                .inEnv(AdAdapted.Env.DEV)
                .setAdditContentListener(new AdditContentListener() {
                    @Override
                    public void onContentAvailable(final AdditContent content) {
                        if(content != null) {
                            switch(content.getType()) {
                                case AdditContent.ADD_TO_LIST_ITEMS:
                                    JSONObject payloadJson = content.getPayload();

                                    Log.i(LOGTAG, "Received Addit Json Payload: " + payloadJson.toString());

                                    content.acknowledge();

                                    final Intent intent = new Intent(content.getActivity(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    content.getActivity().finish();
                                    startActivity(intent);
                                    break;

                                default:
                                    Log.w(LOGTAG, "Unusable Addit content provided.");
                                    content.failed("Unknonwn content type");
                            }

                        }
                        else {
                            Log.w(LOGTAG, "Null Addit content provided.");
                        }
                    }
                })
                .start(this);
    }
}
