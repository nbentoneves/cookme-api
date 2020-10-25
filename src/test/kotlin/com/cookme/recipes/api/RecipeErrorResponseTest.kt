package com.cookme.recipes.api

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test

class RecipeErrorResponseTest {
    
    @Test
    fun `Verify to String`() {

        val instance = RecipeErrorResponse(400, "Error", "Message", "?tags")

        assertThat(instance.toString(), allOf(
                containsString("status="),
                containsString("error="),
                containsString("message="),
                containsString("path=")))

    }

}