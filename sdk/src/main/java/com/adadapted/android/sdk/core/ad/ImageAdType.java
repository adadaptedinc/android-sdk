package com.adadapted.android.sdk.core.ad;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 4/15/15.
 */
public class ImageAdType extends AdType {
    private Map<String, AdImage> images;

    public ImageAdType() {
        images = new HashMap<>();
        setAdType(AdTypes.IMAGE);
    }

    public Map<String, AdImage> getImages() {
        return images;
    }

    public AdImage getStandardImages() {
        return images.get("standard");
    }

    public AdImage getRetinaImages() {
        return images.get("retina");
    }

    public void setImages(Map<String, AdImage> images) {
        this.images = images;
    }
}
