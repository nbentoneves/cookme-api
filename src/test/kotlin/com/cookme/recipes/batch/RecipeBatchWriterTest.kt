package com.cookme.recipes.batch

import com.cookme.recipes.domain.IngredientDTO
import com.cookme.recipes.domain.RecipeDTO
import com.cookme.recipes.logic.RecipeLogic
import com.cookme.recipes.mongo.documents.Recipe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.repeat.RepeatStatus

class RecipeBatchWriterTest {

    @MockK
    private lateinit var stepExecution: StepExecution

    @MockK
    private lateinit var jobExecution: JobExecution

    @MockK
    private lateinit var executionContext: ExecutionContext

    @MockK
    private lateinit var contribution: StepContribution

    @MockK
    private lateinit var chunkContext: ChunkContext

    @MockK
    private lateinit var recipeLogic: RecipeLogic

    @MockK
    private lateinit var recipe: Recipe

    private lateinit var recipeBatchWriter: RecipeBatchWriter

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        recipeBatchWriter = RecipeBatchWriter(recipeLogic)

        every { stepExecution.jobExecution } returns jobExecution
        every { jobExecution.executionContext } returns executionContext
    }

    @Test
    fun `Fails when no exists recipe domain`() {

        every { executionContext.get("recipeDTO") } returns null

        recipeBatchWriter.beforeStep(stepExecution)
        val status = recipeBatchWriter.execute(contribution, chunkContext)
        val statusAfterStep = recipeBatchWriter.afterStep(stepExecution)

        verify(exactly = 0) { recipeLogic.createRecipe(any(), any()) }

        assertEquals(RepeatStatus.FINISHED, status)
        assertEquals(ExitStatus.COMPLETED, statusAfterStep)

    }

    @Test
    fun `Process a valid Recipe`() {

        val instructions = "Heat oven to 200C/180C fan/ gas 6. Cook the vegetables in a casserole dish for 15 mins. Tip in the beans and " +
                "tomatoes, season, and cook for another 10-15 mins until piping hot. Heat the pouch in the microwave on High for 1 min and serve with the chilli."

        val recipeDTO = RecipeDTO(
                "Vegetarian Chilli", "52867",
                setOf(IngredientDTO("Roasted Vegetables", "400g"),
                        IngredientDTO("Kidney Beans", "1 can"),
                        IngredientDTO("Chopped Tomatoes", "1 can"),
                        IngredientDTO("Mixed Grain", "1 Packet")),
                setOf("Chili"), instructions)

        every { executionContext.get("recipeDTO") } returns recipeDTO
        every { recipeLogic.createRecipe(recipeDTO, any()) } returns recipe

        recipeBatchWriter.beforeStep(stepExecution)
        val status = recipeBatchWriter.execute(contribution, chunkContext)
        val statusAfterStep = recipeBatchWriter.afterStep(stepExecution)

        verify(exactly = 1) { recipeLogic.createRecipe(recipeDTO, any()) }

        assertEquals(RepeatStatus.FINISHED, status)
        assertEquals(ExitStatus.COMPLETED, statusAfterStep)

    }

}