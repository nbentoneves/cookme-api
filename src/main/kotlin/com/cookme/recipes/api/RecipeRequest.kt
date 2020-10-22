package com.cookme.recipes.api

data class RecipeRequest(
        val title: String,
        val tags: Set<String>,
        val ingredients: Set<Ingredient>,
        val recipe: String) {

    override fun toString(): String {
        return "RecipeRequest[title='$title', " +
                "tags=$tags, " +
                "ingredients=$ingredients, " +
                "recipe='$recipe']"
    }

    data class Ingredient(val name: String, val measure: String) {

        override fun toString(): String {
            return "Ingredient[name='$name', " +
                    "measure='$measure']"
        }

    }

}