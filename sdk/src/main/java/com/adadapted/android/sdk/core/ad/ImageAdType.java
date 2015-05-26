package com.adadapted.android.sdk.core.ad;

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
        setAdType(AdTypes.IMAGE);
    }

    public Map<String, AdImage> getImages() {
        return images;
    }

    public AdImage getStandardImages() {
        return images.get(STANDARD_IMAGE);
    }

    public AdImage getRetinaImages() {
        return images.get(RETINA_IMAGE);
    }

    public void setImages(Map<String, AdImage> images) {
        this.images = images;
    }

    public String getImageUrlFor(String resolution, String orientation) {
        return images.get(resolution).getOrientation(orientation);
    }
}
