package it.bastoner.taboom.database;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class CardWithTags {

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

        for (Tag t: getTagList()) {
            s.append(t);
        }

        s.append("]");

        return s.toString();
    }
}
