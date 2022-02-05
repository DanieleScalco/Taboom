package it.bastoner.taboom.database;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(primaryKeys = {"idCard", "idTag"},
        foreignKeys = {@ForeignKey(entity = Card.class,
                                    parentColumns = "idCard",
                                    childColumns = "idCard",
                                    onDelete = CASCADE,
                                    onUpdate = CASCADE),
                       @ForeignKey(entity = Tag.class,
                                    parentColumns = "idTag",
                                    childColumns = "idTag",
                                    onDelete = CASCADE,
                                    onUpdate = CASCADE)})
public class CardTagCrossRef {

    public long idCard;
    public long idTag;

    public CardTagCrossRef() {

    }

    @Ignore
    public CardTagCrossRef(long idCard, long idTag) {
        this.idCard = idCard;
        this.idTag = idTag;
    }
}
