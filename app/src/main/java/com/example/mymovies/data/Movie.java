package com.example.mymovies.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie {
    @PrimaryKey(autoGenerate = true)
    private int uniqueId;

    private int id;
    private int voteCount;
    private String title;
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private double rating;
    private String smallPosterPath;
    private String bigPosterPath;
    private String backdropPath;

    public Movie(int uniqueId, int id, int voteCount, String title, String originalTitle, String overview,
                 String releaseDate, double rating, String smallPosterPath, String bigPosterPath, String backdropPath) {
        this.uniqueId = uniqueId;
        this.id = id;
        this.voteCount = voteCount;
        this.title = title;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.smallPosterPath = smallPosterPath;
        this.bigPosterPath = bigPosterPath;
        this.backdropPath = backdropPath;
    }

    @Ignore
    public Movie(int id, int voteCount, String title, String originalTitle, String overview,
                 String releaseDate, double rating, String smallPosterPath, String bigPosterPath, String backdropPath) {
        this.id = id;
        this.voteCount = voteCount;
        this.title = title;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.smallPosterPath = smallPosterPath;
        this.bigPosterPath = bigPosterPath;
        this.backdropPath = backdropPath;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getSmallPosterPath() {
        return smallPosterPath;
    }

    public void setSmallPosterPath(String smallPosterPath) {
        this.smallPosterPath = smallPosterPath;
    }

    public String getBigPosterPath() {
        return bigPosterPath;
    }

    public void setBigPosterPath(String bigPosterPath) {
        this.bigPosterPath = bigPosterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}
