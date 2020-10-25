package com.cookme.recipes.api

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

class IngredientsTest {

    @Test
    fun `Verify to String`() {

        val instance = RecipeRequest.Ingredient("name", "1")

        MatcherAssert.assertThat(instance.toString(), Matchers.allOf(
                Matchers.containsString("name="),
                Matchers.containsString("measure=")))

    }

}