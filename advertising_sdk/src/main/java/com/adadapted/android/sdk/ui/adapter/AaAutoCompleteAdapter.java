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

/**
 * Created by chrisweeden on 6/18/15
 */
public class AaAutoCompleteAdapter extends ArrayAdapter<String> {
    private static final String LOGTAG = AaAutoCompleteAdapter.class.getName();

    private final AaKeywordInterceptMatcher mMatcher;
    private final List<String> mAllItems;

    public AaAutoCompleteAdapter(final Context context,
                                 final int resource,
                                 final List<String> items) {
        super(context.getApplicationContext(), resource, items);

        mMatcher = new AaKeywordInterceptMatcher();
        mAllItems = new ArrayList<>(items);
    }

    public boolean suggestionSelected(final String suggestion) {
        return mMatcher.suggestionSelected(suggestion);
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
                SuggestionPayload suggestionPayload = mMatcher.match(constraint);
                suggestions.addAll(suggestionPayload.getSuggestions());

                for(final String item : mAllItems) {
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
