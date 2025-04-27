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
        String connString = System.getenv().getOrDefault("MONGO_URI", "mongodb://admin:securepassword@localhost:27017/ead_2024?authSource=admin");
        String dbName = System.getenv().getOrDefault("MONGO_DATABASE", "ead_2024");
        String collectionName = "ead_2024";
        initMongoDBClient(connString, dbName, collectionName);
    }

    public Persistence(String connString, String dbName, String colName) {
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
                    .applyConnectionString(mongoUri).build();
            try {
                mongoClient = MongoClients.create(settings);
                System.out.println("Connected to MongoDB: " + connString);
            } catch (MongoException me) {
                System.err.println("Unable to connect to MongoDB: " + me);
                throw new RuntimeException("MongoDB connection failed", me);
            }
        }
        database = mongoClient.getDatabase(dbName);
        collection = database.getCollection(collectionName, Recipe.class);
        System.out.println("Initialized database: " + dbName + ", collection: " + collectionName);
    }

    public List<Recipe> getAllRecipes() {
        MongoCursor<Recipe> cur = collection.find().iterator();
        List<Recipe> myRecipes = new ArrayList<>();
        while (cur.hasNext()) {
            myRecipes.add(cur.next());
        }
        System.out.println("Retrieved " + myRecipes.size() + " recipes");
        return myRecipes;
    }

    public int addRecipes(List<Recipe> recipes) {
        try {
            InsertManyResult result = collection.insertMany(recipes);
            System.out.println("Inserted " + result.getInsertedIds().size() + " documents");
            return result.getInsertedIds().size();
        } catch (MongoException me) {
            System.err.println("Unable to insert recipes: " + me);
            return -1;
        }
    }

    public int deleteRecipes(Bson deleteFilter) {
        try {
            DeleteResult deleteResult = collection.deleteMany(deleteFilter);
            System.out.printf("Deleted %d documents\n", deleteResult.getDeletedCount());
            return (int) deleteResult.getDeletedCount();
        } catch (MongoException me) {
            System.err.println("Unable to delete recipes: " + me);
            return -1;
        }
    }

    public int deleteRecipesByName(List<String> recipeNames) {
        Bson deleteFilter = Filters.in("name", recipeNames);
        return this.deleteRecipes(deleteFilter);
    }

    public void main2() {
        System.out.println("Initializing sample recipes...");
        collection.deleteMany(Filters.exists("_id"));
        addRecipes(recipes);
        List<Recipe> myRecipes = getAllRecipes();
        for (Recipe currentRecipe : myRecipes) {
            System.out.printf("%s has %d ingredients and takes %d minutes to make\n",
                    currentRecipe.getName(), currentRecipe.getIngredients().size(), currentRecipe.getPrepTimeInMinutes());
        }
        deleteRecipesByName(Arrays.asList("elotes", "fried rice"));
    }

    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        ConnectionString mongoUri = new ConnectionString("mongodb+srv://ead2024:ead2024@ead-2023-24.lpclwdo.mongodb.net/");
        String dbName = "ead_ca2";
        String collectionName = "ead_2024";

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(mongoUri).build();

        MongoClient mongoClient = null;
        try {
            mongoClient = MongoClients.create(settings);
        } catch (MongoException me) {
            System.err.println("Unable to connect to the MongoDB instance due to an error: " + me);
            System.exit(1);
        }

        MongoDatabase database = mongoClient.getDatabase(dbName);
        MongoCollection<Recipe> collection = database.getCollection(collectionName, Recipe.class);

        try {
            InsertManyResult result = collection.insertMany(recipes);
            System.out.println("Inserted " + result.getInsertedIds().size() + " documents.\n");
        } catch (MongoException me) {
            System.err.println("Unable to insert any recipes into MongoDB due to an error: " + me);
            System.exit(1);
        }

        try (MongoCursor<Recipe> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Recipe currentRecipe = cursor.next();
                System.out.printf("%s has %d ingredients and takes %d minutes to make\n",
                        currentRecipe.getName(), currentRecipe.getIngredients().size(), currentRecipe.getPrepTimeInMinutes());
            }
        } catch (MongoException me) {
            System.err.println("Unable to find any recipes in MongoDB due to an error: " + me);
        }

        Bson findPotato = Filters.eq("ingredients", "potato");
        try {
            Recipe firstPotato = collection.find(findPotato).first();
            if (firstPotato == null) {
                System.out.println("Couldn't find any recipes containing 'potato' as an ingredient in MongoDB.");
                System.exit(1);
            }
        } catch (MongoException me) {
            System.err.println("Unable to find a recipe to update in MongoDB due to an error: " + me);
            System.exit(1);
        }

        Bson updateFilter = Updates.set("prepTimeInMinutes", 72);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);

        try {
            Recipe updatedDocument = collection.findOneAndUpdate(findPotato, updateFilter, options);
            if (updatedDocument == null) {
                System.out.println("Couldn't update the recipe. Did someone (or something) delete it?");
            } else {
                System.out.println("\nUpdated the recipe to: " + updatedDocument);
            }
        } catch (MongoException me) {
            System.err.println("Unable to update any recipes due to an error: " + me);
        }

        Bson deleteFilter = Filters.in("name", Arrays.asList("elotes", "fried rice"));
        try {
            DeleteResult deleteResult = collection.deleteMany(deleteFilter);
            System.out.printf("\nDeleted %d documents.\n", deleteResult.getDeletedCount());
        } catch (MongoException me) {
            System.err.println("Unable to delete any recipes due to an error: " + me);
        }

        mongoClient.close();
    }
}