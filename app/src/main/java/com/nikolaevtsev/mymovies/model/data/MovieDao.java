package com.nikolaevtsev.mymovies.model.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.nikolaevtsev.mymovies.model.pojo.FavoriteMovie;
import com.nikolaevtsev.mymovies.model.pojo.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM favorite_movies")
    LiveData<List<FavoriteMovie>> getAllFavoriteMovies();

    @Query("SELECT * FROM movies WHERE movieId == :movieId")
    Movie getMovieById(int movieId);

    @Query("SELECT * FROM favorite_movies WHERE movieId == :movieId")
    FavoriteMovie getFavoriteMovieById(int movieId);

    @Query("DELETE FROM movies")
    void deleteAllMovies();

    @Insert
    void insertMovies(List<Movie> movies);

    @Insert
    void insertFavoriteMovie(FavoriteMovie favoriteMovie);

    @Delete
    void deleteFavoriteMovie(FavoriteMovie favoriteMovie);
}
