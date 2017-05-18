package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adadapted.android.sdk.R;
import com.adadapted.android.sdk.core.ad.AdImageLoader;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdComponent;
import com.adadapted.android.sdk.core.ad.model.JsonAdType;
import com.adadapted.android.sdk.ext.http.HttpAdImageLoader;


/**
 * Created by chrisweeden on 5/26/15
 */
class JsonAdViewBuildingStrategy implements AdViewBuildingStrategy {
    private static final String LOGTAG = JsonAdViewBuildingStrategy.class.getName();

    private final Listener mListener;
    private final Context mContext;
    private final AdImageLoader mImageLoader;

    private View mView;

    public JsonAdViewBuildingStrategy(final Context context, final Listener listener) {
        mContext = context.getApplicationContext();
        mListener = listener;
        mImageLoader = new HttpAdImageLoader();

        mView = new View(mContext);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void buildView(final Ad ad,
                          final int width,
                          final int height,
                          final AaZoneViewProperties zoneProperties) {
        int resourceId = zoneProperties.getResourceId();

        if(resourceId == 0) {
            if(mListener != null) {
                mListener.onStrategyViewLoadFailed();
            }

            return;
        }

        mView = View.inflate(mContext, resourceId, null);
        if(mView == null) {
            if(mListener != null) {
                mListener.onStrategyViewLoadFailed();
            }

            return;
        }

        mView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        mView.setBackgroundColor(zoneProperties.getBackgroundColor());

        AdComponent adComponents = ((JsonAdType)ad.getAdType()).getComponents();

        TextView cta1 = (TextView)mView.findViewById(R.id.aa_ad_cta_1);
        if(cta1 != null) {
            cta1.setText(adComponents.getCta1());
        }

        TextView cta2 = (TextView)mView.findViewById(R.id.aa_ad_cta_2);
        if(cta2 != null) {
            cta2.setText(adComponents.getCta2());
        }

        ImageView campaignImage = (ImageView)mView.findViewById(R.id.aa_ad_campaign_image);
        if(campaignImage != null) {
            loadImageViewFromUrl(campaignImage, adComponents.getCampaignImage());
        }

        ImageView sponsorLogo = (ImageView)mView.findViewById(R.id.aa_ad_sponsor_logo);
        if(sponsorLogo != null) {
            loadImageViewFromUrl(sponsorLogo, adComponents.getSponsorLogo());
        }

        TextView sponsorName = (TextView)mView.findViewById(R.id.aa_ad_sponsor_name);
        if(sponsorName != null) {
            sponsorName.setText(adComponents.getSponsorName());
        }

        TextView title = (TextView)mView.findViewById(R.id.aa_ad_title);
        if(title != null) {
            title.setText(adComponents.getTitle());
        }

        TextView tagLine = (TextView)mView.findViewById(R.id.aa_ad_tag_line);
        if(tagLine != null) {
            tagLine.setText(adComponents.getTagLine());
        }

        TextView longText = (TextView)mView.findViewById(R.id.aa_ad_long_text);
        if(longText != null) {
            longText.setText(adComponents.getLongText());
        }

        TextView sponsorText = (TextView)mView.findViewById(R.id.aa_ad_sponsor_text);
        if(sponsorText != null) {
            sponsorText.setText(adComponents.getSponsorText());
        }

        ImageView appIcon1 = (ImageView)mView.findViewById(R.id.aa_ad_app_icon_1);
        if(appIcon1 != null) {
            loadImageViewFromUrl(appIcon1, adComponents.getAppIcon1());
        }

        ImageView appIcon2 = (ImageView)mView.findViewById(R.id.aa_ad_app_icon_2);
        if(appIcon2 != null) {
            loadImageViewFromUrl(appIcon2, adComponents.getAppIcon2());
        }

        if(mListener != null) {
            mListener.onStrategyViewLoaded();
        }
    }

    private void loadImageViewFromUrl(final ImageView imageView, final String url) {
        if(imageView == null || url == null) {
            return;
        }

        mImageLoader.getImage(url, new AdImageLoader.Callback() {
            @Override
            public void adImageLoaded(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void adImageLoadFailed() {

            }
        });
    }

    @Override
    public String toString() {
        return "JsonAdViewBuildingStrategy{}";
    }
}
