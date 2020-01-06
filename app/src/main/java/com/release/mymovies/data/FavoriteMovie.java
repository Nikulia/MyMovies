package com.release.mymovies.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie extends Movie {
    public FavoriteMovie(int uniqueId, int id, int voteCount, String title, String originalTitle, String overview,
                         String releaseDate, double rating, String smallPosterPath,
                         String bigPosterPath, String backdropPath) {
        super(uniqueId, id, voteCount, title, originalTitle, overview, releaseDate, rating,
                smallPosterPath, bigPosterPath, backdropPath);
    }

    @Ignore
    public FavoriteMovie(Movie movie) {
        super(movie.getUniqueId(), movie.getId(), movie.getVoteCount(), movie.getTitle(), movie.getOriginalTitle(),
                movie.getOverview(), movie.getReleaseDate(), movie.getRating(), movie.getSmallPosterPath(),
                movie.getBigPosterPath(), movie.getBackdropPath());
    }
}
