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
    private val mAdBuilder: JsonAdBuilder = JsonAdBuilder()
    private val mDimensionConverter: DimensionConverter = DimensionConverter(deviceScale)

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
                getInstance().trackError(
                        EventStrings.SESSION_ZONE_PAYLOAD_PARSE_FAILED,
                        "Failed to parse Session Zone payload for processing.",
                        errorParams
                )
            }
        }
        return zones
    }

    @Throws(JSONException::class, NumberFormatException::class)
    private fun buildZone(zoneId: String, jsonZone: JSONObject): Zone {
        val newZone = Zone(zoneId)

        if (jsonZone.has(PORT_ZONE_HEIGHT) && jsonZone.has(PORT_ZONE_WIDTH)) {
            val portDimension = Dimension()

            if (jsonZone.has(PORT_ZONE_HEIGHT)) {
                portDimension.height = calculateDimensionValue(jsonZone.getString(PORT_ZONE_HEIGHT))
            } else {
                portDimension.height = Dimension.WRAP_CONTENT
            }

            if (jsonZone.has(PORT_ZONE_WIDTH)) {
                portDimension.width = calculateDimensionValue(jsonZone.getString(PORT_ZONE_WIDTH))
            } else {
                portDimension.width = Dimension.MATCH_PARENT
            }

            newZone.setDimension(Dimension.Orientation.PORT, portDimension)
        }

        if (jsonZone.has(LAND_ZONE_HEIGHT) && jsonZone.has(LAND_ZONE_WIDTH)) {
            val landDimension = Dimension()

            if (jsonZone.has(LAND_ZONE_HEIGHT)) {
                landDimension.height = calculateDimensionValue(jsonZone.getString(LAND_ZONE_HEIGHT))
            } else {
                landDimension.height = Dimension.WRAP_CONTENT
            }

            if (jsonZone.has(LAND_ZONE_WIDTH)) {
                landDimension.width = calculateDimensionValue(jsonZone.getString(LAND_ZONE_WIDTH))
            } else {
                landDimension.width = Dimension.MATCH_PARENT
            }

            newZone.setDimension(Dimension.Orientation.LAND, landDimension)
        }

        val jsonAds = jsonZone.getJSONArray(ADS)
        newZone.ads = mAdBuilder.buildAds(zoneId, jsonAds)
        return newZone
    }

    @Throws(NumberFormatException::class)
    private fun calculateDimensionValue(value: String): Int {
        return mDimensionConverter.convertDpToPx(value.toInt())
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