package com.release.mymovies.data;

public class Trailer {

    private String pathToTrailer;
    private String name;

    public Trailer(String pathToTrailer, String name) {
        this.pathToTrailer = pathToTrailer;
        this.name = name;
    }

    public String getPathToTrailer() {
        return pathToTrailer;
    }

    public void setPathToTrailer(String pathToTrailer) {
        this.pathToTrailer = pathToTrailer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
