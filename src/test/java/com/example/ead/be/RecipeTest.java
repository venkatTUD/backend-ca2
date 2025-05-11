package com.example.ead.be;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RecipeTest {

    @Test
    void testDefaultConstructor() {
        Recipe recipe = new Recipe();
        assertNotNull(recipe);
        assertEquals("", recipe.getName());
        assertNotNull(recipe.getIngredients());
        assertTrue(recipe.getIngredients().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        String name = "Test Recipe";
        List<String> ingredients = Arrays.asList("ingredient1", "ingredient2");
        int prepTime = 30;

        Recipe recipe = new Recipe(name, ingredients, prepTime);

        assertEquals(name, recipe.getName());
        assertEquals(ingredients, recipe.getIngredients());
        assertEquals(prepTime, recipe.getPrepTimeInMinutes());
    }

    @Test
    void testSettersAndGetters() {
        Recipe recipe = new Recipe();
        ObjectId id = new ObjectId();
        String name = "New Recipe";
        List<String> ingredients = Arrays.asList("new ingredient");
        int prepTime = 45;

        recipe.setId(id);
        recipe.setName(name);
        recipe.setIngredients(ingredients);
        recipe.setPrepTimeInMinutes(prepTime);

        assertEquals(id, recipe.getId());
        assertEquals(name, recipe.getName());
        assertEquals(ingredients, recipe.getIngredients());
        assertEquals(prepTime, recipe.getPrepTimeInMinutes());
    }

    @Test
    void testToString() {
        Recipe recipe = new Recipe("Test Recipe", Arrays.asList("ingredient1"), 30);
        String toString = recipe.toString();
        
        assertTrue(toString.contains("Test Recipe"));
        assertTrue(toString.contains("ingredient1"));
        assertTrue(toString.contains("30"));
    }
}
//