package com.release.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.release.mymovies.adapters.ReviewAdapter;
import com.release.mymovies.adapters.TrailerAdapter;
import com.release.mymovies.data.FavoriteMovie;
import com.release.mymovies.data.MainViewModel;
import com.release.mymovies.data.Movie;
import com.release.mymovies.data.Review;
import com.release.mymovies.data.Trailer;
import com.release.mymovies.utils.JsonUtils;
import com.release.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
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
    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private ScrollView scrollViewMovieInfo;

    private Movie movie;
    private MainViewModel viewModel;
    private int movieId;
    private static String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        lang = Locale.getDefault().getLanguage();
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewAddToFavorite = findViewById(R.id.imageViewAddToFavorite);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        scrollViewMovieInfo = findViewById(R.id.scrollViewMovieInfo);
        Intent intent = getIntent();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        movieId = -1;
        if (intent != null && intent.hasExtra(MainActivity.INTENT_MOVIE_ID)
                && intent.hasExtra(MainActivity.INTENT_ACTIVITY_FROM)) {
            movieId = intent.getIntExtra(MainActivity.INTENT_MOVIE_ID, -1);
            int activityFrom = intent.getIntExtra(MainActivity.INTENT_ACTIVITY_FROM, -1);
            if (activityFrom == MainActivity.FROM_MAIN_ACTIVITY)
                movie = viewModel.getMovieById(movieId);
            else if (activityFrom == FavoriteActivity.FROM_FAVORITE_ACTIVITY)
                movie = viewModel.getFavoriteMovieById(movieId);
            else
                throw new IllegalArgumentException("Illegal argument of intent from activity");
        } else
            finish();
        setMovieToViews();
        setFavorite();
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });
        ArrayList<Trailer> trailers =
                JsonUtils.getTrailersFromJsonObject(NetworkUtils.getJsonObjectVideo(movie.getId(), lang));
        ArrayList<Review> reviews =
                JsonUtils.getReviewsFromJsonObject(NetworkUtils.getJsonObjectReview(movie.getId(), lang));
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);
        scrollViewMovieInfo.smoothScrollTo(0, 0);
    }

    private void setMovieToViews() {
        Picasso.get().load(movie.getBigPosterPath()).placeholder(R.drawable.placeholder_big_poster)
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
        textViewRating.setText(String.valueOf(movie.getRating()));
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());
    }

    private static class DownloadBackgroundBitmap extends AsyncTask<Movie, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Movie... movies) {
            Bitmap bitmap = null;
            if (movies != null && movies.length > 0) {
                try {
                    bitmap = Picasso.get().load(movies[0].getBackdropPath()).
                            transform(new ColorFilterTransformation(R.color.background_color)).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }
    }

    public void onClickAddToFavorite(View view) {
        FavoriteMovie favoriteMovie = viewModel.getFavoriteMovieById(movieId);
        if (favoriteMovie == null) {
            viewModel.insertFavoriteMovie(new FavoriteMovie(movie));
            Toast.makeText(this, getString(R.string.add_to_favorite), Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavoriteMovie(favoriteMovie);
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
        FavoriteMovie favoriteMovie = viewModel.getFavoriteMovieById(movieId);
        if (favoriteMovie == null)
            imageViewAddToFavorite.setImageResource(R.drawable.favourite_add_to);
        else
            imageViewAddToFavorite.setImageResource(R.drawable.favourite_remove);
    }
}
