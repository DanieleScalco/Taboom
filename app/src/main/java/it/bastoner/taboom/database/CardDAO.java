package it.bastoner.taboom.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardDAO {

    // Insert method can return void/long/long[] (depends if
    // a single object is inserted or a list)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long insertCard(Card card);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long insertTag(Tag tag);

    // It will return rowId
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long insertCardWithTags(CardTagCrossRef cardTagCrossRef);

    // If called on an item not present in the DB it won't do anything
    // @Update/@Delete return type void or int (number of raws modified/deleted)
    @Update
    public int updateCard(Card card);

    @Update
    public int updateTag(Tag tag);

    @Delete
    public int deleteCard(Card card);

    @Delete
    public int deleteTag(Tag tag);

    @Delete
    public int deleteCardWithTags(CardTagCrossRef cardTagCrossRef);


    // With a query method you can also perform complex inserts/updates/deletes
    // Transaction needed for relational classes
    @Transaction
    @Query("SELECT * FROM Card ORDER BY title")
    LiveData<List<CardWithTags>> getAllCards();

    @Query("SELECT * FROM Tag ORDER BY tag")
    LiveData<List<Tag>> getAllTags();

    @Query("SELECT * FROM Tag WHERE tag = :tag")
    Tag getTag(String tag);
}
