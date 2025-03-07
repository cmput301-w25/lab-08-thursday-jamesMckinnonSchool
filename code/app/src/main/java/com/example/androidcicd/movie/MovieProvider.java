package com.example.androidcicd.movie;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MovieProvider {
    private static MovieProvider movieProvider;
    private final ArrayList<Movie> movies;
    private final CollectionReference movieCollection;

    private MovieProvider(FirebaseFirestore firestore) {
        movies = new ArrayList<>();
        movieCollection = firestore.collection("movies");
    }

    public static void setInstanceForTesting(FirebaseFirestore firestore) {
        movieProvider = new MovieProvider(firestore);
    }

    public interface DataStatus {
        void onDataUpdated();
        void onError(String error);
    }

    public void listenForUpdates(final DataStatus dataStatus) {
        movieCollection.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                dataStatus.onError(error.getMessage());
                return;
            }
            movies.clear();
            if (snapshot != null) {
                for (QueryDocumentSnapshot item : snapshot) {
                    movies.add(item.toObject(Movie.class));
                }
                dataStatus.onDataUpdated();
            }
        });
    }

    public static MovieProvider getInstance(FirebaseFirestore firestore) {
        if (movieProvider == null)
            movieProvider = new MovieProvider(firestore);
        return movieProvider;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void addMovie(Movie movie) {
        isMovieTitleExists(movie.getTitle(), exists -> {
            if (exists) {
                throw new IllegalArgumentException("A movie with this title already exists!");
            } else {
                DocumentReference docRef = movieCollection.document();
                movie.setId(docRef.getId());
                if (validMovie(movie, docRef)) {
                    docRef.set(movie);
                } else {
                    throw new IllegalArgumentException("Invalid Movie!");
                }
            }
        });
    }

    public void updateMovie(Movie movie, String title, String genre, int year) {
        isMovieTitleExists(title, exists -> {
            if (exists && !movie.getTitle().equals(title)) {
                throw new IllegalArgumentException("A movie with this title already exists!");
            } else {
                movie.setTitle(title);
                movie.setGenre(genre);
                movie.setYear(year);
                DocumentReference docRef = movieCollection.document(movie.getId());
                if (validMovie(movie, docRef)) {
                    docRef.set(movie);
                } else {
                    throw new IllegalArgumentException("Invalid Movie!");
                }
            }
        });
    }

    public void deleteMovie(Movie movie) {
        DocumentReference docRef = movieCollection.document(movie.getId());
        docRef.delete();
    }

    private void isMovieTitleExists(String title, OnTitleCheckListener listener) {
        movieCollection.whereEqualTo("title", title).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                listener.onCheck(true);
            } else {
                listener.onCheck(false);
            }
        });
    }

    public boolean validMovie(Movie movie, DocumentReference docRef) {
        return movie.getId().equals(docRef.getId()) && !movie.getTitle().isEmpty() && !movie.getGenre().isEmpty() && movie.getYear() > 0;
    }

    private interface OnTitleCheckListener {
        void onCheck(boolean exists);
    }
}
