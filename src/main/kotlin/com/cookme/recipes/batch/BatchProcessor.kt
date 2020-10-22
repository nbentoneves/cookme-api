package com.cookme.recipes.batch

import com.cookme.recipes.batch.domain.Recipe
import com.cookme.recipes.domain.IngredientDTO
import com.cookme.recipes.domain.RecipeDTO
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import java.util.*

class BatchProcessor : Tasklet, StepExecutionListener {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(BatchProcessor::class.java)
    }

    private lateinit var recipeDTO: Optional<RecipeDTO>
    private lateinit var recipe: Optional<Recipe>

    override fun beforeStep(stepExecution: StepExecution) {

        this.recipeDTO = Optional.empty()
        this.recipe = Optional.empty()

        val value = stepExecution.jobExecution
                .executionContext
                .get("recipe")

        if (value !== null) {
            this.recipe = Optional.of(value as Recipe)
        }
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        LOGGER.info("opr=process, msg='Process the Recipe to RecipeDTO info'")

        if (this.recipe.isPresent) {
            val setOfIngredients = this.recipe.get().ingredients
                    .map { ingredient -> IngredientDTO(ingredient.name, ingredient.measure) }
                    .toSet()

            this.recipeDTO = Optional.of(RecipeDTO(this.recipe.get().title, setOfIngredients,
                    this.recipe.get().tags, this.recipe.get().instructions))
        }

        return RepeatStatus.FINISHED
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {

        if (this.recipeDTO.isPresent) {
            stepExecution.jobExecution
                    .executionContext
                    .put("recipeDTO", this.recipeDTO.get())
        }

        return ExitStatus.COMPLETED
    }
}