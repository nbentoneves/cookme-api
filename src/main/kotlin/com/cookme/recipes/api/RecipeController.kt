package com.cookme.recipes.api

import com.cookme.recipes.domain.IngredientDTO
import com.cookme.recipes.domain.RecipeDTO
import com.cookme.recipes.logic.RecipeLogic
import com.cookme.recipes.mongo.documents.Recipe
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/recipes")
class RecipeController(@Autowired private val recipeLogic: RecipeLogic) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RecipeController::class.java)
    }

    /**
     * Request must be done like: /recipes/create
     *
     */
    @PostMapping("/create")
    fun createRecipe(@RequestBody request: RecipeRequest): ResponseEntity<Recipe> {

        LOGGER.info("opr=createRecipe, msg='Request', request={}", request)

        val recipeDto = RecipeDTO(title = request.title,
                listOfTags = request.tags,
                listOfIngredients = request.ingredients
                        .map { ingredient -> IngredientDTO(ingredient.name, ingredient.measure) }
                        .toSet(),
                recipe = request.recipe)

        val recipe = recipeLogic.createRecipe(recipeDto, UUID.randomUUID())

        return ResponseEntity(recipe, HttpStatus.CREATED)

    }

    /**
     * Request must be done like: /recipes/get/{uuid}
     *
     */
    @RequestMapping("/get/{id}", method = [RequestMethod.GET])
    fun getRecipe(@PathVariable("id") id: String): ResponseEntity<Any> {

        LOGGER.info("opr=getRecipe, msg='Request', id={}", id)

        try {
            val recipe: Optional<Recipe> = recipeLogic.searchRecipe(UUID.fromString(id))

            return if (recipe.isEmpty) {
                val apiError = RecipeErrorResponse(
                        status = HttpStatus.NOT_FOUND.value(),
                        error = "Did not found any recipe",
                        path = "/get/$id")

                ResponseEntity(apiError, HttpStatus.NOT_FOUND)
            } else {
                ResponseEntity(recipe.get(), HttpStatus.OK)
            }
        } catch (ex: IllegalArgumentException) {
            return ResponseEntity(RecipeErrorResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = "The recipe identifier is invalid",
                    path = "/get/$id"), HttpStatus.BAD_REQUEST)

        }

    }

    /**
     * Request must be done like: /recipes/get?ingredients=apple,rice
     *
     */
    @GetMapping("/get")
    @CrossOrigin(origins = ["*"])
    fun getRandomRecipe(@RequestParam ingredients: String): ResponseEntity<Any> {

        LOGGER.info("opr=getRandomRecipe, msg='Request', ingredients={}", ingredients)

        val setOfIngredients = ingredients
                .split(",")
                .map { tag -> tag.trim() }
                .toSet()

        val recipe: Optional<Recipe> = recipeLogic.getRandomRecipe(setOfIngredients)

        return if (recipe.isEmpty) {
            val apiError = RecipeErrorResponse(
                    status = HttpStatus.NOT_FOUND.value(),
                    error = "Did not found any recipe",
                    path = "/get?ingredients=$ingredients")

            ResponseEntity(apiError, HttpStatus.NOT_FOUND)
        } else {
            ResponseEntity(recipe.get(), HttpStatus.OK)
        }

    }

}