package com.adadapted.android.sdk.core.ad.model;

import java.io.Serializable;

/**
 * Created by chrisweeden on 4/9/15.
 */
public abstract class AdAction implements Serializable {
    static final long serialVersionUID = 42L;

    public static final String CONTENT = "content";
    public static final String DELEGATE = "delegate";
    public static final String NULLACTION = "null";
    public static final String POPUP = "popup";

    private String actionType = "";
    private String actionPath = "";

    public AdAction(String actionType) {
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionPath() {
        return actionPath;
    }

    public void setActionPath(String actionPath) {
        this.actionPath = actionPath;
    }
}
