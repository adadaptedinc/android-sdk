package com.adadapted.android.sdk.core.ad.model;

/**
 * Created by chrisweeden on 4/15/15.
 */
public class JsonAdType extends AdType {
    private AdComponent components;

    public JsonAdType() {
        setType(JSON);
        components = new AdComponent();
    }

    public AdComponent getComponents() {
        return components;
    }

    public void setComponents(final AdComponent components) {
        this.components = components;
    }
}
