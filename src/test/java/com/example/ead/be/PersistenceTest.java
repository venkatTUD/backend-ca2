package com.example.ead.be;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PersistenceTest {

    @InjectMocks
    private Persistence persistence;

    @Mock
    private MongoClient mongoClient;
    @Mock
    private MongoDatabase database;
    @Mock
    private MongoCollection<Recipe> collection;
    @Mock
    private FindIterable<Recipe> findIterable;
    @Mock
    private MongoCursor<Recipe> mongoCursor;
    @Mock
    private InsertManyResult insertManyResult;
    @Mock
    private DeleteResult deleteResult;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.collection}")
    private String collectionName;

    private static final List<Recipe> TEST_RECIPES = Arrays.asList(
        new Recipe("testRecipe1", Collections.singletonList("ingredient1"), 10),
        new Recipe("testRecipe2", Collections.singletonList("ingredient2"), 20)
    );

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        persistence = new Persistence();
        
        // Set required properties
        ReflectionTestUtils.setField(persistence, "mongoUri", "mongodb://localhost:27017");
        ReflectionTestUtils.setField(persistence, "databaseName", "testDB");
        ReflectionTestUtils.setField(persistence, "collectionName", "testCollection");
        
        // Set mocked dependencies
        ReflectionTestUtils.setField(persistence, "mongoClient", mongoClient);
        ReflectionTestUtils.setField(persistence, "database", database);
        ReflectionTestUtils.setField(persistence, "collection", collection);
    }

    @Test
    void testGetAllRecipes_Success() {
        when(collection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(true, true, false);
        when(mongoCursor.next()).thenReturn(TEST_RECIPES.get(0), TEST_RECIPES.get(1));

        List<Recipe> results = persistence.getAllRecipes();
        
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(collection, times(1)).find();
    }

    @Test
    void testGetAllRecipes_Failure() {
        when(collection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenThrow(new MongoException("Connection error"));

        Exception exception = assertThrows(RuntimeException.class, () -> persistence.getAllRecipes());
        assertEquals("com.mongodb.MongoException: Connection error", exception.getCause().toString());
    }

    @Test
    void testAddRecipes_Success() {
        when(collection.insertMany(any(List.class))).thenReturn(insertManyResult);
        when(insertManyResult.getInsertedIds()).thenReturn(Collections.singletonMap(0, new ObjectId()));

        int result = persistence.addRecipes(TEST_RECIPES);
        assertEquals(1, result);
    }

    @Test
    void testAddRecipes_Failure() {
        when(collection.insertMany(any(List.class))).thenThrow(new MongoException("Write error"));

        int result = persistence.addRecipes(TEST_RECIPES);
        assertEquals(-1, result);
    }

    @Test
    void testDeleteRecipesByName_Success() {
        when(collection.deleteMany(any(Bson.class))).thenReturn(deleteResult);
        when(deleteResult.getDeletedCount()).thenReturn(2L);

        int result = persistence.deleteRecipesByName(Arrays.asList("testRecipe1", "testRecipe2"));
        assertEquals(2, result);
    }

    @Test
    void testDeleteRecipesByName_Failure() {
        when(collection.deleteMany(any(Bson.class))).thenThrow(new MongoException("Delete error"));

        int result = persistence.deleteRecipesByName(Arrays.asList("testRecipe1", "testRecipe2"));
        assertEquals(-1, result);
    }

    @Test
    void testDeleteRecipes() {
        Bson deleteFilter = mock(Bson.class);
        
        when(collection.deleteMany(deleteFilter)).thenReturn(deleteResult);
        when(deleteResult.getDeletedCount()).thenReturn(1L);
        
        int result = persistence.deleteRecipes(deleteFilter);
        
        assertEquals(1, result);
        verify(collection, times(1)).deleteMany(deleteFilter);
    }
}
//