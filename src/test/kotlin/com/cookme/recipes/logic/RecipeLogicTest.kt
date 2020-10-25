package com.cookme.recipes.logic

import com.cookme.recipes.domain.IngredientDTO
import com.cookme.recipes.domain.RecipeDTO
import com.cookme.recipes.mongo.documents.Ingredient
import com.cookme.recipes.mongo.documents.Recipe
import com.cookme.recipes.mongo.repository.RecipeRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RecipeLogicTest {

    @MockK
    private lateinit var recipeRepository: RecipeRepository

    private lateinit var recipeLogic: RecipeLogic

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        recipeLogic = RecipeLogic(recipeRepository)
    }

    @Test
    fun `create a recipe`() {

        val identifier = UUID.randomUUID()

        val recipeDto = RecipeDTO("A simple example", null,
                setOf(IngredientDTO("ingre1", "measure1"), IngredientDTO("ingre2", "measure2")),
                setOf("tag1", "tag2"),
                "This is a recipe")

        val recipe = Recipe(identifier.toString(), null, "A simple example",
                setOf("tag1", "tag2"),
                setOf(Ingredient("ingre1", "measure1"), Ingredient("ingre2", "measure2")),
                "This is a recipe")

        every { recipeRepository.insert(recipe) } returns recipe

        val recipeResult = recipeLogic.createRecipe(recipeDto, identifier)

        assertEquals(recipe, recipeResult)
        assertEquals("A simple example", recipeResult.title)
        assertEquals("This is a recipe", recipeResult.recipe)
        assertTrue(recipeResult.tags.contains("tag1"))
        assertTrue(recipeResult.tags.contains("tag2"))
        assertNotNull(recipeResult.ingredients.find { ingredient -> "ingre1" == ingredient.name })
        assertNotNull(recipeResult.ingredients.find { ingredient -> "ingre2" == ingredient.name })
    }

    @Test
    fun `search a recipe using valid uuid should returns a present recipe`() {

        val identifier = UUID.randomUUID()

        val recipe = Recipe(identifier.toString(), null, "A simple example",
                setOf("tag1", "tag2"),
                setOf(Ingredient("ingre1", "measure1"), Ingredient("ingre2", "measure2")),
                "This is a recipe")

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
    fun `get a random recipe based on valid ingredients should returns a present recipe`(){

        val recipe1 = Recipe(UUID.randomUUID().toString(), null,"Recipe 1",
                setOf("tag1", "tag2"),
                setOf(Ingredient("ingre1", "measure1"), Ingredient("ingre2", "measure2")),
                "This is a recipe 1")
        val recipe2 = Recipe(UUID.randomUUID().toString(), null, "Recipe 2",
                setOf("tag1", "tag3"),
                setOf(Ingredient("ingre3", "measure1"), Ingredient("ingre4", "measure2")),
                "This is a recipe 2")

        every { recipeRepository.findAll() } returns listOf(recipe1, recipe2)

        val result = recipeLogic.getRandomRecipe(setOf("ingre3", "ingre4"))

        assertTrue(result.isPresent)
        assertEquals("Recipe 2", result.get().title)
    }

    @Test
    fun `get a random recipe based on one invalid ingredient should returns a empty recipe`(){

        val recipe1 = Recipe(UUID.randomUUID().toString(), null,"A simple example",
                setOf("tag1", "tag2"),
                setOf(Ingredient("ingre1", "measure1"), Ingredient("ingre2", "measure2")),
                "This is a recipe")
        val recipe2 = Recipe(UUID.randomUUID().toString(), null,"A simple example",
                setOf("tag1", "tag3"),
                setOf(Ingredient("ingre3", "measure1"), Ingredient("ingre4", "measure2")),
                "This is a recipe")

        every { recipeRepository.findAll() } returns listOf(recipe1, recipe2)

        val result = recipeLogic.getRandomRecipe(setOf("invalidIngredient1"))

        assertTrue(result.isEmpty)
    }

    @Test
    fun `get a random recipe when system have not recipes should returns a empty recipe`(){

        every { recipeRepository.findAll() } returns listOf()

        val result = recipeLogic.getRandomRecipe(setOf("ingre1", "ingre2"))

        assertTrue(result.isEmpty)
    }
}