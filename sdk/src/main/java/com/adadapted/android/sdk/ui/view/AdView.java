package com.adadapted.android.sdk.ui.view;

import com.adadapted.android.sdk.core.ad.model.Ad;

/**
 * Created by chrisweeden on 7/7/15.
 */
interface AdView {
    void buildView(Ad ad);
    void setAdInteractionListener(AdInteractionListener listener);
    void removeAdInteractionListener();
}
