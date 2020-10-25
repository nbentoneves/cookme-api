package com.cookme.recipes.batch.domain

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class RecipeTest {

    @Test
    fun `Verify Equals and Hashcode`() {

        val instance = Recipe("20012", "Recipe Title", "Category",
                "area", "instructions", emptySet(),
                "youtube", "source", emptySet())

        val instance2 = Recipe("20013", "Recipe Title", "Category",
                "area", "instructions", emptySet(),
                "youtube", "source", emptySet())

        assertNotEquals(instance, instance2)
        assertNotEquals(instance.hashCode(), instance2.hashCode())

    }

    @Test
    fun `Verify to String`() {

        val instance = Recipe("20012", "Recipe Title", "Category",
                "area", "instructions", emptySet(),
                "youtube", "source", emptySet())

        assertThat(instance.toString(), allOf(
                containsString("id="),
                containsString("title="),
                containsString("category="),
                containsString("area="),
                containsString("tags="),
                containsString("youtube="),
                containsString("source="),
                containsString("ingredients=")))

    }

}