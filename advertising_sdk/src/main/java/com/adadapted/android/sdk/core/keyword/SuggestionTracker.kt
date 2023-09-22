package com.adadapted.android.sdk.core.keyword

import kotlin.jvm.Synchronized

object SuggestionTracker {
    private val items: MutableMap<String, String> = HashMap()
    private val replacements: MutableMap<String, String> = HashMap()

    @Synchronized
    fun suggestionMatched(
        searchId: String,
        termId: String,
        term: String,
        replacement: String,
        userInput: String
    ) {
        val lcTerm = convertToLowerCase(term)
        val lcUserInput = convertToLowerCase(userInput)
        val lcReplacement = convertToLowerCase(replacement)
        items[lcTerm] = lcUserInput
        replacements[lcReplacement] = lcTerm
        InterceptClient.getInstance().trackMatched(searchId, termId, lcTerm, lcUserInput)
    }

    @Synchronized
    fun suggestionPresented(searchId: String, termId: String, replacement: String) {
        val lcReplacement = convertToLowerCase(replacement)
        if (replacements.containsKey(lcReplacement)) {
            val term = replacements[lcReplacement]
            val userInput = items[term]
            InterceptClient.getInstance()
                .trackPresented(searchId, termId, term ?: "", userInput ?: "")
        }
    }

    @Synchronized
    fun suggestionSelected(searchId: String, termId: String, replacement: String) {
        val lcReplacement = convertToLowerCase(replacement)
        if (replacements.containsKey(lcReplacement)) {
            val term = replacements[lcReplacement]
            val userInput = items[term]
            InterceptClient.getInstance()
                .trackSelected(searchId, termId, term ?: "", userInput ?: "")
        }
    }

    @Synchronized
    fun suggestionNotMatched(searchId: String, userInput: String) {
        val lcUserInput = convertToLowerCase(userInput)
        InterceptClient.getInstance().trackNotMatched(searchId, lcUserInput)
    }

    private fun convertToLowerCase(str: String?): String {
        return str?.lowercase() ?: ""
    }
}