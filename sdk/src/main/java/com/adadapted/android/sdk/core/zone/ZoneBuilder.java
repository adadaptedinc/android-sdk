package com.adadapted.android.sdk.core.zone;

import com.adadapted.android.sdk.core.zone.model.Zone;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chrisweeden on 3/27/15.
 */
public interface ZoneBuilder {
    Map<String, Zone> buildZones(JSONObject jsonZones);
}
