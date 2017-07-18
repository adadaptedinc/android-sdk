package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.adadapted.android.sdk.ui.model.SuggestionPayload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AaAutoCompleteAdapter extends ArrayAdapter<String> {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AaAutoCompleteAdapter.class.getName();

    private final AaKeywordInterceptMatcher matcher;
    private final List<String> allItems;

    public AaAutoCompleteAdapter(final Context context,
                                 final int resource,
                                 final List<String> items) {
        super(context.getApplicationContext(), resource, items);

        matcher = new AaKeywordInterceptMatcher();
        allItems = new ArrayList<>(items);
    }

    public boolean suggestionSelected(final String suggestion) {
        return matcher.suggestionSelected(suggestion);
    }

    @Override
    @NonNull
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(final CharSequence constraint) {
            final FilterResults filterResults = new FilterResults();
            final Set<String> suggestions = new HashSet<>();

            if(constraint != null) {
                SuggestionPayload suggestionPayload = matcher.match(constraint);
                suggestions.addAll(suggestionPayload.getSuggestions());

                for(final String item : allItems) {
                    if (item != null && item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestionPayload.presented(item);
                        suggestions.add(item);
                    }
                }
            }

            filterResults.values = new ArrayList<>(suggestions);
            filterResults.count = suggestions.size();

            return filterResults;
        }

        @Override
        protected void publishResults(final CharSequence constraint,
                                      final Filter.FilterResults results) {
            if(results != null) {
                clear();

                if (results.count > 0) {
                    List<?> filteredList = (ArrayList<?>) results.values;

                    for (final Object o : filteredList) {
                        if(o instanceof String) {
                            final String s = (String) o;
                            add(s);
                        }
                    }
                }

                notifyDataSetChanged();
            }
        }
    };
}
