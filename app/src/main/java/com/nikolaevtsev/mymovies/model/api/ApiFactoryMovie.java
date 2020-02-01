package com.nikolaevtsev.mymovies.model.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactoryMovie {

    private static Retrofit retrofit;
    private static ApiFactoryMovie apiFactory;
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/";

    private ApiFactoryMovie() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public static ApiFactoryMovie getInstance() {
        if (apiFactory == null)
            apiFactory = new ApiFactoryMovie();
        return apiFactory;
    }

    public ApiServiceMovie getApiService() {
        return retrofit.create(ApiServiceMovie.class);
    }

}
