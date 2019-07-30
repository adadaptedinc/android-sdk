package com.adadapted.android.sdk.core.addit;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PayloadContentParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void parse() {
        final JSONObject json = new JSONObject();

        final PayloadContentParser parser = new PayloadContentParser();
        final List<AdditContent> content = parser.parse(json);

        assertEquals(1, content.size());
    }
}