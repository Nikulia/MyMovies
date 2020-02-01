package com.nikolaevtsev.mymovies.model.api;

import com.nikolaevtsev.mymovies.model.pojo.Movie;
import com.nikolaevtsev.mymovies.model.pojo.ResponseMovie;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServiceMovie {

    @GET("movie")
    Observable<ResponseMovie> getMovies(@Query("api_key") String apiKey,
                                        @Query("language") String lang,
                                        @Query("page") int page,
                                        @Query("sort_by") String sortBy,
                                        @Query("vote_count.gte") int voteCountValue);

}
