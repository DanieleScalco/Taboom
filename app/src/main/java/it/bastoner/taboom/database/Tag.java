package it.bastoner.taboom.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"tag"}, unique = true)})
public class Tag {

    @PrimaryKey(autoGenerate = true)
    private long idTag;

    @ColumnInfo(name = "tag")
    private String tag;

    public Tag () {

    }

    public Tag(String tag) {
        this.tag = tag;
    }

    public Tag(long id, String tag) {
        this.idTag = id;
        this.tag = tag;
    }

    public long getIdTag() {
        return idTag;
    }

    public void setIdTag(long idTag) {
        this.idTag = idTag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    @NonNull
    @Override
    public String toString() {
        return getTag();
    }
}
