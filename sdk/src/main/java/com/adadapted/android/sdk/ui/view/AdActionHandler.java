package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.model.AdAction;
import com.adadapted.android.sdk.core.ad.model.ContentAdAction;
import com.adadapted.android.sdk.core.content.ContentPayload;
import com.adadapted.android.sdk.ui.model.ViewAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by chrisweeden on 7/1/15.
 */
public class AdActionHandler {
    private static final String TAG = AdActionHandler.class.getName();

    private final Context context;

    public AdActionHandler(Context context) {
        this.context = context;
    }

    public void handleAction(ViewAd ad) {
        switch(ad.getAd().getAdAction().getActionType()) {
            case AdAction.CONTENT:
                Log.d(TAG, "Handling CONTENT Ad Action");

                List items = ((ContentAdAction)ad.getAd().getAdAction()).getItems();
                JSONObject json = new JSONObject();

                try {
                    json.put("add_to_list_items", new JSONArray(items));
                }
                catch(JSONException ex) {
                    Log.w(TAG, "Problem parsing JSON");
                }

                ContentPayload payload = new ContentPayload(ContentPayload.ADD_TO_LIST, json);
                AdAdapted.getInstance().publishContent(ad.getAd().getZoneId(), payload);

                break;

            case AdAction.DELEGATE:
                Log.d(TAG, "Handling DELEGATE Ad Action");
                break;

            case AdAction.NULLACTION:
                Log.d(TAG, "Handling NULL Ad Action");
                break;

            case AdAction.POPUP:
                Log.d(TAG, "Handling POPUP Ad Action");

                Intent intent = new Intent(context, WebViewPopupActivity.class);
                intent.putExtra(WebViewPopupActivity.EXTRA_POPUP_AD, ad.getAd());
                intent.putExtra(WebViewPopupActivity.EXTRA_SESSSION_ID, ad.getSessionId());
                context.startActivity(intent);

                break;
        }
    }
}
