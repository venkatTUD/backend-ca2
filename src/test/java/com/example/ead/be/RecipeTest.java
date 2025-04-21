package com.example.ead.be;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RecipeTest {

    @Test
    public void testRecipeConstructorWithParameters() {
        // Arrange
        String name = "Pasta Carbonara";
        List<String> ingredients = Arrays.asList("pasta", "eggs", "bacon", "cheese");
        int prepTime = 30;
        
        // Act
        Recipe recipe = new Recipe(name, ingredients, prepTime);
        
        // Assert
        assertEquals(name, recipe.getName());
        assertEquals(ingredients, recipe.getIngredients());
        assertEquals(prepTime, recipe.getPrepTimeInMinutes());
    }
    
    @Test
    public void testEmptyConstructor() {
        // Act
        Recipe recipe = new Recipe();
        
        // Assert
        assertEquals("", recipe.getName());
        assertNotNull(recipe.getIngredients());
        assertTrue(recipe.getIngredients().isEmpty());
        assertEquals(0, recipe.getPrepTimeInMinutes());
    }
    
    @Test
    public void testSetName() {
        // Arrange
        Recipe recipe = new Recipe();
        String name = "Spaghetti Bolognese";
        
        // Act
        recipe.setName(name);
        
        // Assert
        assertEquals(name, recipe.getName());
    }
    
    @Test
    public void testSetIngredients() {
        // Arrange
        Recipe recipe = new Recipe();
        List<String> ingredients = Arrays.asList("pasta", "ground beef", "tomato sauce");
        
        // Act
        recipe.setIngredients(ingredients);
        
        // Assert
        assertEquals(ingredients, recipe.getIngredients());
    }
    
    @Test
    public void testSetPrepTimeInMinutes() {
        // Arrange
        Recipe recipe = new Recipe();
        int prepTime = 45;
        
        // Act
        recipe.setPrepTimeInMinutes(prepTime);
        
        // Assert
        assertEquals(prepTime, recipe.getPrepTimeInMinutes());
    }
    
    @Test
    public void testToString() {
        // Arrange
        String name = "Lasagna";
        List<String> ingredients = Arrays.asList("pasta sheets", "beef", "tomato", "cheese");
        int prepTime = 60;
        Recipe recipe = new Recipe(name, ingredients, prepTime);
        
        // Act
        String result = recipe.toString();
        
        // Assert
        assertTrue(result.contains(name));
        assertTrue(result.contains(ingredients.toString()));
        assertTrue(result.contains(String.valueOf(prepTime)));
    }
} 