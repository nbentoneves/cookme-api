package com.cookme.recipes.batch.domain

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class IngredientTest {

    @Test
    fun `Verify Equals and Hashcode`() {

        val instance = Ingredient("name1", "measure")

        val instance2 = Ingredient("name2", "measure")

        assertNotEquals(instance, instance2)
        assertNotEquals(instance.hashCode(), instance2.hashCode())

    }

    @Test
    fun `Verify to String`() {

        val instance = Ingredient("name1", "measure")

        assertThat(instance.toString(), allOf(
                containsString("name="),
                containsString("measure=")))

    }

}