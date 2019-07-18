package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.adadapted.android.sdk.ui.model.Suggestion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AutoCompleteAdapter.class.getName();

    private final KeywordInterceptMatcher matcher;
    private final List<String> allItems;
    private final Set<Suggestion> currentSuggestions;

    public AutoCompleteAdapter(final Context context,
                               final int resource,
                               final List<String> items) {
        super(context.getApplicationContext(), resource, items);

        this.matcher = new KeywordInterceptMatcher();
        this.allItems = new ArrayList<>(items);
        this.currentSuggestions = new HashSet<>();
    }

    public void suggestionSelected(final String name) {
        for(Suggestion suggestion : currentSuggestions) {
            if (suggestion.getName().equals(name)) {
                suggestion.selected();
            }
        }
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
            final List<String> items = new ArrayList<>();

            if(constraint != null) {
                currentSuggestions.clear();
                currentSuggestions.addAll(matcher.match(constraint));

                for(final Suggestion suggestion : currentSuggestions) {
                    items.add(suggestion.getName());
                    suggestion.presented();
                }

                final String input = constraint.toString().toLowerCase(Locale.ROOT);
                for(final String item : allItems) {
                    if (item != null) {
                        if (item.toLowerCase(Locale.ROOT).startsWith(input)) {
                            items.add(item);
                        }
                        else if (item.toLowerCase(Locale.ROOT).contains(input)) {
                            items.add(item);
                        }
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
