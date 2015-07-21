package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.adadapted.android.sdk.ui.model.SuggestionPayload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chrisweeden on 6/18/15.
 */
@SuppressWarnings("ALL")
public class AaAutoCompleteAdapter extends ArrayAdapter<String> {
    private static final String TAG = AaAutoCompleteAdapter.class.getName();

    private final AaKeywordInterceptMatcher matcher;
    private final List<String> allItems;

    public AaAutoCompleteAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);

        matcher = new AaKeywordInterceptMatcher(context);
        allItems = new ArrayList<>(items);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            Set<String> suggestions = new HashSet<>();

            if(constraint != null) {
                SuggestionPayload suggestionPayload = matcher.match(constraint);
                suggestions.addAll(suggestionPayload.getSuggestions());

                for(String item : allItems) {
                    if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
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
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            List<String> filteredList = (ArrayList<String>) results.values;
            clear();

            if(results.count > 0) {
                for (String s : filteredList) {
                    add(s);
                }
            }

            notifyDataSetChanged();
        }
    };
}
