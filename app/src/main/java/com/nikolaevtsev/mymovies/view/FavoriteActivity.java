package com.nikolaevtsev.mymovies.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikolaevtsev.mymovies.R;
import com.nikolaevtsev.mymovies.model.pojo.FavoriteMovie;
import com.nikolaevtsev.mymovies.model.pojo.Movie;
import com.nikolaevtsev.mymovies.view.adapters.MovieAdapter;
import com.nikolaevtsev.mymovies.viewmodel.ListFavoriteMovieViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    public static final int FROM_FAVORITE_ACTIVITY = 1;
    private RecyclerView recyclerViewFavoriteMovies;
    private MovieAdapter adapter;
    private ListFavoriteMovieViewModel favoriteMovieViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        recyclerViewFavoriteMovies = findViewById(R.id.recyclerViewFavoriteMovies);
        recyclerViewFavoriteMovies.setLayoutManager(new GridLayoutManager(this, MainActivity.getCountOfColumns(this)));
        favoriteMovieViewModel = ViewModelProviders.of(this).get(ListFavoriteMovieViewModel.class);
        LiveData<List<FavoriteMovie>> favoriteMovies = favoriteMovieViewModel.getFavoriteMovies();
        favoriteMovies.observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(List<FavoriteMovie> favoriteMovies) {
                if (favoriteMovies != null) {
                    List<Movie> movies = new ArrayList<>();
                    movies.addAll(favoriteMovies);
                    adapter.setMovies(movies);
                }
            }
        });
        adapter = new MovieAdapter();
        recyclerViewFavoriteMovies.setAdapter(adapter);
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
                intent.putExtra(MainActivity.INTENT_MOVIE_ID, movie.getMovieId());
                intent.putExtra(MainActivity.INTENT_ACTIVITY_FROM, FROM_FAVORITE_ACTIVITY);
                startActivity(intent);
            }
        });

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

}
