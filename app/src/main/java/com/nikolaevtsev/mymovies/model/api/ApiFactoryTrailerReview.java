package com.nikolaevtsev.mymovies.model.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactoryTrailerReview {

    private static Retrofit retrofit;
    private static ApiFactoryTrailerReview apiFactory;
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    private ApiFactoryTrailerReview() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL).build();
    }

    public static ApiFactoryTrailerReview getInstance () {
        if (apiFactory == null)
            apiFactory = new ApiFactoryTrailerReview();
        return apiFactory;
    }

    public ApiServiceTrailerReview getApiService () {
        return retrofit.create(ApiServiceTrailerReview.class);
    }

}
