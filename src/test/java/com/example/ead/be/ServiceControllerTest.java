package com.example.ead.be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceControllerTest {

    @Mock
    private Persistence persistence;

    private ServiceController serviceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serviceController = new ServiceController(persistence);
    }

    @Test
    void testIndex() {
        String response = serviceController.index();
        assertEquals("Greetings from EAD CA2 Template project 2023-24!", response);
    }

    @Test
    void testGetAllRecipes() {
        List<Recipe> expectedRecipes = Arrays.asList(
            new Recipe("Recipe 1", Arrays.asList("ingredient1"), 30),
            new Recipe("Recipe 2", Arrays.asList("ingredient2"), 45)
        );
        
        when(persistence.getAllRecipes()).thenReturn(expectedRecipes);
        
        List<Recipe> actualRecipes = serviceController.getAllRecipes();
        
        assertEquals(expectedRecipes, actualRecipes);
        verify(persistence, times(1)).getAllRecipes();
    }

    @Test
    void testDeleteRecipe() {
        String recipeName = "Test Recipe";
        when(persistence.deleteRecipesByName(Arrays.asList(recipeName))).thenReturn(1);
        
        int result = serviceController.deleteRecipe(recipeName);
        
        assertEquals(1, result);
        verify(persistence, times(1)).deleteRecipesByName(Arrays.asList(recipeName));
    }

    @Test
    void testSaveRecipe() {
        Recipe recipe = new Recipe("New Recipe", Arrays.asList("ingredient1"), 30);
        when(persistence.addRecipes(Arrays.asList(recipe))).thenReturn(1);
        
        int result = serviceController.saveRecipe(recipe);
        
        assertEquals(1, result);
        verify(persistence, times(1)).addRecipes(Arrays.asList(recipe));
    }
}

// Dummy Recipe class for testing purposes
class Recipe {
    private String name;
    private String description;
    private List<String> ingredients;
    private int prepTimeInMinutes;

    // Constructor
    public Recipe(String name, List<String> ingredients, int prepTimeInMinutes) {
        this.name = name;
        this.ingredients = ingredients;
        this.prepTimeInMinutes = prepTimeInMinutes;
    }

    // Getters (needed for assertions and potentially mocking)
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public int prepTimeInMinutes() {
        return prepTimeInMinutes;
    }

    // equals and hashCode are good practice for list comparisons
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return java.util.Objects.equals(name, recipe.name) &&
               java.util.Objects.equals(ingredients, recipe.ingredients) &&
               java.util.Objects.equals(prepTimeInMinutes, recipe.prepTimeInMinutes);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, ingredients, prepTimeInMinutes);
    }

    @Override
    public String toString() {
        return "Recipe{" +
               "name='" + name + '\'' +
               ", ingredients=" + ingredients +
               ", prepTimeInMinutes=" + prepTimeInMinutes +
               '}';
    }
}

// Dummy Persistence class (only needed for compilation if not mocking the interface)
// If Persistence is an interface, you don't need this dummy class.
// Assuming it's a class for now based on your provided code.
class Persistence {
    // Default constructor - will not be used when mocking
    public Persistence() {
        // Simulate connection to local Mongo - not active in unit tests
        System.out.println("Persistence: Connecting to local Mongo (simulated)");
    }

    // Method to add recipes (simulated)
    public int addRecipes(List<Recipe> recipes) {
        System.out.println("Persistence: Adding recipes (simulated)");
        return recipes.size(); // Simulate success
    }

    // Method to get all recipes (simulated)
    public List<Recipe> getAllRecipes() {
        System.out.println("Persistence: Getting all recipes (simulated)");
        return Collections.emptyList(); // Simulate empty result by default
    }

    // Method to delete recipes by name (simulated)
    public int deleteRecipesByName(List<String> names) {
        System.out.println("Persistence: Deleting recipes by name (simulated)");
        return names.size(); // Simulate success for all names
    }
}
