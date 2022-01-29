package it.bastoner.taboom.database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCard(CardEntity card);

    // If called on an item not present in the DB it won't do anything
    @Update
    public void updateCard(CardEntity card);

    @Delete
    public void deleteCard(CardEntity card);

    // With a query method you can perform complex inserts/updates/deletes
    @Query("SELECT * FROM cardentity")
    LiveData<List<CardEntity>> getAll();
}
