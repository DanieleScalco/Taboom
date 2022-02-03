package it.bastoner.taboom.database;

import androidx.room.Entity;

@Entity(primaryKeys = {"idCard", "idTag"})
public class CardTagCrossRef {

    public long idCard;
    public long idTag;
}
