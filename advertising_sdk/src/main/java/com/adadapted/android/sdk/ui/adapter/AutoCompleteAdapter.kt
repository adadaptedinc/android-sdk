package com.adadapted.android.sdk.ui.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.adadapted.android.sdk.core.intercept.KeywordInterceptMatcher
import com.adadapted.android.sdk.ui.model.Suggestion

class AutoCompleteAdapter(context: Context, resource: Int, items: List<String>) : ArrayAdapter<String>(context.applicationContext, resource, items) {
    private val allItems: List<String> = ArrayList(items)
    private val currentSuggestions: MutableSet<Suggestion> = HashSet()

    fun suggestionSelected(name: String) {
        currentSuggestions.firstOrNull { s -> s.name == name }?.selected()
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    private val mFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResults = FilterResults()
            val listItems: MutableList<String> = ArrayList()
            currentSuggestions.clear()
            currentSuggestions.addAll(KeywordInterceptMatcher.match(constraint))

            for (suggestion in currentSuggestions) {
                listItems.add(suggestion.name)
                suggestion.presented()
            }

            val input = constraint.toString()
            for (item in allItems) {
                if (item.startsWith(input, ignoreCase = true)) {
                    listItems.add(item)
                } else if (item.contains(input, ignoreCase = true)) {
                    listItems.add(item)
                }
            }
            filterResults.values = ArrayList(listItems)
            filterResults.count = listItems.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear()
            if (results != null) {
                if (results.count > 0) {
                    val filteredList: List<*> = results.values as ArrayList<*>
                    filteredList.forEach {
                        if (it is String) {
                            add(it)
                        }
                    }.also { notifyDataSetChanged() }
                }
            }
        }
    }
}