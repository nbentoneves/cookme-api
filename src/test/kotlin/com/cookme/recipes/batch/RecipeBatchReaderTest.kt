package com.cookme.recipes.batch

import com.cookme.recipes.batch.domain.Ingredient
import com.cookme.recipes.batch.domain.Recipe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class RecipeBatchReaderTest {

    @MockK
    private lateinit var stepExecution: StepExecution

    @MockK
    private lateinit var restTemplate: RestTemplate

    @MockK
    private lateinit var jobExecution: JobExecution

    @SpyK
    private var executionContext: ExecutionContext = ExecutionContext()

    @MockK
    private lateinit var contribution: StepContribution

    @MockK
    private lateinit var chunkContext: ChunkContext

    private lateinit var recipeBatchReader: RecipeBatchReader

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        recipeBatchReader = RecipeBatchReader(restTemplate, "endpoint")

        every { stepExecution.jobExecution } returns jobExecution
        every { jobExecution.executionContext } returns executionContext
    }

    @Test
    fun `Empty recipe when http request fails`() {

        every { restTemplate.getForEntity("endpoint", String::class.java) } returns
                ResponseEntity(HttpStatus.BAD_REQUEST)

        recipeBatchReader.beforeStep(stepExecution)
        val status = recipeBatchReader.execute(contribution, chunkContext)
        val statusAfterStep = recipeBatchReader.afterStep(stepExecution)

        verify(exactly = 0) { executionContext.put("recipe", any()) }

        assertEquals(RepeatStatus.FINISHED, status)
        assertEquals(ExitStatus.COMPLETED, statusAfterStep)
    }

    @Test
    fun `Fails when recipe has invalid ingredients parameter`() {

        val jsonData = this::class.java.getResource("/recipes/jsonRecipeInvalidIngredients.json")
                .readText()

        every { restTemplate.getForEntity("endpoint", String::class.java) } returns
                ResponseEntity(jsonData, HttpStatus.OK)

        recipeBatchReader.beforeStep(stepExecution)
        val status = recipeBatchReader.execute(contribution, chunkContext)
        val statusAfterStep = recipeBatchReader.afterStep(stepExecution)

        verify(exactly = 0) { executionContext.put("recipe", any()) }

        assertEquals(RepeatStatus.FINISHED, status)
        assertEquals(ExitStatus.COMPLETED, statusAfterStep)

    }

    @Test
    fun `Get a valid Recipe`() {

        val jsonData = this::class.java.getResource("/recipes/jsonRecipe.json")
                .readText()

        every { restTemplate.getForEntity("endpoint", String::class.java) } returns
                ResponseEntity(jsonData, HttpStatus.OK)

        val recipe = Recipe("52867", "Vegetarian Chilli", "Vegetarian", "British",
                "Heat oven to 200C/180C fan/ gas 6. Cook the vegetables in a casserole dish for 15 mins. Tip in the beans and " +
                        "tomatoes, season, and cook for another 10-15 mins until piping hot. Heat the pouch in the microwave on High for 1 min and serve with the chilli.",
                setOf("Chili"), "https://www.youtube.com/watch?v=D0bFRVH_EqU", "https://www.bbcgoodfood.com/recipes/veggie-chilli",
                setOf(Ingredient("Roasted Vegetables", "400g"),
                        Ingredient("Kidney Beans", "1 can"),
                        Ingredient("Chopped Tomatoes", "1 can"),
                        Ingredient("Mixed Grain", "1 Packet")))

        recipeBatchReader.beforeStep(stepExecution)
        val status = recipeBatchReader.execute(contribution, chunkContext)
        val statusAfterStep = recipeBatchReader.afterStep(stepExecution)

        verify(exactly = 1) { executionContext.put("recipe", recipe) }

        assertEquals(RepeatStatus.FINISHED, status)
        assertEquals(ExitStatus.COMPLETED, statusAfterStep)

    }
}