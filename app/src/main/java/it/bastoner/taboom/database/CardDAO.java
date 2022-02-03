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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertCard(Card card);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long insertTag(Tag tag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCardWithTags(CardTagCrossRef cardTagCrossRef);

    // If called on an item not present in the DB it won't do anything
    @Update
    public void updateCard(Card card);

    @Delete
    public void deleteCard(Card card);

    // With a query method you can also perform complex inserts/updates/deletes
    // Transaction needed for relational classes
    @Transaction
    @Query("SELECT * FROM Card")
    LiveData<List<CardWithTags>> getAllCards();

    @Query("SELECT * FROM Tag")
    LiveData<List<Tag>> getAllTags();

    @Query("SELECT * FROM Tag WHERE tag = :tag")
    Tag getTag(String tag);
}
