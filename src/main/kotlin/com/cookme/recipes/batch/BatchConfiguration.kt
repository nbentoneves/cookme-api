package com.cookme.recipes.batch

import com.cookme.recipes.logic.RecipeLogic
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableBatchProcessing
class BatchConfiguration(private val jobBuilderFactory: JobBuilderFactory,
                         private val stepBuilderFactory: StepBuilderFactory,
                         private val recipeBatchReader: RecipeBatchReader,
                         private val recipeBatchProcessor: BatchProcessor,
                         private val recipeBatchWriter: RecipeBatchWriter,
                         private val recipeLogic: RecipeLogic) {

    @Bean
    fun recipeBatchReader(restTemplate: RestTemplate): RecipeBatchReader {
        return RecipeBatchReader(restTemplate, "https://www.themealdb.com/api/json/v1/1/random.php")
    }

    @Bean
    fun recipeBatchProcessor(): BatchProcessor {
        return BatchProcessor()
    }

    @Bean
    fun recipeBatchWriter(): RecipeBatchWriter {
        return RecipeBatchWriter(recipeLogic)
    }

    @Bean
    fun reader(): Step {
        //FIXME: Change the hardcoded url to a dynamic one
        return this.stepBuilderFactory
                .get("recipeReader")
                .tasklet(recipeBatchReader)
                .allowStartIfComplete(true)
                .build()
    }

    @Bean
    fun processor(): Step {
        return this.stepBuilderFactory
                .get("recipeProcessor")
                .tasklet(recipeBatchProcessor)
                .allowStartIfComplete(true)
                .build()
    }

    @Bean
    fun writer(): Step {
        return this.stepBuilderFactory
                .get("recipeWriter")
                .tasklet(recipeBatchWriter)
                .allowStartIfComplete(true)
                .build()
    }

    @Bean("jobParseRecipes")
    fun job(reader: Step, writer: Step, processor: Step): Job {
        return jobBuilderFactory.get("parseRecipe")
                .incrementer(RunIdIncrementer())
                .start(reader)
                .next(processor)
                .next(writer)
                .build()
    }

}