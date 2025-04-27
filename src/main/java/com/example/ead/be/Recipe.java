package com.example.ead.be;

import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private ObjectId id;
    private String name;
    private List<String> ingredients;
    private int prepTimeInMinutes;

    public Recipe(String name, List<String> ingredients, int prepTimeInMinutes) {
        this.name = name;
        this.ingredients = ingredients;
        this.prepTimeInMinutes = prepTimeInMinutes;
    }

    public Recipe() {
        this.ingredients = new ArrayList<>();
        this.name = "";
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Recipe{");
        sb.append("id=").append(id != null ? id.toHexString() : null);
        sb.append(", name=").append(name);
        sb.append(", ingredients=").append(ingredients);
        sb.append(", prepTimeInMinutes=").append(prepTimeInMinutes);
        sb.append('}');
        return sb.toString();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public int getPrepTimeInMinutes() {
        return prepTimeInMinutes;
    }

    public void setPrepTimeInMinutes(int prepTimeInMinutes) {
        this.prepTimeInMinutes = prepTimeInMinutes;
    }
}