package com.release.mymovies.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class, FavoriteMovie.class}, version = 7, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    private static MovieDatabase database;
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movies.db";

    public static MovieDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (database == null)
                database = Room.databaseBuilder(context, MovieDatabase.class, DATABASE_NAME).
                        fallbackToDestructiveMigration().build();
            return database;
        }
    }

    public abstract MovieDao movieDao();
}
