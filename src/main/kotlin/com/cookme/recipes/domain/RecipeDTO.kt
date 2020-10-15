package com.cookme.recipes.domain

data class RecipeDTO(
        var title: String,
        var listOfTags: Set<String> = mutableSetOf(),
        var recipe: String) {

    override fun toString(): String {
        return "RecipeDTO[title='$title', listOfTags=$listOfTags, recipe='$recipe']"
    }
}