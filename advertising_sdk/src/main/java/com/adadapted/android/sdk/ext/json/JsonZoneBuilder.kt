package com.adadapted.android.sdk.ext.json

import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.common.Dimension
import com.adadapted.android.sdk.core.common.DimensionConverter
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance
import com.adadapted.android.sdk.core.zone.Zone
import org.json.JSONException
import org.json.JSONObject

class JsonZoneBuilder(deviceScale: Float) {
    private val jsonAdBuilder: JsonAdBuilder = JsonAdBuilder()
    private val dimensionConverter: DimensionConverter = DimensionConverter(deviceScale)

    fun buildZones(jsonZones: JSONObject): Map<String, Zone> {
        val zones: MutableMap<String, Zone> = HashMap()
        val z = jsonZones.keys()
        while (z.hasNext()) {
            try {
                val zoneId = z.next()
                val jsonZone = jsonZones.getJSONObject(zoneId)
                val zone = buildZone(zoneId, jsonZone)
                zones[zoneId] = zone
            } catch (ex: Exception) {
                Log.w(LOGTAG, "Problem converting to JSON.", ex)
                val errorParams: MutableMap<String, String> = HashMap()
                errorParams["bad_json"] = jsonZones.toString()
                ex.message?.let { errorParams["exception"] = it }
                getInstance().trackError(EventStrings.SESSION_ZONE_PAYLOAD_PARSE_FAILED, "Failed to parse Session Zone payload for processing.", errorParams)
            }
        }
        return zones
    }

    @Throws(JSONException::class, NumberFormatException::class)
    private fun buildZone(zoneId: String, jsonZone: JSONObject): Zone {
        val newZone = Zone(zoneId)

        if (jsonZone.has(PORT_ZONE_HEIGHT) && jsonZone.has(PORT_ZONE_WIDTH)) {
            val portDimension = Dimension()
            portDimension.height = calculateDimensionValue(JsonHelper.tryGetIntFromJson(jsonZone, PORT_ZONE_HEIGHT))
            portDimension.width = calculateDimensionValue(JsonHelper.tryGetIntFromJson(jsonZone, PORT_ZONE_WIDTH))
            newZone.setDimension(Dimension.Orientation.PORT, portDimension)
        }

        if (jsonZone.has(LAND_ZONE_HEIGHT) && jsonZone.has(LAND_ZONE_WIDTH)) {
            val landDimension = Dimension()
            landDimension.height = calculateDimensionValue(JsonHelper.tryGetIntFromJson(jsonZone, LAND_ZONE_HEIGHT))
            landDimension.width = calculateDimensionValue(JsonHelper.tryGetIntFromJson(jsonZone, LAND_ZONE_WIDTH))
            newZone.setDimension(Dimension.Orientation.LAND, landDimension)
        }

        val jsonAds = jsonZone.getJSONArray(ADS)
        newZone.ads = jsonAdBuilder.buildAds(zoneId, jsonAds)
        return newZone
    }

    @Throws(NumberFormatException::class)
    private fun calculateDimensionValue(value: Int): Int {
        return dimensionConverter.convertDpToPx(value)
    }

    companion object {
        private val LOGTAG = JsonZoneBuilder::class.java.name
        private const val ADS = "ads"
        private const val PORT_ZONE_HEIGHT = "port_height"
        private const val PORT_ZONE_WIDTH = "port_width"
        private const val LAND_ZONE_HEIGHT = "land_height"
        private const val LAND_ZONE_WIDTH = "land_width"
    }
}