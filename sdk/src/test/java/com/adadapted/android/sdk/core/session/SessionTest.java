package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 4/28/15.
 */
public class SessionTest {
    private static final String SESSIONID = "sessionid";
    private static final boolean ACTIVECAMPAIGNS = true;
    private static final Date EXPIRESAT = new Date();
    private static final long POLLINGINTERVAL = 30000;
    private static final Map<String, Zone> ZONES = new HashMap<>();

    private Session session;

    @Before
    public void setUp() {
        session = new Session();
        session.setSessionId(SESSIONID);
        session.setActiveCampaigns(ACTIVECAMPAIGNS);
        session.setExpiresAt(EXPIRESAT);
        session.setPollingInterval(POLLINGINTERVAL);
        session.updateZones(ZONES);
    }

    @Test
    public void testGetSessionId() {
        Assert.assertEquals(SESSIONID, session.getSessionId());
    }

    @Test
    public void testHasActiveCampaigns() {
        Assert.assertEquals(ACTIVECAMPAIGNS, session.hasActiveCampaigns());
    }

    @Test
    public void testGetExpiresAt() {
        Assert.assertEquals(EXPIRESAT, session.getExpiresAt());
    }

    @Test
    public void testGetPollingInterval() {
        Assert.assertEquals(POLLINGINTERVAL, session.getPollingInterval());
    }

    @Test
    public void testHasExpired_WhenExpired_ReturnsTrue() {
        Assert.assertTrue(session.hasExpired());
    }

    @Test
    public void testHasExpired_WhenNotExpired_ReturnsFalse() {
        session.setExpiresAt(EXPIRESAT.getTime() + 10000);
        Assert.assertFalse(session.hasExpired());
    }

    @Test
    public void testUpdateZones_WithNewZones_OnlyContainsNewZones() {
        Map<String, Zone> zones = new HashMap<>(ZONES);
        zones.put("10", new Zone("10"));
        zones.put("11", new Zone("11"));

        session.updateZones(zones);

        Assert.assertEquals(zones, session.getZones());
    }
}