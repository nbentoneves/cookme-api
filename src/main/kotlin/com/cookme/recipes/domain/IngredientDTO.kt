package com.cookme.recipes.domain

import java.io.Serializable

data class IngredientDTO(var name: String,
                         var measure: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IngredientDTO

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "IngredientDTO[name='$name', " +
                "measure='$measure']"
    }
}