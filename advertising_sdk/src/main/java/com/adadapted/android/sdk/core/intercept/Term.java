package com.adadapted.android.sdk.core.intercept;

public class Term {
    private final String termId;
    private final String term;
    private final String replacement;
    private final String icon;
    private final String tagLine;
    private final int priority;

    public Term(final String termId,
                final String term,
                final String replacement,
                final String icon,
                final String tagLine,
                final int priority) {
        this.termId = termId;
        this.term = term;
        this.replacement = replacement;
        this.icon = icon;
        this.tagLine = tagLine;
        this.priority = priority;
    }

    public String getTermId() {
        return termId;
    }

    public String getTerm() {
        return term;
    }

    public String getReplacement() {
        return replacement;
    }

    public String getIcon() {
        return icon;
    }

    public String getTagLine() {
        return tagLine;
    }

    public int getPriority() {
        return priority;
    }

    public int compareTo(Term a2) {
        if(this.getPriority() == a2.getPriority()) {
            return this.getTerm().compareTo(a2.getTerm());
        } else if(this.getPriority() < a2.getPriority()) {
            return -1;
        }

        return 1;
    }

    @Override
    public String toString() {
        return "Term{" +
                "termId='" + termId + '\'' +
                ", term='" + term + '\'' +
                ", replacement='" + replacement + '\'' +
                ", icon='" + icon + '\'' +
                ", tagLine='" + tagLine + '\'' +
                ", priority=" + priority +
                '}';
    }
}
