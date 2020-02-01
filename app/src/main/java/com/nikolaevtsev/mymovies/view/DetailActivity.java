package com.nikolaevtsev.mymovies.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikolaevtsev.mymovies.R;
import com.nikolaevtsev.mymovies.model.pojo.FavoriteMovie;
import com.nikolaevtsev.mymovies.model.pojo.Movie;
import com.nikolaevtsev.mymovies.model.pojo.Review;
import com.nikolaevtsev.mymovies.model.pojo.Trailer;
import com.nikolaevtsev.mymovies.view.adapters.ReviewAdapter;
import com.nikolaevtsev.mymovies.view.adapters.TrailerAdapter;
import com.nikolaevtsev.mymovies.viewmodel.ListFavoriteMovieViewModel;
import com.nikolaevtsev.mymovies.viewmodel.ListMovieViewModel;
import com.nikolaevtsev.mymovies.viewmodel.ListReviewViewModel;
import com.nikolaevtsev.mymovies.viewmodel.ListTrailerViewModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.picasso.transformations.ColorFilterTransformation;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private ImageView imageViewAddToFavorite;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewReleaseDate;
    private TextView textViewRating;
    private TextView textViewOverview;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private ScrollView scrollViewMovieInfo;

    private Movie movie;
    private ListMovieViewModel listMovieViewModel;
    private ListFavoriteMovieViewModel listFavoriteMovieViewModel;
    private int movieId;

    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    private static final String BIG_IMAGE_SIZE = "w780";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewAddToFavorite = findViewById(R.id.imageViewAddToFavorite);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        scrollViewMovieInfo = findViewById(R.id.scrollViewMovieInfo);
        Intent intent = getIntent();
        listMovieViewModel = ViewModelProviders.of(this).get(ListMovieViewModel.class);
        listFavoriteMovieViewModel = ViewModelProviders.of(this)
                .get(ListFavoriteMovieViewModel.class);
        movieId = -1;
        if (intent != null && intent.hasExtra(MainActivity.INTENT_MOVIE_ID)
                && intent.hasExtra(MainActivity.INTENT_ACTIVITY_FROM)) {
            movieId = intent.getIntExtra(MainActivity.INTENT_MOVIE_ID, -1);
            int activityFrom = intent.getIntExtra(MainActivity.INTENT_ACTIVITY_FROM, -1);
            if (activityFrom == MainActivity.FROM_MAIN_ACTIVITY)
                movie = listMovieViewModel.getMovieById(movieId);
            else if (activityFrom == FavoriteActivity.FROM_FAVORITE_ACTIVITY)
                movie = listFavoriteMovieViewModel.getFavoriteMovieById(movieId);
            else
                throw new IllegalArgumentException("Illegal argument of intent from activity");
        } else
            finish();
        setMovieToViews();
        setFavorite();
        RecyclerView recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        RecyclerView recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);
        trailerAdapter.setTrailers(new ArrayList<Trailer>());
        reviewAdapter.setReviews(new ArrayList<Review>());
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });
        ListTrailerViewModel listTrailerViewModel = ViewModelProviders.of(this).get(ListTrailerViewModel.class);
        listTrailerViewModel.getLiveDataTrailers().observe(this, new Observer<List<Trailer>>() {
            @Override
            public void onChanged(List<Trailer> trailers) {
                trailerAdapter.setTrailers(trailers);
            }
        });
        ListReviewViewModel listReviewViewModel = ViewModelProviders.of(this).get(ListReviewViewModel.class);
        listReviewViewModel.getLiveDataReviews().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                reviewAdapter.setReviews(reviews);
            }
        });
        listReviewViewModel.loadData(movieId);
        listTrailerViewModel.loadData(movieId);
        scrollViewMovieInfo.smoothScrollTo(0, 0);
    }

    private void setMovieToViews() {
        Picasso.get().load(BASE_IMAGE_URL + BIG_IMAGE_SIZE + movie.getPosterPath())
                .placeholder(R.drawable.placeholder_big_poster)
                .into(imageViewBigPoster);
        Bitmap bitmapBackground = null;
        try {
            bitmapBackground = new DownloadBackgroundBitmap().execute(movie).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (bitmapBackground != null)
            scrollViewMovieInfo.setBackground(new BitmapDrawable(getResources(), bitmapBackground));
        else
            scrollViewMovieInfo.setBackground(getResources().getDrawable(R.drawable.placeholder_background));
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewRating.setText(String.valueOf(movie.getVoteAverage()));
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());
    }

    private static class DownloadBackgroundBitmap extends AsyncTask<Movie, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Movie... movies) {
            Bitmap bitmap = null;
            if (movies != null && movies.length > 0) {
                try {
                    bitmap = Picasso.get().load(BASE_IMAGE_URL + BIG_IMAGE_SIZE +
                            movies[0].getBackdropPath())
                            .transform(new ColorFilterTransformation(R.color.background_color))
                            .get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }
    }

    public void onClickAddToFavorite(View view) {
        FavoriteMovie favoriteMovie = listFavoriteMovieViewModel.getFavoriteMovieById(movieId);
        if (favoriteMovie == null) {
            listFavoriteMovieViewModel.insertFavoriteMovie(new FavoriteMovie(movie));
            Toast.makeText(this, getString(R.string.add_to_favorite), Toast.LENGTH_SHORT).show();
        } else {
            listFavoriteMovieViewModel.deleteFavoriteMovie(favoriteMovie);
            Toast.makeText(this, getString(R.string.delete_from_favorite), Toast.LENGTH_SHORT).show();
        }
        setFavorite();
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

    private void setFavorite() {
        FavoriteMovie favoriteMovie = listFavoriteMovieViewModel.getFavoriteMovieById(movieId);
        if (favoriteMovie == null)
            imageViewAddToFavorite.setImageResource(R.drawable.favourite_add_to);
        else
            imageViewAddToFavorite.setImageResource(R.drawable.favourite_remove);
    }
}
