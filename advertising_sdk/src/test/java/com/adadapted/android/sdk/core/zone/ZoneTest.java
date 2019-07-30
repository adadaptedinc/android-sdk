package com.adadapted.android.sdk.core.zone;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.common.Dimension;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class ZoneTest {

    @Test
    public void emptyZone() {
        final Zone zone = Zone.emptyZone();

        assertEquals("", zone.getId());
        assertEquals(0, zone.getDimensions().size());
        assertEquals(0, zone.getAds().size());
        assertFalse(zone.hasAds());
    }

    @Test
    public void builder() {
        final Zone.Builder builder = new Zone.Builder();
        builder.setZoneId("test_zone");
        builder.setDimension(Dimension.Orientation.PORT, new Dimension());
        builder.setDimension(Dimension.Orientation.LAND, new Dimension());
        builder.setAds(new LinkedList<Ad>());

        final Zone zone = builder.build();

        assertEquals("test_zone", zone.getId());
    }
}