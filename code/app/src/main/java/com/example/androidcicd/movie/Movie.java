package com.example.androidcicd.movie;

import androidx.annotation.Nullable;

import java.io.Serializable;

// Movie object
public class Movie implements Serializable {

    // attributes
    private String id;
    private String title;
    private String genre;
    private int year;

    public Movie() {}

    // constructor
    public Movie(String title, String genre, int year) {
        this.title = title;
        this.genre = genre;
        this.year = year;
    }

    // getters and setters
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        assert obj != null;
        if (obj instanceof Movie)
            return this.id.equals(((Movie) obj).id);
        return false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void set(Movie movie) {
        
    }
}
