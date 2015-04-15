package com.adadapted.android.sdk.core.ad;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 3/30/15.
 */
public class AdImage {
    private final Map<String, String> orientations;

    public AdImage() {
        orientations = new HashMap<>();
    }

    public void addOrientation(String orientation, String url) {
        orientations.put(orientation, url);
    }

    public String getOrientation(String orientation) {
        return orientations.get(orientation);
    }

    @Override
    public String toString() {
        return "AdImage{" +
                "orientations=" + orientations +
                '}';
    }
}
