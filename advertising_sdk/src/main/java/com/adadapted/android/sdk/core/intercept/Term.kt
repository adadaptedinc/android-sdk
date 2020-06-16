package com.adadapted.android.sdk.core.intercept

class Term(val termId: String,
           val term: String,
           val replacement: String,
           val icon: String,
           val tagLine: String,
           private val priority: Int) {

    operator fun compareTo(a2: Term): Int {
        if (priority == a2.priority) {
            return term.compareTo(a2.term)
        } else if (priority < a2.priority) {
            return -1
        }
        return 1
    }

    override fun toString(): String {
        return "Term{" +
                "termId='" + termId + '\'' +
                ", term='" + term + '\'' +
                ", replacement='" + replacement + '\'' +
                ", icon='" + icon + '\'' +
                ", tagLine='" + tagLine + '\'' +
                ", priority=" + priority +
                '}'
    }
}