package com.example.ead.be;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PersistenceTest {

    @Mock
    private MongoClient mockMongoClient;

    @Mock
    private MongoDatabase mockDatabase;

    @Mock
    private MongoCollection<Recipe> mockCollection;

    @Mock
    private FindIterable<Recipe> mockFindIterable;

    @Mock
    private MongoCursor<Recipe> mockCursor;

    private Persistence persistence;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock the MongoDB client chain
        when(mockMongoClient.getDatabase(anyString())).thenReturn(mockDatabase);
        when(mockDatabase.getCollection(anyString(), eq(Recipe.class))).thenReturn(mockCollection);
        
        // Create a persistence instance with reflection to use mocks
        persistence = new Persistence();
        
        // Use reflection to inject our mocks
        java.lang.reflect.Field mongoClientField = Persistence.class.getDeclaredField("mongoClient");
        mongoClientField.setAccessible(true);
        mongoClientField.set(persistence, mockMongoClient);
        
        java.lang.reflect.Field databaseField = Persistence.class.getDeclaredField("database");
        databaseField.setAccessible(true);
        databaseField.set(persistence, mockDatabase);
        
        java.lang.reflect.Field collectionField = Persistence.class.getDeclaredField("collection");
        collectionField.setAccessible(true);
        collectionField.set(persistence, mockCollection);
    }

    @Test
    public void testGetAllRecipes() {
        // Arrange
        List<Recipe> expectedRecipes = Arrays.asList(
            new Recipe("Recipe1", Arrays.asList("ingredient1", "ingredient2"), 30),
            new Recipe("Recipe2", Arrays.asList("ingredient3", "ingredient4"), 45)
        );
        
        when(mockCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        
        // Set up cursor to return recipes then null on next() calls
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(expectedRecipes.get(0), expectedRecipes.get(1));
        
        // Act
        List<Recipe> result = persistence.getAllRecipes();
        
        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedRecipes.get(0).getName(), result.get(0).getName());
        assertEquals(expectedRecipes.get(1).getName(), result.get(1).getName());
        verify(mockCollection, times(1)).find();
    }

    @Test
    public void testAddRecipes_Success() {
        // Arrange
        List<Recipe> recipesToAdd = Arrays.asList(
            new Recipe("Recipe1", Arrays.asList("ingredient1", "ingredient2"), 30)
        );
        
        // Mock successful insertion result
        InsertManyResult mockResult = mock(InsertManyResult.class);
        Map<Integer, BsonValue> insertedIds = new HashMap<>();
        insertedIds.put(0, mock(BsonValue.class));
        when(mockResult.getInsertedIds()).thenReturn(insertedIds);
        
        when(mockCollection.insertMany(recipesToAdd)).thenReturn(mockResult);
        
        // Act
        int result = persistence.addRecipes(recipesToAdd);
        
        // Assert
        assertEquals(1, result);
        verify(mockCollection, times(1)).insertMany(recipesToAdd);
    }

    @Test
    public void testAddRecipes_Exception() {
        // Arrange
        List<Recipe> recipesToAdd = Arrays.asList(
            new Recipe("Recipe1", Arrays.asList("ingredient1", "ingredient2"), 30)
        );
        
        // Mock MongoDB exception on insert
        when(mockCollection.insertMany(any())).thenThrow(new MongoException("Test exception"));
        
        // Act
        int result = persistence.addRecipes(recipesToAdd);
        
        // Assert
        assertEquals(-1, result);
        verify(mockCollection, times(1)).insertMany(recipesToAdd);
    }

    @Test
    public void testDeleteRecipes_Success() {
        // Arrange
        Bson mockFilter = mock(Bson.class);
        DeleteResult mockResult = mock(DeleteResult.class);
        when(mockResult.getDeletedCount()).thenReturn(2L);
        
        when(mockCollection.deleteMany(mockFilter)).thenReturn(mockResult);
        
        // Act
        int result = persistence.deleteRecipes(mockFilter);
        
        // Assert
        assertEquals(2, result);
        verify(mockCollection, times(1)).deleteMany(mockFilter);
    }

    @Test
    public void testDeleteRecipes_Exception() {
        // Arrange
        Bson mockFilter = mock(Bson.class);
        
        // Mock MongoDB exception on delete
        when(mockCollection.deleteMany(any())).thenThrow(new MongoException("Test exception"));
        
        // Act
        int result = persistence.deleteRecipes(mockFilter);
        
        // Assert
        assertEquals(-1, result);
        verify(mockCollection, times(1)).deleteMany(mockFilter);
    }

    @Test
    public void testDeleteRecipesByName() {
        // Arrange - using a spy to verify the deleteRecipes method is called with proper filter
        Persistence spy = spy(persistence);
        List<String> recipeNames = Arrays.asList("Recipe1", "Recipe2");
        
        doReturn(2).when(spy).deleteRecipes(any(Bson.class));
        
        // Act
        int result = spy.deleteRecipesByName(recipeNames);
        
        // Assert
        assertEquals(2, result);
        verify(spy, times(1)).deleteRecipes(any(Bson.class));
    }
} 