package com.nikolaevtsev.mymovies.model.pojo;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie extends Movie {
    public FavoriteMovie(int uniqueId, double popularity, int voteCount, boolean video, String posterPath,
                         int movieId, boolean adult, String backdropPath, String originalLanguage,
                         String originalTitle, String title, double voteAverage, String overview,
                         String releaseDate) {
        super(uniqueId, popularity, voteCount, video, posterPath,
                movieId, adult, backdropPath, originalLanguage,
                originalTitle, title, voteAverage, overview,
                releaseDate);
    }

    @Ignore
    public FavoriteMovie(Movie movie) {
        super(movie.getUniqueId(), movie.getPopularity(), movie.getVoteCount(), movie.isVideo(),
                movie.getPosterPath(), movie.getMovieId(), movie.isAdult(), movie.getBackdropPath(),
                movie.getOriginalLanguage(), movie.getOriginalTitle(), movie.getTitle(),
                movie.getVoteAverage(), movie.getOverview(), movie.getReleaseDate());
    }
}
