package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.adadapted.android.sdk.ui.model.Suggestion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AutoCompleteAdapter.class.getName();

    private final KeywordInterceptMatcher matcher;
    private final List<String> allItems;

    public AutoCompleteAdapter(final Context context,
                               final int resource,
                               final List<String> items) {
        super(context.getApplicationContext(), resource, items);

        matcher = new KeywordInterceptMatcher();
        allItems = new ArrayList<>(items);
    }

    public void suggestionSelected(final String suggestion) {
        matcher.suggestionSelected(suggestion);
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
            final Set<String> items = new HashSet<>();

            if(constraint != null) {
                final Set<Suggestion> suggestions = matcher.match(constraint);
                for(final Suggestion suggestion : suggestions) {
                    items.add(suggestion.getReplacement());
                    matcher.suggestionPresented(suggestion.getReplacement());
                }

                for(final String item : allItems) {
                    if (item != null && item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        items.add(item);
                    }
                }
            }

            filterResults.values = new ArrayList<>(items);
            filterResults.count = items.size();

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
