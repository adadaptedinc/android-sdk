package com.adadapted.android.sdk.core.atl;

import java.util.List;

public interface AddToListContent {
    final class Sources {
        public static final String IN_APP = "in_app";
        public static final String OUT_OF_APP = "out_of_app";
    }

    void acknowledge();
    void itemAcknowledge(AddToListItem item);
    void failed(String message);
    void itemFailed(AddToListItem item, String message);
    String getSource();
    List<AddToListItem> getItems();
    boolean hasNoItems();
}
