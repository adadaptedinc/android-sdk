package com.adadapted.android.sdk.core.ad.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chrisweeden on 6/25/15.
 */
public class ContentAdAction extends AdAction {
    private List items;

    public ContentAdAction() {
        super(CONTENT);

        items = new ArrayList();
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }
}
