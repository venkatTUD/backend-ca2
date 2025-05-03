package com.example.ead.be;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Component
public class Persistence {

    // @Value("${database.url}")
    // private String mongoUri;

    // @Value("${database.name}")
    // private String databaseName;

    // @Value("${database.collection}")
    // private String collectionName;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.collection}")
    private String collectionName;


    private static final Logger LOGGER = Logger.getLogger(Persistence.class.getName());

    public static final List<Recipe> defaultRecipes = Arrays.asList(
            new Recipe("elotes", Arrays.asList("corn", "mayonnaise", "cotija cheese", "sour cream", "lime"), 35),
            new Recipe("loco moco", Arrays.asList("ground beef", "butter", "onion", "egg", "bread bun", "mushrooms"), 54),
            new Recipe("patatas bravas", Arrays.asList("potato", "tomato", "olive oil", "onion", "garlic", "paprika"), 80),
            new Recipe("fried rice", Arrays.asList("rice", "soy sauce", "egg", "onion", "pea", "carrot", "sesame oil"), 40)
    );

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Recipe> collection;

    @PostConstruct
    public void init() {
        validateConfig();

        LOGGER.info("Initializing MongoDB connection...");
        LOGGER.info("Mongo URI: " + mongoUri);
        LOGGER.info("Mongo Database: " + databaseName);
        LOGGER.info("Mongo Collection: " + collectionName);

        ConnectionString connectionString = new ConnectionString(mongoUri);

        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(pojoCodecRegistry)
                .retryWrites(true)
                .retryReads(true)
                .build();

        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

        connectToDatabase(settings);
        populateDefaultRecipes();
    }

    private void validateConfig() {
        if (mongoUri == null || mongoUri.isEmpty()) {
            throw new IllegalStateException("Missing MongoDB URI");
        }
        if (databaseName == null || databaseName.isEmpty()) {
            throw new IllegalStateException("Missing MongoDB database name");
        }
        if (collectionName == null || collectionName.isEmpty()) {
            throw new IllegalStateException("Missing MongoDB collection name");
        }
    }

    private void connectToDatabase(MongoClientSettings settings) {
        int retries = 5;
        while (retries > 0) {
            try {
                mongoClient = MongoClients.create(settings);
                database = mongoClient.getDatabase(databaseName);
                collection = database.getCollection(collectionName, Recipe.class);
                collection.countDocuments(); // Test connection
                LOGGER.info("MongoDB connection established.");
                break;
            } catch (MongoException e) {
                LOGGER.severe("Failed to connect to MongoDB: " + e.getMessage());
                retries--;
                if (retries == 0) {
                    throw new RuntimeException("MongoDB connection failed after retries", e);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void populateDefaultRecipes() {
        LOGGER.info("Checking for missing default recipes...");
    
        for (Recipe recipe : defaultRecipes) {
            if (collection.find(Filters.eq("name", recipe.getName())).first() == null) {
                LOGGER.info("Inserting missing default recipe: " + recipe.getName());
                collection.insertOne(recipe);
            } else {
                LOGGER.info("Recipe '" + recipe.getName() + "' already exists—skipping insert.");
            }
        }
    }
    
    

    public List<Recipe> getAllRecipes() {
        List<Recipe> result = new ArrayList<>();
        try (MongoCursor<Recipe> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
            LOGGER.info("Fetched " + result.size() + " recipes.");
            return result;
        } catch (MongoException me) {
            LOGGER.severe("Error retrieving recipes: " + me.getMessage());
            throw new RuntimeException(me);
        }
    }

    public int addRecipes(List<Recipe> recipes) {
        try {
            InsertManyResult result = collection.insertMany(recipes);
            LOGGER.info("Inserted " + result.getInsertedIds().size() + " documents.");
            return result.getInsertedIds().size();
        } catch (MongoException me) {
            LOGGER.severe("Error inserting recipes: " + me.getMessage());
            return -1;
        }
    }

    public int deleteRecipes(Bson deleteFilter) {
        try {
            DeleteResult result = collection.deleteMany(deleteFilter);
            LOGGER.info("Deleted " + result.getDeletedCount() + " recipes.");
            return (int) result.getDeletedCount();
        } catch (MongoException me) {
            LOGGER.severe("Error deleting recipes: " + me.getMessage());
            return -1;
        }
    }

    public int deleteRecipesByName(List<String> recipeNames) {
        Bson deleteFilter = Filters.in("name", recipeNames);
        return deleteRecipes(deleteFilter);
    }
}
