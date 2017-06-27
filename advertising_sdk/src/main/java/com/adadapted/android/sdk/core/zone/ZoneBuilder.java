package com.adadapted.android.sdk.core.zone;

import com.adadapted.android.sdk.core.zone.model.Zone;

import org.json.JSONObject;

import java.util.Map;

public interface ZoneBuilder {
    Map<String, Zone> buildZones(JSONObject jsonZones);
}
