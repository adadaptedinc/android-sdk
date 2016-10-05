package com.adadapted.android.sdk.core.ad.model;

import java.io.Serializable;

/**
 * Created by chrisweeden on 4/9/15.
 */
public abstract class AdAction implements Serializable {
    private static final long serialVersionUID = 42L;

    public static final String CONTENT = "c";
    public static final String DELEGATE = "d";
    public static final String LINK = "l";
    public static final String NULLACTION = "null";
    public static final String POPUP = "p";

    private final String actionType;
    private String actionPath = "";

    public AdAction(final String actionType) {
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }

    public String getActionPath() {
        return actionPath;
    }

    public void setActionPath(final String actionPath) {
        this.actionPath = actionPath;
    }
}
