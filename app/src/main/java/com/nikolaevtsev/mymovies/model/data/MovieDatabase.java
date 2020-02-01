package com.nikolaevtsev.mymovies.model.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nikolaevtsev.mymovies.model.pojo.FavoriteMovie;
import com.nikolaevtsev.mymovies.model.pojo.Movie;

@Database(entities = {Movie.class, FavoriteMovie.class}, version = 9, exportSchema = false)
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
