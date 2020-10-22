package com.cookme.recipes.logic

import com.cookme.recipes.domain.RecipeDTO
import com.cookme.recipes.mongo.documents.Ingredient
import com.cookme.recipes.mongo.documents.Recipe
import com.cookme.recipes.mongo.repository.RecipeRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.lang.NonNull
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class RecipeLogic(@Autowired private val recipeRepository: RecipeRepository) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RecipeLogic::class.java)
    }

    fun createRecipe(@NonNull recipeDto: RecipeDTO,
                     @NonNull uuid: UUID): Recipe {

        val setOfIngredients = recipeDto.listOfIngredients
                .map { ingredient -> Ingredient(ingredient.name, ingredient.measure) }
                .toSet()

        val recipeDoc = Recipe(
                id = uuid.toString(),
                title = recipeDto.title,
                tags = recipeDto.listOfTags,
                recipe = recipeDto.recipe,
                ingredients = setOfIngredients)

        LOGGER.info("opr=createRecipe, msg='Create recipe', recipe={}", recipeDoc)

        return recipeRepository.insert(recipeDoc)
    }

    fun searchRecipe(@NonNull id: UUID): Optional<Recipe> {

        LOGGER.info("opr=searchRecipe, msg='Search recipe', uuid={}", id)

        val recipe = recipeRepository.findById(id.toString())

        LOGGER.info("opr=searchRecipe, msg='Recipe returned', recipe={}", recipe)

        return recipe

    }

    fun getRandomRecipe(@NonNull listOfIngredients: Set<String>): Optional<Recipe> {

        var recipe: Optional<Recipe> = Optional.empty()
        val allRecipes = recipeRepository.findAll()

        LOGGER.debug("opr=getRandomRecipe, msg='Number of recipes', size={}", allRecipes.size)

        if (!allRecipes.isNullOrEmpty()) {

            recipe = Optional.ofNullable(allRecipes
                    .filter {
                        val ingredientList = it.ingredients
                                .map { ingredient -> ingredient.name }
                                .toSet()

                        ingredientList.containsAll(listOfIngredients)
                    }
                    .randomOrNull())
        }

        LOGGER.info("opr=getRandomRecipe, msg='Recipe returned', recipe={}", recipe)

        return recipe
    }

}