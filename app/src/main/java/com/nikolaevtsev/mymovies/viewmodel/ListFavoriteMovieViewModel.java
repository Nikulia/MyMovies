package com.nikolaevtsev.mymovies.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nikolaevtsev.mymovies.model.data.MovieDatabase;
import com.nikolaevtsev.mymovies.model.pojo.FavoriteMovie;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListFavoriteMovieViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    private LiveData<List<FavoriteMovie>> favoriteMovies;

    public ListFavoriteMovieViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(application);
        favoriteMovies = database.movieDao().getAllFavoriteMovies();
    }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void insertFavoriteMovie(FavoriteMovie favoriteMovie) {
        new ListFavoriteMovieViewModel.InsertFavoriteMovieTask().execute(favoriteMovie);
    }

    public void deleteFavoriteMovie(FavoriteMovie favoriteMovie) {
        new ListFavoriteMovieViewModel.DeleteFavoriteMovieTask().execute(favoriteMovie);
    }

    public FavoriteMovie getFavoriteMovieById(int id) {
        FavoriteMovie movie = null;
        try {
            movie = new ListFavoriteMovieViewModel.GetFavoriteMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return movie;
    }

    private static class InsertFavoriteMovieTask extends AsyncTask<FavoriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavoriteMovie... movies) {
            if (movies != null && movies.length > 0)
                database.movieDao().insertFavoriteMovie(movies[0]);
            return null;
        }
    }

    private static class DeleteFavoriteMovieTask extends AsyncTask<FavoriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavoriteMovie... movies) {
            if (movies != null && movies.length > 0)
                database.movieDao().deleteFavoriteMovie(movies[0]);
            return null;
        }
    }

    private static class GetFavoriteMovieTask extends AsyncTask<Integer, Void, FavoriteMovie> {
        @Override
        protected FavoriteMovie doInBackground(Integer... integers) {
            FavoriteMovie movie = null;
            if (integers != null && integers.length > 0)
                movie = database.movieDao().getFavoriteMovieById(integers[0]);
            return movie;
        }
    }
}
