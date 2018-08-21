package com.adadapted.android.sdk.core.atl;

import java.util.List;

public interface AddToListContent {
    final class Sources {
        public static final String DEEPLINK = "deeplink";
        public static final String IN_APP = "in_app";
        public static final String PAYLOAD = "payload";
    }

    void acknowledge();
    void itemAcknowledge(AddToListItem item);
    void failed(String message);
    void itemFailed(AddToListItem item, String message);
    String getSource();
    List<AddToListItem> getItems();
    boolean hasItems();
    boolean hasNoItems();
}