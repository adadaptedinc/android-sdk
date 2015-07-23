package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.HtmlAdType;

/**
 * Created by chrisweeden on 5/20/15.
 */
class HtmlAdViewBuildingStrategy implements AdViewBuildingStrategy {
    private static final String TAG = HtmlAdViewBuildingStrategy.class.getName();

    public interface Listener {
        void onHtmlViewLoaded();
    }

    private final Listener listener;
    private final WebView view;

    public HtmlAdViewBuildingStrategy(final Context context, final Listener listener) {
        this.listener = listener;

        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        //        ViewGroup.LayoutParams.MATCH_PARENT,
        //        ViewGroup.LayoutParams.MATCH_PARENT);

        view = new WebView(context);
        //view.setLayoutParams(layoutParams);
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

        String dummyDocument = "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /><style>body{width:100px;height100px;}</style></head><body></body></html>";
        view.loadData(dummyDocument, "text/html", null);
    }

    @Override
    public void buildView(Ad ad) {
        final HtmlAdType adType = (HtmlAdType) ad.getAdType();

        view.loadUrl(adType.getAdUrl());
        listener.onHtmlViewLoaded();
    }

    @Override
    public View getView() {
        return view;
    }
}
