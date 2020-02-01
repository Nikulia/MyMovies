package com.nikolaevtsev.mymovies.model.api;

import com.nikolaevtsev.mymovies.model.pojo.ResponseReview;
import com.nikolaevtsev.mymovies.model.pojo.ResponseTrailer;
import com.nikolaevtsev.mymovies.model.pojo.Review;
import com.nikolaevtsev.mymovies.model.pojo.Trailer;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServiceTrailerReview {
    @GET("{id}/videos")
    Observable<ResponseTrailer> getTrailers(@Path("id") int movieId,
                                            @Query("api_key") String apiKey,
                                            @Query("language") String lang);

    @GET("{id}/reviews")
    Observable<ResponseReview> getReviews(@Path("id") int movieId,
                                          @Query("api_key") String apiKey,
                                          @Query("language") String lang);
}
