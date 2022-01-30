package it.bastoner.taboom.database;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CardEntity.class},
          version = 1)
public abstract class DatabaseTaboom extends RoomDatabase {

    public static final String DATABASE_NAME = "db_taboom-1";

    public abstract CardDAO cardDao();

    public static DatabaseTaboom db;

    // Singleton pattern
    public static DatabaseTaboom getDatabase(Context applicationContext) {
        if (db == null) {
            db = Room.databaseBuilder(applicationContext, DatabaseTaboom.class, DATABASE_NAME)
                    //.allowMainThreadQueries()
                    .build();
        }
        return db;
    }
}