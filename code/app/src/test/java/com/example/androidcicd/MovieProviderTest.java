package com.example.androidcicd;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.androidcicd.movie.Movie;
import com.example.androidcicd.movie.MovieProvider;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MovieProviderTest {

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock

    private CollectionReference mockCollection;

    @Mock
    private DocumentReference mockDocument;

    @Mock
    private QuerySnapshot mockQuerySnapshot;

    @Mock
    private Task<QuerySnapshot> mockQueryTask;

    @Mock
    private Task<Void> mockWriteTask;

    private MovieProvider movieProvider;

    @Before
    public void setUp() {

        // Mock Firestore and its dependencies
        when(mockFirestore.collection(any(String.class))).thenReturn(mockCollection);
        when(mockCollection.document(any(String.class))).thenReturn(mockDocument);

        // Initialize MovieProvider with the mocked Firestore
        movieProvider = new MovieProvider(mockFirestore);
    }

    @Test
    public void testAddMovie_WhenMovieTitleExists_ShouldCallOnMovieAddError() {
        // Arrange
        Movie movie = new Movie("Existing Movie", "Action", 2021);
        MovieProvider.OnMovieAddListener mockListener = mock(MovieProvider.OnMovieAddListener.class);

        // Mock Firestore query to return a non-empty result
        when(mockCollection.whereEqualTo("title", movie.getTitle())).thenReturn(mockCollection);
        when(mockCollection.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.isSuccessful()).thenReturn(true);
        when(mockQueryTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.isEmpty()).thenReturn(false);

        // Act
        movieProvider.addMovie(movie, mockListener);

        // Assert
        verify(mockListener).onMovieAddError("A movie with this title already exists!");
        verify(mockListener, never()).onMovieAddedSuccessfully();
    }
}
