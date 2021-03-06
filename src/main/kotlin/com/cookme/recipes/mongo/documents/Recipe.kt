package com.cookme.recipes.mongo.documents

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document(collection = "recipes")
data class Recipe(
        @MongoId
        val id: String,
        val externalId: String? = null,
        val title: String,
        val tags: Set<String>,
        val ingredients: Set<Ingredient>,
        val recipe: String) {

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
        return "Recipe[id='$id', " +
                "externalId='$externalId', " +
                "title='$title', " +
                "ingredients='$ingredients', " +
                "tags=$tags]"
    }
}