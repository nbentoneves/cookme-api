package com.cookme.recipes.batch

import com.cookme.recipes.batch.domain.Ingredient
import com.cookme.recipes.batch.domain.Recipe
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.*

class RecipeBatchReader(private val restTemplate: RestTemplate,
                        private val url: String) : Tasklet, StepExecutionListener {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RecipeBatchReader::class.java)

        private const val FIELD_ID_MEAL = "idMeal"
        private const val FIELD_STR_MEAL = "strMeal"
        private const val FIELD_STR_CATEGORY = "strCategory"
        private const val FIELD_STR_AREA = "strArea"
        private const val FIELD_STR_INSTRUCTIONS = "strInstructions"
        private const val FIELD_STR_THUMB = "strMealThumb"
        private const val FIELD_STR_TAGS = "strTags"
        private const val FIELD_STR_YOUTUBE = "strYoutube"
        private const val FIELD_STR_INGREDIENT = "strIngredient"
        private const val FIELD_STR_MEASURE = "strMeasure"
        private const val FIELD_STR_SOURCE = "strSource"

    }

    private lateinit var recipe: Optional<Recipe>

    override fun beforeStep(stepExecution: StepExecution) {
        this.recipe = Optional.empty()
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        LOGGER.info("opr=read, msg='Starting read data'")

        try {
            val recipe = Optional.empty<Recipe>()
            //val recipe = fetchData()

            if (recipe.isPresent) {
                LOGGER.info("opr=read, msg='Finished read data', recipeId={}", recipe.get().id)
                this.recipe = Optional.of(recipe.get())
            } else {
                LOGGER.warn("For some reason the system can not collect any recipe, please check the code")
            }

        } catch (ex: Exception) {
            LOGGER.error("Something wrong happened", ex)
        }

        return RepeatStatus.FINISHED
    }

    private fun fetchData(): Optional<Recipe> {
        val response: ResponseEntity<String> = restTemplate.getForEntity(url, String::class.java)

        LOGGER.debug("opr=fetchData, msg='Data retrieved from url', data={}", response)

        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(response.body)
        val meals: JsonNode = root.path("meals")

        if (meals.isMissingNode || meals[0] == null) {
            return Optional.empty()
        }

        val meal = meals[0]

        val recipe = Recipe(meal.get(FIELD_ID_MEAL).asText(),
                meal.get(FIELD_STR_MEAL).asText(),
                meal.get(FIELD_STR_CATEGORY).asText(),
                meal.get(FIELD_STR_AREA).asText(),
                meal.get(FIELD_STR_INSTRUCTIONS).asText(),
                extractTags(meal.get(FIELD_STR_TAGS)),
                meal.get(FIELD_STR_YOUTUBE).asText(),
                meal.get(FIELD_STR_SOURCE).asText(),
                extractIngredients(meal))

        return Optional.of(recipe)
    }

    private fun extractIngredients(meal: JsonNode): Set<Ingredient> {

        var indexIngredient = 1
        val ingredients = mutableSetOf<Ingredient>()

        while (meal.get(FIELD_STR_INGREDIENT + indexIngredient).asText().isNotBlank()) {

            val ingredient = Ingredient(
                    meal.get(FIELD_STR_INGREDIENT + indexIngredient).asText(),
                    meal.get(FIELD_STR_MEASURE + indexIngredient).asText())

            ingredients.add(ingredient)

            indexIngredient++
        }

        return ingredients

    }

    private fun extractTags(tagsNode: JsonNode): Set<String> {

        return tagsNode.asText()
                .split(",")
                .toSet()

    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {

        if (this.recipe.isPresent) {
            stepExecution
                    .jobExecution
                    .executionContext
                    .put("recipe", this.recipe.get())
        }

        return ExitStatus.COMPLETED
    }

}
