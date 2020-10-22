package com.cookme.recipes.batch.domain

import java.io.Serializable

data class Recipe(var id: String,
                  var title: String,
                  var category: String,
                  var area: String,
                  var instructions: String,
                  var tags: Set<String> = setOf(),
                  var youtube: String,
                  var source: String,
                  var ingredients: Set<Ingredient> = setOf()) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Recipe[id='$id', title='$title', category='$category', area='$area', " +
                "instructions='$instructions', tags=$tags, youtube='$youtube', " +
                "source='$source', ingredients=$ingredients]"
    }

}