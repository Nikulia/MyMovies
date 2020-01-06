package com.release.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.release.mymovies.adapters.MovieAdapter;
import com.release.mymovies.data.MainViewModel;
import com.release.mymovies.data.Movie;
import com.release.mymovies.utils.JsonUtils;
import com.release.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    public static final String INTENT_MOVIE_ID = "movieId";
    public static final String INTENT_ACTIVITY_FROM = "activity";
    public static final int FROM_MAIN_ACTIVITY = 0;
    public static final String BUNDLE_ATTRIBUTE_URL = "url";
    public static final String BUNDLE_RECYCLER_VIEW_POSITION = "recyclerView's position";
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private Switch switchSort;
    private TextView textViewRating;
    private TextView textViewPopularity;
    private MainViewModel viewModel;
    private ProgressBar progressBarDownload;
    private int recyclerViewSavedPosition = 0;

    private static final int LOADER_ID = 122;
    private LoaderManager loaderManager;

    private static int page = 1;
    private static int methodOfSort;
    private static boolean isLoading = false;
    private static String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lang = Locale.getDefault().getLanguage();
        loaderManager = LoaderManager.getInstance(this);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        switchSort = findViewById(R.id.switchSort);
        textViewRating = findViewById(R.id.textViewTopRated);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        recyclerViewPosters = findViewById(R.id.resyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getCountOfColumns(this)));
        progressBarDownload = findViewById(R.id.progressBarDownload);
        if (savedInstanceState != null) {
            recyclerViewSavedPosition = savedInstanceState.getInt(BUNDLE_RECYCLER_VIEW_POSITION);
        }
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
                setMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(INTENT_MOVIE_ID, movie.getId());
                intent.putExtra(INTENT_ACTIVITY_FROM, FROM_MAIN_ACTIVITY);
                startActivity(intent);
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading)
                    downloadData(page, methodOfSort, lang);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_RECYCLER_VIEW_POSITION,
                ((LinearLayoutManager) recyclerViewPosters.getLayoutManager()).findLastVisibleItemPosition());
    }

    public static int getCountOfColumns(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / 185 > 2 ? width / 185 : 2;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemMain) {
            Intent intentToMain = new Intent(this, MainActivity.class);
            startActivity(intentToMain);
        } else if (id == R.id.itemFavorite) {
            Intent intentToFavorite = new Intent(this, FavoriteActivity.class);
            startActivity(intentToFavorite);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            methodOfSort = NetworkUtils.RATING;
            textViewPopularity.setTextColor(getResources().getColor(R.color.white));
            textViewRating.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewRating.setTextColor(getResources().getColor(R.color.white));
        }
        downloadData(page, methodOfSort, lang);
    }

    public void onClickPopularity(View view) {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickTopRated(View view) {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    public void downloadData(int page, int methodOfSort, String lang) {
        URL url = NetworkUtils.getUrlToJson(page, methodOfSort, lang);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ATTRIBUTE_URL, url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.JsonLoader jsonLoader = new NetworkUtils.JsonLoader(this, args);
        jsonLoader.setOnLoadingStartListener(new NetworkUtils.JsonLoader.OnLoadingStartListener() {
            @Override
            public void onLoadingStart() {
                isLoading = true;
                progressBarDownload.setVisibility(View.VISIBLE);
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        List<Movie> movies = JsonUtils.getMoviesFromJsonObject(data);
        if (movies != null && !movies.isEmpty()) {
            if (page == 1) {
                viewModel.deleteAllMovies();
                movieAdapter.clear();
                recyclerViewPosters.scrollToPosition(0);
            }
            for (Movie movie : movies)
                viewModel.insertMovie(movie);
            movieAdapter.addMoviesToList(movies);
            page++;
        } else {
            viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(List<Movie> moviesFromLiveData) {
                    if (moviesFromLiveData != null && !moviesFromLiveData.isEmpty()) {
                        movieAdapter.setMovies(moviesFromLiveData);
                    }
                }
            });
        }
        isLoading = false;
        progressBarDownload.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);
        if (recyclerViewSavedPosition > movieAdapter.getMovies().size() - 1) {
            downloadData(page, methodOfSort, lang);
        }
        if (recyclerViewSavedPosition > 0 &&
                recyclerViewSavedPosition <= movieAdapter.getMovies().size() - 1) {
            recyclerViewPosters.getLayoutManager().scrollToPosition(recyclerViewSavedPosition);
            recyclerViewSavedPosition = 0;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}
