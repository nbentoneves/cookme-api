package com.cookme.recipes.domain

import java.io.Serializable

data class RecipeDTO(
        var title: String,
        var listOfIngredients: Set<IngredientDTO>,
        var listOfTags: Set<String> = mutableSetOf(),
        var recipe: String) : Serializable {

    override fun toString(): String {
        return "RecipeDTO[title='$title', " +
                "listOfIngredients=$listOfIngredients, " +
                "listOfTags=$listOfTags]"
    }
}