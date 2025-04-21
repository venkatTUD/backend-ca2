package com.example.ead.be;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
public class ServiceController {

	private final Persistence p = new Persistence(); // Use default constructor that connects to local Mongo

	@GetMapping("/")
	public String index() {
		return "Greetings from EAD CA2 Template project 2023-24!";
	}

	@GetMapping("/recipes")
	public List<Recipe> getAllRecipes() {
		System.out.println("About to get all the recipes in MongoDB!");
		return p.getAllRecipes();
	}

	@DeleteMapping("/recipe/{name}")
	public int deleteRecipe(@PathVariable("name") String name) {
		System.out.println("About to delete all the recipes named " + name);
		return p.deleteRecipesByName(Arrays.asList(name));
	}

	@PostMapping("/recipe")
	@ResponseStatus(HttpStatus.CREATED)
	public int saveRecipe(@RequestBody Recipe rec) {
		System.out.println("About to add the following recipe: " + rec);
		return p.addRecipes(Arrays.asList(rec));
	}
}