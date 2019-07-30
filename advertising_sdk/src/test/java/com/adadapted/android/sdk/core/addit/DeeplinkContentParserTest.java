package com.adadapted.android.sdk.core.addit;

import android.net.Uri;

import com.adadapted.android.sdk.core.atl.AddToListContent;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeeplinkContentParserTest {
    private static final String TEST_URL_STRING = "droidrecipe://adadapted.com/addit_add_list_items?data=eyJwYXlsb2FkX2lkIjoiNzQ5OEU2M0YtQkY0NC00NjZCLUEzOTEtOEI2NzIxQzMyRkY3IiwicGF5bG9hZF9tZXNzYWdlIjoiU2FtcGxlIFByb2R1Y3QiLCJwYXlsb2FkX2ltYWdlIjoiIiwiY2FtcGFpZ25faWQiOiIyNTQiLCJhcHBfaWQiOiJkcm9pZHJlY2lwZSIsImV4cGlyZV9zZWNvbmRzIjo2MDQ4MDAsImRldGFpbGVkX2xpc3RfaXRlbXMiOlt7InRyYWNraW5nX2lkIjoiRDA2OTk4OTItQkY5RS00RTM1LUI5MkQtQzVEN0YyRDZFOUFEIiwicHJvZHVjdF90aXRsZSI6IlNhbXBsZSBQcm9kdWN0IiwicHJvZHVjdF9icmFuZCI6IkJyYW5kIiwicHJvZHVjdF9jYXRlZ29yeSI6IiIsInByb2R1Y3RfYmFyY29kZSI6IjAiLCJwcm9kdWN0X3NrdSI6IiIsInByb2R1Y3RfZGlzY291bnQiOiIiLCJwcm9kdWN0X2ltYWdlIjoiaHR0cHM6XC9cL2ltYWdlcy5hZGFkYXB0ZWQuY29tXC8ifV19";

    @Test
    public void parse() throws Exception {
        final Uri uri = Uri.parse(TEST_URL_STRING);

        final DeeplinkContentParser parser = new DeeplinkContentParser();
        final AdditContent content  = parser.parse(uri);

        assertEquals("7498E63F-BF44-466B-A391-8B6721C32FF7", content.getPayloadId());
        assertEquals("Sample Product", content.getMessage());
        assertEquals("", content.getImage());
        assertEquals(AdditContent.AdditSources.DEEPLINK, content.getAdditSource());
        assertEquals(AddToListContent.Sources.OUT_OF_APP, content.getSource());
        assertEquals(1, content.getItems().size());
    }
}