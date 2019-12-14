package com.example.mymovies.utils;

import com.example.mymovies.data.Movie;
import com.example.mymovies.data.Review;
import com.example.mymovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static final int COUNT_MOVIES_ON_PAGE = 20;
    private static final String JSON_ARRAY_NAME = "results";

    //for reviews
    private static final String ATTRIBUTE_AUTHOR = "author";
    private static final String ATTRIBUTE_CONTENT = "content";

    //for videos
    private static final String ATTRIBUTE_KEY_OF_VIDEO = "key";
    private static final String ATTRIBUTE_NAME_OF_VIDEO = "name";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    //for information of movie
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_VOTE_COUNT = "vote_count";
    private static final String ATTRIBUTE_TITLE = "title";
    private static final String ATTRIBUTE_ORIGINAL_TITLE = "original_title";
    private static final String ATTRIBUTE_OVERVIEW = "overview";
    private static final String ATTRIBUTE_RELEASE_DATE = "release_date";
    private static final String ATTRIBUTE_RATING = "vote_average";
    private static final String ATTRIBUTE_POSTER_PATH = "poster_path";
    private static final String ATTRIBUTE_BACKDROP_PATH = "backdrop_path";

    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    private static final String SMALL_POSTER_SIZE = "w185";
    private static final String BIG_POSTER_SIZE = "w780";

    public static ArrayList<Movie> getMoviesFromJsonObject(JSONObject jsonObject) {
        ArrayList<Movie> movies = new ArrayList<>();
        if (jsonObject == null)
            return movies;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_NAME);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectMovie = jsonArray.getJSONObject(i);
                int id = jsonObjectMovie.getInt(ATTRIBUTE_ID);
                int voteCount = jsonObjectMovie.getInt(ATTRIBUTE_VOTE_COUNT);
                String title = jsonObjectMovie.getString(ATTRIBUTE_TITLE);
                String originalTitle = jsonObjectMovie.getString(ATTRIBUTE_ORIGINAL_TITLE);
                String overview = jsonObjectMovie.getString(ATTRIBUTE_OVERVIEW);
                String releaseDate = jsonObjectMovie.getString(ATTRIBUTE_RELEASE_DATE);
                double rating = jsonObjectMovie.getDouble(ATTRIBUTE_RATING);
                String smallPosterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE +
                        jsonObjectMovie.getString(ATTRIBUTE_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE +
                        jsonObjectMovie.getString(ATTRIBUTE_POSTER_PATH);
                String backdropPath = jsonObjectMovie.getString(ATTRIBUTE_BACKDROP_PATH);
                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, releaseDate, rating,
                        smallPosterPath, bigPosterPath, backdropPath);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static ArrayList<Review> getReviewsFromJsonObject (JSONObject jsonObject) {
        ArrayList<Review> reviews = new ArrayList<>();
        if (jsonObject == null)
            return reviews;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_NAME);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String author = object.getString(ATTRIBUTE_AUTHOR);
                String content = object.getString(ATTRIBUTE_CONTENT);
                Review review = new Review(author, content);
                reviews.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public static ArrayList<Trailer> getTrailersFromJsonObject (JSONObject jsonObject) {
        ArrayList<Trailer> trailers = new ArrayList<>();
        if (jsonObject == null)
            return trailers;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_NAME);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String pathToTrailer = BASE_YOUTUBE_URL + object.getString(ATTRIBUTE_KEY_OF_VIDEO);
                String name = object.getString(ATTRIBUTE_NAME_OF_VIDEO);
                Trailer trailer = new Trailer(pathToTrailer, name);
                trailers.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

}
