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
                         private val stepBuilderFactory: StepBuilderFactory) {

    @Bean
    fun reader(restTemplate: RestTemplate): Step {
        //FIXME: Change the hardcoded url to a dynamic one
        return this.stepBuilderFactory
                .get("recipeReader")
                .tasklet(RecipeBatchReader(restTemplate, "https://www.themealdb.com/api/json/v1/1/random.php"))
                .allowStartIfComplete(true)
                .build()
    }

    @Bean
    fun processor(): Step {
        return this.stepBuilderFactory
                .get("recipeProcessor")
                .tasklet(BatchProcessor())
                .allowStartIfComplete(true)
                .build()
    }

    @Bean
    fun writer(recipeLogic: RecipeLogic): Step {
        return this.stepBuilderFactory
                .get("recipeWriter")
                .tasklet(RecipeBatchWriter(recipeLogic))
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