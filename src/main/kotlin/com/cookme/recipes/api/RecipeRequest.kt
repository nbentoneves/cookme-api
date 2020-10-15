package com.cookme.recipes.api

data class RecipeRequest(
        val title: String,
        val tags: Set<String>,
        val recipe: String) {

    override fun toString(): String {
        return "RecipeRequest[title='$title', tags=$tags, recipe='$recipe']"
    }

}