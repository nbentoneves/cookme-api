package com.cookme.recipes.api

import com.cookme.recipes.domain.IngredientDTO
import com.cookme.recipes.domain.RecipeDTO
import com.cookme.recipes.logic.RecipeLogic
import com.cookme.recipes.mongo.documents.Ingredient
import com.cookme.recipes.mongo.documents.Recipe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class RecipeControllerTest {

    @MockK
    lateinit var recipeLogic: RecipeLogic

    lateinit var recipeController: RecipeController

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        recipeController = RecipeController(recipeLogic)
    }

    @Test
    fun `create a recipe`() {

        val identifier = UUID.randomUUID()
        val request = RecipeRequest("A title", setOf("tag1", "tag2"),
                setOf(RecipeRequest.Ingredient("ingre1", "measure1")),
                "recipe")

        val recipe = Recipe(identifier.toString(), "A title", setOf("tag1", "tag2"),
                setOf(Ingredient("ingre1", "measuere1")),
                "recipe")

        every {
            recipeLogic.createRecipe(
                    RecipeDTO("A title",
                            setOf(IngredientDTO("ingre1", "measure1")),
                            setOf("tag1", "tag2"), "recipe"), any())
        } returns recipe

        val result = recipeController.createRecipe(request)

        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals("A title", result.body!!.title)
        assertEquals(setOf("tag1", "tag2"), result.body!!.tags)
        assertEquals(identifier.toString(), result.body!!.id)
        assertEquals("recipe", result.body!!.recipe)
    }

    @Test
    fun `get a valid recipe`() {

        val identifier = UUID.randomUUID()

        val recipe = Recipe(identifier.toString(), "A title",
                setOf("tag1", "tag2"),
                setOf(Ingredient("ingre1", "measuere1")),
                "recipe")

        every {
            recipeLogic.searchRecipe(identifier)
        } returns Optional.of(recipe)

        val result = recipeController.getRecipe(identifier.toString())

        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertTrue(result.body is Recipe)
    }

    @Test
    fun `get a invalid recipe`() {

        val identifier = UUID.randomUUID()

        every {
            recipeLogic.searchRecipe(identifier)
        } returns Optional.empty()

        val result = recipeController.getRecipe(identifier.toString())

        val apiError = result.body as RecipeErrorResponse

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("Did not found any recipe", apiError.error)
        assertEquals("", apiError.message)
        assertEquals("/get/${identifier}", apiError.path)
        assertEquals(404, apiError.status)
    }

    @Test
    fun `get a recipe with invalid uuid`() {

        val result = recipeController.getRecipe("INVALID")

        val apiError = result.body as RecipeErrorResponse

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals("The recipe identifier is invalid", apiError.error)
        assertEquals("", apiError.message)
        assertEquals("/get/INVALID", apiError.path)
        assertEquals(400, apiError.status)
    }

    @Test
    fun `get a valid random recipe`() {

        val recipe = Recipe(UUID.randomUUID().toString(), "A title",
                setOf("tag1", "tag2"),
                setOf(Ingredient("ingre1", "measuere1")),
                "recipe")

        every {
            recipeLogic.getRandomRecipe(setOf("tag1", "tag2"))
        } returns Optional.of(recipe)

        val result = recipeController.getRandomRecipe("tag1,tag2")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertTrue(result.body is Recipe)

    }

    @Test
    fun `get a invalid random recipe`() {

        every {
            recipeLogic.getRandomRecipe(setOf("tag1-tag2"))
        } returns Optional.empty()

        val result = recipeController.getRandomRecipe("tag1-tag2")

        val apiError = result.body as RecipeErrorResponse

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("Did not found any recipe", apiError.error)
        assertEquals("", apiError.message)
        assertEquals("/get?tags=tag1-tag2", apiError.path)
        assertEquals(404, apiError.status)
    }

}