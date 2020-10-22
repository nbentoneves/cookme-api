package com.cookme.recipes.batch.domain

import java.io.Serializable

data class Ingredient(var name: String,
                      var measure: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ingredient

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "Ingredients[name='$name', measure='$measure']"
    }

}