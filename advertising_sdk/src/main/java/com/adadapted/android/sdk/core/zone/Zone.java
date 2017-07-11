package com.adadapted.android.sdk.core.zone;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.common.Dimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zone {
    private final String id;
    private final Map<String, Dimension> dimensions;
    private final List<Ad> ads;

    public Zone(final String id,
                final Map<String, Dimension> dimensions,
                final List<Ad> ads) {
        this.id = id;
        this.dimensions = dimensions;
        this.ads = ads;
    }

    public static Zone emptyZone() {
        return new Zone("", new HashMap<String, Dimension>(), new ArrayList<Ad>());
    }

    public String getId() {
        return id;
    }

    public Map<String, Dimension> getDimensions() {
        return dimensions;
    }

    public List<Ad> getAds() {
        return ads;
    }

    public boolean isEmpty() {
        return ads.isEmpty();
    }

    public boolean isNotEmpty() {
        return !ads.isEmpty();
    }

    public boolean hasAds() {
        return !ads.isEmpty();
    }

    public static class Builder {
        private String zoneId;
        private Map<String, Dimension> dimensions;
        private List<Ad> ads;

        public String getZoneId() {
            return zoneId;
        }

        public void setZoneId(String zoneId) {
            this.zoneId = zoneId;
        }

        public Map<String, Dimension> getDimensions() {
            return dimensions;
        }

        public void setDimensions(Map<String, Dimension> dimensions) {
            this.dimensions = dimensions;
        }

        public List<Ad> getAds() {
            return ads;
        }

        public void setAds(List<Ad> ads) {
            this.ads = ads;
        }

        public Zone build() {
            return new Zone(
                getZoneId(),
                getDimensions(),
                getAds()
            );
        }
    }
}
