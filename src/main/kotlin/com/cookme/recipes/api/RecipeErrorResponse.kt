package com.cookme.recipes.api

data class RecipeErrorResponse(
        val status: Int,
        val error: String,
        val message: String = "",
        val path: String) {

    override fun toString(): String {
        return "RecipeErrorResponse[status=$status, error='$error', message='$message', path='$path']"
    }
}