package it.bastoner.taboom.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Card.class, Tag.class, CardTagCrossRef.class},
          version = 1,
          exportSchema = false)
public abstract class DatabaseTaboom extends RoomDatabase {

    public static final String DATABASE_NAME = "db_taboom";

    public abstract CardDAO cardDao();

    public static DatabaseTaboom db;

    // To get db file in an Activity
    // File databaseFile = new File(getDatabasePath(DatabaseTaboom.DATABASE_NAME).getAbsolutePath());

    // Singleton pattern
    public static DatabaseTaboom getDatabase(Context applicationContext) {
        if (db == null) {
            db = Room.databaseBuilder(applicationContext, DatabaseTaboom.class, DATABASE_NAME)
                    .createFromAsset("db_taboom") // From assets folder
                    //.allowMainThreadQueries()
                    .build();
        }
        return db;
    }
}