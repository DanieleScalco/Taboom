package it.bastoner.taboom.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class CardWithTags implements Parcelable {

    @Embedded
    private Card card;

    @Relation(parentColumn = "idCard",
                entityColumn = "idTag",
                associateBy = @Junction(CardTagCrossRef.class))
    private List<Tag> tagList;

    public CardWithTags() {

    }

    @Ignore
    public CardWithTags(Card card, List<Tag> tagList) {
        this.card = card;
        this.tagList = tagList;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public String toString() {

        StringBuilder s = new StringBuilder(getCard().toString());
        s.append(", TAG[");

        if (getTagList() != null)
            for (Tag t: getTagList()) {
                s.append(t);
            }

        s.append("]");

        return s.toString();
    }

    // TODO empty
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    @Ignore
    protected CardWithTags(Parcel in) {
    }

    public static final Creator<CardWithTags> CREATOR = new Creator<CardWithTags>() {
        @Override
        public CardWithTags createFromParcel(Parcel in) {
            return new CardWithTags(in);
        }

        @Override
        public CardWithTags[] newArray(int size) {
            return new CardWithTags[size];
        }
    };
}
