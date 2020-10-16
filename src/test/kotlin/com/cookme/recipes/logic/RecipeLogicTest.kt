package com.cookme.recipes.logic

import com.cookme.recipes.domain.RecipeDTO
import com.cookme.recipes.mongo.documents.Recipe
import com.cookme.recipes.mongo.repository.RecipeRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RecipeLogicTest {

    @MockK
    lateinit var recipeRepository: RecipeRepository

    lateinit var recipeLogic: RecipeLogic

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        recipeLogic = RecipeLogic(recipeRepository)
    }

    @Test
    fun `create a recipe`() {

        val identifier = UUID.randomUUID()

        val recipeDto = RecipeDTO("A simple example", setOf("tag1", "tag2"),
                "This is a recipe")

        val recipe = Recipe(identifier.toString(), "A simple example",
                setOf("tag1", "tag2"), "This is a recipe")

        every { recipeRepository.insert(recipe) } returns recipe

        val recipeResult = recipeLogic.createRecipe(recipeDto, identifier)

        assertEquals(recipe, recipeResult)
        assertEquals("A simple example", recipeResult.title)
        assertEquals("This is a recipe", recipeResult.recipe)
        assertTrue(recipeResult.tags.contains("tag1"))
        assertTrue(recipeResult.tags.contains("tag2"))
    }

    @Test
    fun `search a recipe using valid uuid should returns a present recipe`() {

        val identifier = UUID.randomUUID()

        val recipe = Recipe(identifier.toString(), "A simple example",
                setOf("tag1", "tag2"), "This is a recipe")

        every { recipeRepository.findById(identifier.toString()) } returns Optional.of(recipe)

        val result = recipeLogic.searchRecipe(identifier)

        assertTrue(result.isPresent)
    }

    @Test
    fun `search a recipe using invalid uuid should returns a empty recipe`() {

        val identifier = UUID.randomUUID()

        every { recipeRepository.findById(identifier.toString()) } returns Optional.empty()

        val result = recipeLogic.searchRecipe(identifier)

        assertTrue(result.isEmpty)
    }

    @Test
    fun `get a random recipe based on valid tags should returns a present recipe`(){

        val recipe1 = Recipe(UUID.randomUUID().toString(), "A simple example",
                setOf("tag1", "tag2"), "This is a recipe")
        val recipe2 = Recipe(UUID.randomUUID().toString(), "A simple example",
                setOf("tag1", "tag3"), "This is a recipe")

        every { recipeRepository.findAll() } returns listOf(recipe1, recipe2)

        val result = recipeLogic.getRandomRecipe(setOf("tag1", "tag2"))

        assertTrue(result.isPresent)
    }

    @Test
    fun `get a random recipe based on one invalid tag should returns a empty recipe`(){

        val recipe1 = Recipe(UUID.randomUUID().toString(), "A simple example",
                setOf("tag1", "tag2"), "This is a recipe")
        val recipe2 = Recipe(UUID.randomUUID().toString(), "A simple example",
                setOf("tag1", "tag3"), "This is a recipe")

        every { recipeRepository.findAll() } returns listOf(recipe1, recipe2)

        val result = recipeLogic.getRandomRecipe(setOf("invalidTag1", "tag2"))

        assertTrue(result.isEmpty)
    }

    @Test
    fun `get a random recipe when system have not recipes should returns a empty recipe`(){

        every { recipeRepository.findAll() } returns listOf()

        val result = recipeLogic.getRandomRecipe(setOf("tag1", "tag2"))

        assertTrue(result.isEmpty)
    }
}