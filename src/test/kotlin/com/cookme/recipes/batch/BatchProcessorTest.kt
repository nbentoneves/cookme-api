package com.cookme.recipes.batch

import com.cookme.recipes.batch.domain.Ingredient
import com.cookme.recipes.batch.domain.Recipe
import com.cookme.recipes.domain.IngredientDTO
import com.cookme.recipes.domain.RecipeDTO
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import org.junit.jupiter.api.Assertions
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

class BatchProcessorTest {

    @MockK
    private lateinit var stepExecution: StepExecution

    @MockK
    private lateinit var jobExecution: JobExecution

    @SpyK
    private var executionContext: ExecutionContext = ExecutionContext()

    @MockK
    private lateinit var contribution: StepContribution

    @MockK
    private lateinit var chunkContext: ChunkContext

    private val recipeBatchProcessor: BatchProcessor = BatchProcessor()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { stepExecution.jobExecution } returns jobExecution
        every { jobExecution.executionContext } returns executionContext
    }

    @Test
    fun `Fails when no exists recipe`() {

        every { executionContext.get("recipe") } returns null

        recipeBatchProcessor.beforeStep(stepExecution)
        val status = recipeBatchProcessor.execute(contribution, chunkContext)
        val statusAfterStep = recipeBatchProcessor.afterStep(stepExecution)

        verify(exactly = 0) { executionContext.put("recipeDTO", any()) }

        assertEquals(RepeatStatus.FINISHED, status)
        assertEquals(ExitStatus.COMPLETED, statusAfterStep)

    }

    @Test
    fun `Process a valid Recipe`() {

        val instructions = "Heat oven to 200C/180C fan/ gas 6. Cook the vegetables in a casserole dish for 15 mins. Tip in the beans and " +
                "tomatoes, season, and cook for another 10-15 mins until piping hot. Heat the pouch in the microwave on High for 1 min and serve with the chilli."

        val recipe = Recipe("52867", "Vegetarian Chilli", "Vegetarian", "British",
                instructions,
                setOf("Chili"), "https://www.youtube.com/watch?v=D0bFRVH_EqU", "https://www.bbcgoodfood.com/recipes/veggie-chilli",
                setOf(Ingredient("Roasted Vegetables", "400g"),
                        Ingredient("Kidney Beans", "1 can"),
                        Ingredient("Chopped Tomatoes", "1 can"),
                        Ingredient("Mixed Grain", "1 Packet")))

        every { executionContext.get("recipe") } returns recipe

        recipeBatchProcessor.beforeStep(stepExecution)
        val status = recipeBatchProcessor.execute(contribution, chunkContext)
        val statusAfterStep = recipeBatchProcessor.afterStep(stepExecution)

        val recipeDTO = RecipeDTO(
                "Vegetarian Chilli", "52867",
                setOf(IngredientDTO("Roasted Vegetables", "400g"),
                        IngredientDTO("Kidney Beans", "1 can"),
                        IngredientDTO("Chopped Tomatoes", "1 can"),
                        IngredientDTO("Mixed Grain", "1 Packet")),
                setOf("Chili"), instructions)

        verify(exactly = 1) { executionContext.put("recipeDTO", recipeDTO) }

        assertEquals(RepeatStatus.FINISHED, status)
        assertEquals(ExitStatus.COMPLETED, statusAfterStep)

    }

}