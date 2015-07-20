package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adadapted.android.sdk.R;
import com.adadapted.android.sdk.core.ad.AdImageLoader;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdComponent;
import com.adadapted.android.sdk.core.ad.model.JsonAdType;
import com.adadapted.android.sdk.ext.http.HttpAdImageLoader;

/**
 * Created by chrisweeden on 5/26/15.
 */
class JsonAdView implements AdView {
    private static final String TAG = HtmlAdView.class.getName();

    public interface Listener {
        void onJsonViewLoaded();
    }

    private final Listener listener;
    private final View view;

    private final AdImageLoader imageLoader;

    public JsonAdView(final Context context, final Listener listener, int resourceId) {
        this.listener = listener;
        this.imageLoader = new HttpAdImageLoader();

        view = View.inflate(context, resourceId, null);
    }

    @Override
    public void buildView(Ad ad) {
        AdComponent adComponents = ((JsonAdType)ad.getAdType()).getComponents();

        TextView cta1 = (TextView)view.findViewById(R.id.aa_ad_cta_1);
        if(cta1 != null) {
            cta1.setText(adComponents.getCta1());
        }

        TextView cta2 = (TextView)view.findViewById(R.id.aa_ad_cta_2);
        if(cta2 != null) {
            cta2.setText(adComponents.getCta2());
        }

        ImageView campaignImage = (ImageView)view.findViewById(R.id.aa_ad_campaign_image);
        if(campaignImage != null) {
            loadImageViewFromUrl(campaignImage, adComponents.getCampaignImage());
        }

        ImageView sponsorLogo = (ImageView)view.findViewById(R.id.aa_ad_sponsor_logo);
        if(sponsorLogo != null) {
            loadImageViewFromUrl(sponsorLogo, adComponents.getSponsorLogo());
        }

        TextView sponsorName = (TextView)view.findViewById(R.id.aa_ad_sponsor_name);
        if(sponsorName != null) {
            sponsorName.setText(adComponents.getSponsorName());
        }

        TextView title = (TextView)view.findViewById(R.id.aa_ad_title);
        if(title != null) {
            title.setText(adComponents.getTitle());
        }

        TextView tagLine = (TextView)view.findViewById(R.id.aa_ad_tag_line);
        if(tagLine != null) {
            tagLine.setText(adComponents.getTagLine());
        }

        TextView longText = (TextView)view.findViewById(R.id.aa_ad_long_text);
        if(longText != null) {
            longText.setText(adComponents.getLongText());
        }

        TextView sponsorText = (TextView)view.findViewById(R.id.aa_ad_sponsor_text);
        if(sponsorText != null) {
            sponsorText.setText(adComponents.getSponsorText());
        }

        ImageView appIcon1 = (ImageView)view.findViewById(R.id.aa_ad_app_icon_1);
        if(appIcon1 != null) {
            loadImageViewFromUrl(appIcon1, adComponents.getAppIcon1());
        }

        ImageView appIcon2 = (ImageView)view.findViewById(R.id.aa_ad_app_icon_2);
        if(appIcon2 != null) {
            loadImageViewFromUrl(appIcon2, adComponents.getAppIcon2());
        }

        listener.onJsonViewLoaded();
    }

    @Override
    public View getView() {
        return view;
    }

    private void loadImageViewFromUrl(final ImageView imageView, final String url) {
        imageLoader.getImage(url, new AdImageLoader.Listener() {
            @Override
            public void onAdImageLoaded(final Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onAdImageLoadFailed() {}
        });
    }
}
