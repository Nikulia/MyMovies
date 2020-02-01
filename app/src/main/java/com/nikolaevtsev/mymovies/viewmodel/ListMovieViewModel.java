package com.nikolaevtsev.mymovies.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nikolaevtsev.mymovies.model.api.ApiFactoryMovie;
import com.nikolaevtsev.mymovies.model.api.ApiServiceMovie;
import com.nikolaevtsev.mymovies.model.data.MovieDatabase;
import com.nikolaevtsev.mymovies.model.pojo.Movie;
import com.nikolaevtsev.mymovies.model.pojo.ResponseMovie;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ListMovieViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    private LiveData<List<Movie>> movies;
    private CompositeDisposable compositeDisposable;
    private String lang;
    private int page;
    private String methodOfSort;

    private static final String API_KEY = "cd13d25e707fedec6df80ea235a078fb";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_RATING = "vote_average.desc";
    private static final int MIN_VOTE_COUNT_VALUE = 1000;

    public ListMovieViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(application.getApplicationContext());
        movies = database.movieDao().getAllMovies();
        lang = Locale.getDefault().getLanguage();
        compositeDisposable = new CompositeDisposable();
    }

    public void startLoadDataPopularity() {
        page = 1;
        methodOfSort = SORT_BY_POPULARITY;
        loadData();
        Log.i("StartLoadPopularity", "6");
    }

    public void startLoadDataRating() {
        page = 1;
        methodOfSort = SORT_BY_RATING;
        loadData();
        Log.i("StartLoadRate", "5");
    }

    public void continueLoadData() {
        loadData();
    }

    private void loadData() {
        Log.i("loadData", "4");
        ApiFactoryMovie apiFactoryMovie = ApiFactoryMovie.getInstance();
        ApiServiceMovie apiServiceMovie = apiFactoryMovie.getApiService();
        Disposable disposable = apiServiceMovie.getMovies(API_KEY, lang, page, methodOfSort,
                MIN_VOTE_COUNT_VALUE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseMovie>() {
                    @Override
                    public void accept(ResponseMovie responseMovie) throws Exception {
                        if (page == 1) {
                            deleteAllMovies();
                        }
                        Log.i("page", Integer.toString(page));
                        Log.i("size of movies", Integer.toString(movies.getValue().size()));
                        //if (page > 1 || movies.getValue().size() == 0) {
                        insertMovies(responseMovie.getMovies());
                        page++;
                        //}
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("Thrawable", "error");

                    }
                });
        compositeDisposable.add(disposable);
    }


    public Movie getMovieById(int id) {
        Movie movie = null;
        try {
            movie = new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return movie;
    }

    private void deleteAllMovies() {
        new DeleteAllMoviesTask().execute();
    }


    @SuppressWarnings("unchecked")
    private void insertMovies(List<Movie> movies) {
        new InsertMoviesTask().execute(movies);
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }


    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected Movie doInBackground(Integer... integers) {
            Movie movie = null;
            if (integers != null && integers.length > 0)
                movie = database.movieDao().getMovieById(integers[0]);
            return movie;
        }
    }

    private static class DeleteAllMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.movieDao().deleteAllMovies();
            return null;
        }
    }

    private static class InsertMoviesTask extends AsyncTask<List<Movie>, Void, Void> {
        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Movie>... lists) {
            if (lists != null && lists.length > 0)
                database.movieDao().insertMovies(lists[0]);
            return null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null)
            compositeDisposable.dispose();
    }
}
