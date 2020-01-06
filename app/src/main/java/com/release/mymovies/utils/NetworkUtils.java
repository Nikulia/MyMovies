package com.release.mymovies.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.release.mymovies.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String BASE_URL_VIDEO = "https://api.themoviedb.org/3/movie/%d/videos";
    private static final String BASE_URL_REVIEW = "https://api.themoviedb.org/3/movie/%d/reviews";

    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";

    private static final String API_KEY = "cd13d25e707fedec6df80ea235a078fb";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_RATING = "vote_average.desc";
    private static final String MIN_VOTE_COUNT_VALUE = "1000";

    public static final int POPULARITY = 0;
    public static final int RATING = 1;


    public static URL getUrlToVideoJson(int id, String lang) {
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEO, id)).buildUpon().
                appendQueryParameter(PARAMS_API_KEY, API_KEY).
                appendQueryParameter(PARAMS_LANGUAGE, lang).build();
        URL urlToVideoJson = null;
        try {
            urlToVideoJson = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return urlToVideoJson;
    }

    public static URL getUrlToReviewJson(int id, String lang) {
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEW, id)).buildUpon().
                appendQueryParameter(PARAMS_LANGUAGE, lang).
                appendQueryParameter(PARAMS_API_KEY, API_KEY).build();
        URL urlToReviewJson = null;
        try {
            urlToReviewJson = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return urlToReviewJson;
    }

    public static URL getUrlToJson(int page, int sortBy, String lang) {
        String sortByString;
        if (sortBy == POPULARITY)
            sortByString = SORT_BY_POPULARITY;
        else if (sortBy == RATING)
            sortByString = SORT_BY_RATING;
        else
            throw new IllegalArgumentException("No variant of sort with this value");
        Uri uri = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(PARAMS_API_KEY, API_KEY).
                appendQueryParameter(PARAMS_LANGUAGE, lang).
                appendQueryParameter(PARAMS_PAGE, Integer.toString(page)).
                appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE).
                appendQueryParameter(PARAMS_SORT_BY, sortByString).build();
        URL urlToJson = null;
        try {
            urlToJson = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return urlToJson;
    }

    public static JSONObject getJsonObject(int page, int sortBy, String lang) {
        DownloadJsonTask downloadJsonTask = new DownloadJsonTask();
        JSONObject jsonObject = null;
        try {
            jsonObject = downloadJsonTask.execute(getUrlToJson(page, sortBy, lang)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static class JsonLoader extends AsyncTaskLoader<JSONObject> {
        private OnLoadingStartListener onLoadingStartListener;

        public interface OnLoadingStartListener {
            public void onLoadingStart();
        }

        public void setOnLoadingStartListener(OnLoadingStartListener onLoadingStartListener) {
            this.onLoadingStartListener = onLoadingStartListener;
        }

        private Bundle bundle;

        public JsonLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onLoadingStartListener != null)
                onLoadingStartListener.onLoadingStart();
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder jsonStringBuilder = new StringBuilder();
            JSONObject jsonObject = null;
            if (bundle != null) {
                try {
                    url = new URL(bundle.getString(MainActivity.BUNDLE_ATTRIBUTE_URL));
                    urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader
                            (urlConnection.getInputStream(), StandardCharsets.UTF_8));
                    String line = reader.readLine();
                    while (line != null) {
                        jsonStringBuilder.append(line);
                        line = reader.readLine();
                    }
                    jsonObject = new JSONObject(jsonStringBuilder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return jsonObject;
        }


    }

    private static class DownloadJsonTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject jsonObject = null;
            HttpURLConnection urlConnection = null;
            StringBuilder jsonStringBuilder = new StringBuilder();
            if (urls != null && urls.length > 0) {
                try {
                    urlConnection = (HttpURLConnection) urls[0].openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader
                            (urlConnection.getInputStream(), StandardCharsets.UTF_8));
                    String line = reader.readLine();
                    while (line != null) {
                        jsonStringBuilder.append(line);
                        line = reader.readLine();
                    }
                    jsonObject = new JSONObject(jsonStringBuilder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return jsonObject;
        }
    }

    public static JSONObject getJsonObjectVideo(int movieId, String lang) {
        DownloadJsonTask downloadJsonTask = new DownloadJsonTask();
        JSONObject jsonObject = null;
        try {
            jsonObject = downloadJsonTask.execute(getUrlToVideoJson(movieId, lang)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getJsonObjectReview(int movieId, String lang) {
        DownloadJsonTask downloadJsonTask = new DownloadJsonTask();
        JSONObject jsonObject = null;
        try {
            jsonObject = downloadJsonTask.execute(getUrlToReviewJson(movieId, lang)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

