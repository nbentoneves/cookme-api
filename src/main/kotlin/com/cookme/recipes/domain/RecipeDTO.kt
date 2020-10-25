package com.cookme.recipes.domain

import java.io.Serializable

data class RecipeDTO(
        var title: String,
        var externalId: String? = null,
        var listOfIngredients: Set<IngredientDTO>,
        var listOfTags: Set<String> = mutableSetOf(),
        var recipe: String) : Serializable {

    override fun toString(): String {
        return "RecipeDTO[title='$title', " +
                "externalId='$externalId', " +
                "listOfIngredients=$listOfIngredients, " +
                "listOfTags=$listOfTags]"
    }
}