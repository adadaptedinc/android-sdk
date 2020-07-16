package com.adadapted.android.sdk.ui.adapter

import com.adadapted.android.sdk.core.intercept.InterceptClient
import java.util.Locale
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

object SuggestionTracker {
    private val matcherLock: Lock = ReentrantLock()
    private val items: MutableMap<String, String> = HashMap()
    private val replacements: MutableMap<String, String> = HashMap()

    @Synchronized
    fun suggestionMatched(
            searchId: String,
            termId: String,
            term: String,
            replacement: String,
            userInput: String) {
        matcherLock.lock()
        try {
            val lcTerm = convertToLowerCase(term)
            val lcUserInput = convertToLowerCase(userInput)
            val lcReplacement = convertToLowerCase(replacement)
            items[lcTerm] = lcUserInput
            replacements[lcReplacement] = lcTerm
            InterceptClient.getInstance().trackMatched(searchId, termId, lcTerm, lcUserInput)
        } finally {
            matcherLock.unlock()
        }
    }

    @Synchronized
    fun suggestionPresented(searchId: String, termId: String, replacement: String) {
        val lcReplacement = convertToLowerCase(replacement)
        matcherLock.lock()
        try {
            if (replacements.containsKey(lcReplacement)) {
                val term = replacements[lcReplacement]
                val userInput = items[term]
                InterceptClient.getInstance().trackPresented(searchId, termId, term ?: "", userInput ?: "")
            }
        } finally {
            matcherLock.unlock()
        }
    }

    @Synchronized
    fun suggestionSelected(searchId: String, termId: String, replacement: String) {
        val lcReplacement = convertToLowerCase(replacement)
        matcherLock.lock()
        try {
            if (replacements.containsKey(lcReplacement)) {
                val term = replacements[lcReplacement]
                val userInput = items[term]
                InterceptClient.getInstance().trackSelected(searchId, termId, term ?: "", userInput ?: "")
            }
        } finally {
            matcherLock.unlock()
        }
    }

    @Synchronized
    fun suggestionNotMatched(searchId: String, userInput: String) {
        matcherLock.lock()
        try {
            val lcUserInput = convertToLowerCase(userInput)
            InterceptClient.getInstance().trackNotMatched(searchId, lcUserInput)
        } finally {
            matcherLock.unlock()
        }
    }

    private fun convertToLowerCase(str: String?): String {
        return str?.toLowerCase(Locale.ROOT) ?: ""
    }
}