package com.cookme.recipes.mongo.documents

data class Ingredient(val name: String,
                      val measure: String) {

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
        return "Ingredient[name='$name', " +
                "measure='$measure']"
    }

}