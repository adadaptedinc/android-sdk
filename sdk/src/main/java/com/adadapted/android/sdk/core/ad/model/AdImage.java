package com.adadapted.android.sdk.core.ad.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 3/30/15.
 */
public class AdImage implements Serializable {
    private static final long serialVersionUID = 42L;

    private final Map<String, String> orientations;

    public static final String PORTRAIT = "port";
    public static final String LANDSCAPE = "land";

    public AdImage() {
        orientations = new HashMap<>();
    }

    public void addOrientation(String orientation, String url) {
        orientations.put(orientation, url);
    }

    public String getOrientation(String orientation) {
        return orientations.get(orientation);
    }
}
