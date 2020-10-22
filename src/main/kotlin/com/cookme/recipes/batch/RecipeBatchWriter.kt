package com.cookme.recipes.batch

import com.cookme.recipes.domain.RecipeDTO
import com.cookme.recipes.logic.RecipeLogic
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import java.util.*

class RecipeBatchWriter(private val recipeLogic: RecipeLogic) : Tasklet, StepExecutionListener {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RecipeBatchWriter::class.java)
    }

    private lateinit var recipe: Optional<RecipeDTO>

    override fun beforeStep(stepExecution: StepExecution) {

        this.recipe = Optional.empty()

        val value = stepExecution.jobExecution
                .executionContext
                .get("recipeDTO")

        if (value !== null) {
            this.recipe = Optional.of(value as RecipeDTO)
        }

    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {

        if (this.recipe.isPresent) {
            LOGGER.info("opr=write, msg='Writing the recipe into database'")

            //FIXME: Create internal id to reference with website (avoid repeated recipes)
            val recipe = recipeLogic.createRecipe(this.recipe.get(), UUID.randomUUID())

            LOGGER.info("opr=write, msg='Recipe wrote database', recipe={}", recipe)
        }

        return RepeatStatus.FINISHED
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {
        return ExitStatus.COMPLETED
    }

}