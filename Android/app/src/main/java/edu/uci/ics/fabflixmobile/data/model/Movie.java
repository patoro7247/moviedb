package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String name;
    private final short year;

    private final String director;

    private final String genreString;

    private final String starString;

    public Movie(String name, short year, String director, String genreString, String starString) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.genreString = genreString;
        this.starString = starString;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }
    public String getDirector() {
        return director;
    }
    public String getGenreString() {
        return genreString;
    }
    public String getStarString() {
        return starString;
    }
}