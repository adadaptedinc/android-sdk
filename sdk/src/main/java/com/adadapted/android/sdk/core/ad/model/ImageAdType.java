package com.adadapted.android.sdk.core.ad.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 4/15/15.
 */
public class ImageAdType extends AdType {
    private Map<String, AdImage> images;

    public static final String STANDARD_IMAGE = "standard";
    public static final String RETINA_IMAGE = "retina";

    public ImageAdType() {
        images = new HashMap<>();
        setType(AdTypes.IMAGE);
    }

    public void setImages(final Map<String, AdImage> images) {
        this.images = images;
    }

    public String getImageUrlFor(final String resolution, final String orientation) {
        return images.get(resolution).getOrientation(orientation);
    }
}
