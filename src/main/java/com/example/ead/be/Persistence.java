package com.example.ead.be;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Component
public class Persistence {
    private static final Logger LOGGER = Logger.getLogger(Persistence.class.getName());
    public static List<Recipe> recipes = Arrays.asList(
            new Recipe("elotes",
                    Arrays.asList("corn", "mayonnaise", "cotija cheese", "sour cream", "lime"),
                    35),
            new Recipe("loco moco",
                    Arrays.asList("ground beef", "butter", "onion", "egg", "bread bun", "mushrooms"),
                    54),
            new Recipe("patatas bravas",
                    Arrays.asList("potato", "tomato", "olive oil", "onion", "garlic", "paprika"),
                    80),
            new Recipe("fried rice",
                    Arrays.asList("rice", "soy sauce", "egg", "onion", "pea", "carrot", "sesame oil"),
                    40)
    );

    private MongoClient mongoClient = null;
    private MongoDatabase database = null;
    private MongoCollection<Recipe> collection = null;

    public Persistence() {
        String connString = System.getenv("MONGO_URI");
        String dbName = System.getenv("MONGO_DATABASE");
        String collectionName = "ead_2024";

        if (connString == null || connString.isEmpty()) {
            throw new IllegalStateException("MONGO_URI environment variable is not set");
        }
        if (dbName == null || dbName.isEmpty()) {
            throw new IllegalStateException("MONGO_DATABASE environment variable is not set");
        }

        initMongoDBClient(connString, dbName, collectionName);
    }

    public Persistence(String connString, String dbName, String colName) {
        if (connString == null || connString.isEmpty()) {
            throw new IllegalStateException("MongoDB connection string is required");
        }
        if (dbName == null || dbName.isEmpty()) {
            throw new IllegalStateException("MongoDB database name is required");
        }
        initMongoDBClient(connString, dbName, colName);
    }

    public void initMongoDBClient(String connString, String dbName, String collectionName) {
        ConnectionString mongoUri = new ConnectionString(connString);
        if (mongoClient == null) {
            Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            MongoClientSettings settings = MongoClientSettings.builder()
                    .codecRegistry(pojoCodecRegistry)
                    .applyConnectionString(mongoUri)
                    .retryWrites(true)
                    .retryReads(true)
                    .build();
            int retries = 5;
            while (retries > 0) {
                try {
                    mongoClient = MongoClients.create(settings);
                    database = mongoClient.getDatabase(dbName);
                    collection = database.getCollection(collectionName, Recipe.class);
                    // Test connection
                    collection.countDocuments();
                    LOGGER.info("Connected to MongoDB: " + connString + ", database: " + dbName + ", collection: " + collectionName);
                    break;
                } catch (MongoException me) {
                    LOGGER.severe("MongoDB connection attempt failed: " + me.getMessage());
                    retries--;
                    if (retries == 0) {
                        throw new RuntimeException("MongoDB connection failed after retries", me);
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public List<Recipe> getAllRecipes() {
        try {
            MongoCursor<Recipe> cur = collection.find().iterator();
            List<Recipe> myRecipes = new ArrayList<>();
            while (cur.hasNext()) {
                myRecipes.add(cur.next());
            }
            LOGGER.info("Retrieved " + myRecipes.size() + " recipes");
            return myRecipes;
        } catch (MongoException me) {
            LOGGER.severe("Failed to retrieve recipes: " + me.getMessage());
            throw new RuntimeException("Database query failed", me);
        }
    }

    public int addRecipes(List<Recipe> recipes) {
        try {
            InsertManyResult result = collection.insertMany(recipes);
            LOGGER.info("Inserted " + result.getInsertedIds().size() + " documents");
            return result.getInsertedIds().size();
        } catch (MongoException me) {
            LOGGER.severe("Unable to insert recipes: " + me.getMessage());
            return -1;
        }
    }

    public int deleteRecipes(Bson deleteFilter) {
        try {
            DeleteResult deleteResult = collection.deleteMany(deleteFilter);
            LOGGER.info("Deleted " + deleteResult.getDeletedCount() + " documents");
            return (int) deleteResult.getDeletedCount();
        } catch (MongoException me) {
            LOGGER.severe("Unable to delete recipes: " + me.getMessage());
            return -1;
        }
    }

    public int deleteRecipesByName(List<String> recipeNames) {
        Bson deleteFilter = Filters.in("name", recipeNames);
        return this.deleteRecipes(deleteFilter);
    }

    public void main2() {
        LOGGER.info("Initializing sample recipes...");
        try {
            collection.deleteMany(Filters.exists("_id"));
            addRecipes(recipes);
            List<Recipe> myRecipes = getAllRecipes();
            for (Recipe currentRecipe : myRecipes) {
                LOGGER.info(String.format("%s has %d ingredients and takes %d minutes to make",
                        currentRecipe.getName(), currentRecipe.getIngredients().size(), currentRecipe.getPrepTimeInMinutes()));
            }
            deleteRecipesByName(Arrays.asList("elotes", "fried rice"));
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize sample recipes: " + e.getMessage());
        }
    }
}