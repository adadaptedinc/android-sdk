package com.adadapted.demo.baskethandle.plugins;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adadapted.demo.baskethandle.app.common.MainActivity;
import com.adadapted.sdk.addit.AdAdapted;
import com.adadapted.sdk.addit.core.content.AddToListItem;
import com.adadapted.sdk.addit.core.content.Content;
import com.adadapted.sdk.addit.core.content.ContentTypes;
import com.adadapted.sdk.addit.ui.AdditContentListener;

import java.util.List;

/**
 * Created by chrisweeden on 10/4/16.
 */
public class AdditPlugin implements Plugin {
    private static final String LOGTAG = AdditPlugin.class.getName();

    @Override
    public void initialize(final Context context) {
        Log.i(LOGTAG, "Initializing Addit SDK.");
        AdAdapted.init()
                .withAppId("NTGXMGZHNTDIY2FK")
                .inEnv(AdAdapted.Env.DEV)
                .setAdditContentListener(new AdditContentListener() {
                    @Override
                    public void onContentAvailable(final Content content) {
                        if(content != null) {
                            switch(content.getType()) {
                                case ContentTypes.ADD_TO_LIST_ITEMS:
                                    List<AddToListItem> payload = content.getPayload();

                                    Log.i(LOGTAG, "Received Addit Content: " + payload.toString());

                                    content.acknowledge();

                                    final Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    context.startActivity(intent);
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
                .start(context);
    }


}
