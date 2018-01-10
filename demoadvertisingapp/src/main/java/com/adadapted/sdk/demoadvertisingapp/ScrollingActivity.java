package com.adadapted.sdk.demoadvertisingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.ui.messaging.AdContentListener;
import com.adadapted.android.sdk.ui.model.AdContent;
import com.adadapted.android.sdk.ui.view.AaZoneView;

import java.util.List;

public class ScrollingActivity extends AppCompatActivity {
    private static final String TAG = ScrollingActivity.class.getName();

    private AaZoneView m_adAdAdapted;

    private boolean m_activityStarted  = false;
    private boolean m_adAdaptedLoaded  = false;
    private boolean m_adAdaptedStarted = false;

    /**
     * Listen for whether the AdAdapted ad has come in so we can display the ad section.
     */
    private final AaZoneView.Listener m_adAdaptedListener = new AaZoneView.Listener() {
        // Interface method added with the 1.1.8 SDK update
        // On Zone init and after other content refreshes will return true if the Zone has Ads to
        // serve and can be used to show/hide Zone
        @Override
        public void onZoneHasAds(boolean hasAds) {
            m_adAdaptedLoaded = hasAds;
            Log.i(TAG, "Has AdAdapted ads To Serve: " + hasAds);

            updateAdInterface();
        }

        // Will be called on each Ad load. Could be used to show the Zone if it has been previously hidden.
        // The view probably doesn't need to change the visibility of the Zone on each Ad load.
        @Override
        public void onAdLoaded() {
            Log.i(TAG, "Got AdAdapted ad");
        }

        // Will be called if there is an issue loading an Ad when either there isn't an Ad to load
        // or there is a problem when loading and Ad
        @Override
        public void onAdLoadFailed() {
            m_adAdaptedLoaded = false;
            Log.i(TAG, "Did not get AdAdapted ad");

            updateAdInterface();
        }
    };

    /**
     * Listener for AdAdapted's Add-to-List feature, when the user wants to add the item
     * to their list.
     */

    // Updated to match the new Listener which returns a better defined object and doesn't require sorting through a JSON object
    private final AdContentListener m_adAdaptedContentListener = new AdContentListener() {
        @Override
        public void onContentAvailable(final String zoneId, final AdContent content) {
            final List<AddToListItem> items = content.getItems();

            for (final AddToListItem item : items) {
                // App specific item handling
            }

            // Tell AdAdapted that the user did this.
            content.acknowledge();
        }
    };

    /**
     * Start or stop the AdAdapted ad based on whether our activity is started and whether
     * we're displaying the ad.
     */
    private void syncAdAdaptedState() {
        // Figure out whether we want AdAdapted to be started.
        boolean wantStarted = m_activityStarted && OgAdAdapted.IS_ENABLED;

        // Sync that up with the ad's state.
        if (wantStarted != m_adAdaptedStarted) {
            if (wantStarted) {
                m_adAdAdapted.onStart(m_adAdaptedListener, m_adAdaptedContentListener);

                // We'll get called back on the m_adAdaptedListener to tell us if the ad
                // loaded successfully or not.
            } else {
                m_adAdAdapted.onStop(m_adAdaptedContentListener);
            }
            m_adAdaptedStarted = wantStarted;
        }
    }

    /**
     * Update the UI for the ad.
     */
    private void updateAdInterface() {
        m_adAdAdapted.setVisibility(View.GONE);

        if (m_adAdaptedLoaded) {
            m_adAdAdapted.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        m_adAdAdapted = findViewById(R.id.item_details_AdAdapted);
        OgAdAdapted.initZone(m_adAdAdapted);
    }

    @Override
    public void onStart() {
        super.onStart();
        m_activityStarted = true;

        syncAdAdaptedState();
    }

    @Override
    public void onStop() {
        super.onStop();
        m_activityStarted = false;

        syncAdAdaptedState();
    }
}
