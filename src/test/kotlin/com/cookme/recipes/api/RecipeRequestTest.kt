package com.cookme.recipes.api

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test

class RecipeRequestTest {
    
    @Test
    fun `Verify to String`() {

        val instance = RecipeRequest("title", emptySet(), emptySet(), "recipe")

        assertThat(instance.toString(), allOf(
                containsString("title="),
                containsString("tags="),
                containsString("ingredients="),
                containsString("recipe=")))

    }

}