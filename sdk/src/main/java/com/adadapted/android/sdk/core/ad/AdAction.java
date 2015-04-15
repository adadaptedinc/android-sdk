package com.adadapted.android.sdk.core.ad;

/**
 * Created by chrisweeden on 4/9/15.
 */
public abstract class AdAction {
    private String actionType;
    private String actionPath;

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
