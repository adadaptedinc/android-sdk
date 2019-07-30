package com.adadapted.android.sdk.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.adadapted.android.sdk.core.intercept.Term;
import com.adadapted.android.sdk.ui.adapter.SuggestionTracker;

public class Suggestion implements Parcelable {
    public static final Creator<Suggestion> CREATOR = new Creator<Suggestion>() {
        @Override
        public Suggestion createFromParcel(Parcel in) {
            return new Suggestion(in);
        }

        @Override
        public Suggestion[] newArray(int size) {
            return new Suggestion[size];
        }
    };

    private final String searchId;

    private final String termId;
    private final String name;
    private final String icon;
    private final String tagLine;

    private boolean presented;
    private boolean selected;

    public Suggestion(final String searchId, final Term term) {
        this.searchId = searchId;

        this.termId = term.getTermId();
        this.name = term.getReplacement();
        this.icon = term.getIcon();
        this.tagLine = term.getTagLine();

        presented = false;
        selected = false;
    }

    protected Suggestion(Parcel in) {
        searchId = in.readString();
        termId = in.readString();
        name = in.readString();
        icon = in.readString();
        tagLine = in.readString();
        presented = in.readByte() != 0;
        selected = in.readByte() != 0;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void presented() {
        if(!presented) {
            presented = true;
            SuggestionTracker.suggestionPresented(searchId, termId, getName());
        }
    }

    public void selected() {
        if(!selected) {
            selected = true;
            SuggestionTracker.suggestionSelected(searchId, termId, getName());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(searchId);
        parcel.writeString(termId);
        parcel.writeString(name);
        parcel.writeString(icon);
        parcel.writeString(tagLine);
        parcel.writeByte((byte) (presented ? 1 : 0));
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
