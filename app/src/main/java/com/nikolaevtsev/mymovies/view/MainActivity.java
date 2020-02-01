package com.nikolaevtsev.mymovies.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikolaevtsev.mymovies.R;
import com.nikolaevtsev.mymovies.model.pojo.Movie;
import com.nikolaevtsev.mymovies.view.adapters.MovieAdapter;
import com.nikolaevtsev.mymovies.viewmodel.ListMovieViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String INTENT_MOVIE_ID = "movieId";
    public static final String INTENT_ACTIVITY_FROM = "activity";
    public static final int FROM_MAIN_ACTIVITY = 0;
    public static final String BUNDLE_RECYCLER_VIEW_STATE = "recyclerView's position";
    private static final String BUNDLE_IS_TOP_RATED = "method of sort is rate";
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private Switch switchSort;
    private TextView textViewRating;
    private TextView textViewPopularity;
    private ListMovieViewModel listMovieViewModel;
    private ProgressBar progressBarDownload;
    private Parcelable savedRecyclerViewState;
    private boolean savedSwitchPosition;
    private boolean isLoading = false;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listMovieViewModel = ViewModelProviders.of(this).get(ListMovieViewModel.class);
        switchSort = findViewById(R.id.switchSort);
        textViewRating = findViewById(R.id.textViewTopRated);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        recyclerViewPosters = findViewById(R.id.resyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getCountOfColumns(this)));
        progressBarDownload = findViewById(R.id.progressBarDownload);
        progressBarDownload.setVisibility(View.INVISIBLE);
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMethodOfSort(isChecked);
            }
        });
        if (savedInstanceState != null) {
            savedRecyclerViewState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_VIEW_STATE);
            savedSwitchPosition = savedInstanceState.getBoolean(BUNDLE_IS_TOP_RATED);
            switchSort.setChecked(savedSwitchPosition);
        } else {
            switchSort.setChecked(false);
        }
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(INTENT_MOVIE_ID, movie.getMovieId());
                intent.putExtra(INTENT_ACTIVITY_FROM, FROM_MAIN_ACTIVITY);
                startActivity(intent);
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading) {
                    listMovieViewModel.continueLoadData();
                    setLoading(true);
                }
            }
        });
        listMovieViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                setLoading(false);
                movieAdapter.setMovies(movies);
                if (savedRecyclerViewState != null) {
                    recyclerViewPosters.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState);
                    savedRecyclerViewState = null;
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_STATE,
                recyclerViewPosters.getLayoutManager().onSaveInstanceState());
        outState.putBoolean(BUNDLE_IS_TOP_RATED, switchSort.isChecked());
    }

    public static int getCountOfColumns(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / 185 > 2 ? width / 185 : 2;
    }

    //Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Listener for menu item
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
        movieAdapter.clear();
        if (isTopRated) {
            if (savedRecyclerViewState == null) {
                listMovieViewModel.startLoadDataRating();
                setLoading(true);
            }
            textViewPopularity.setTextColor(getResources().getColor(R.color.white));
            textViewRating.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            if (savedRecyclerViewState == null) {
                listMovieViewModel.startLoadDataPopularity();
                setLoading(true);
            }
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewRating.setTextColor(getResources().getColor(R.color.white));
        }
    }

    public void onClickPopularity(View view) {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickTopRated(View view) {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        if (isLoading && progressBarDownload.getVisibility() == View.INVISIBLE)
            progressBarDownload.setVisibility(View.VISIBLE);
        if (!isLoading && progressBarDownload.getVisibility() == View.VISIBLE)
            progressBarDownload.setVisibility(View.INVISIBLE);
    }
}

