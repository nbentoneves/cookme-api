package com.cookme.recipes.logic

import com.cookme.recipes.domain.RecipeDTO
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

    fun createRecipe(@NonNull recipeDto: RecipeDTO): Recipe {

        val recipeDoc = Recipe(
                id = UUID.randomUUID().toString(),
                title = recipeDto.title,
                tags = recipeDto.listOfTags,
                recipe = recipeDto.recipe)

        LOGGER.info("opr=createRecipe, msg='Create recipe', recipe={}", recipeDoc)

        return recipeRepository.insert(recipeDoc)
    }

    fun searchRecipe(@NonNull id: UUID): Optional<Recipe> {

        LOGGER.info("opr=searchRecipe, msg='Search recipe', uuid={}", id)

        val recipe = recipeRepository.findById(id.toString())

        LOGGER.info("opr=searchRecipe, msg='Recipe returned', recipe={}", recipe)

        return recipe

    }

    fun getRandomRecipe(@NonNull listOfTags: Set<String>): Optional<Recipe> {

        var recipe: Optional<Recipe> = Optional.empty()
        val allRecipes = recipeRepository.findAll()

        LOGGER.debug("opr=getRandomRecipe, msg='Number of recipes', size={}", allRecipes.size)

        if (!allRecipes.isNullOrEmpty()) {
            recipe = Optional.ofNullable(allRecipes
                    .filter { recipeEntity -> recipeEntity.tags.containsAll(listOfTags) }
                    .randomOrNull())
        }

        LOGGER.info("opr=getRandomRecipe, msg='Recipe returned', recipe={}", recipe)

        return recipe
    }

}