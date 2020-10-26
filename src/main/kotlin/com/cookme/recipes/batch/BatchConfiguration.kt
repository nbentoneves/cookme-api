package com.cookme.recipes.batch

import com.cookme.recipes.logic.RecipeLogic
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableBatchProcessing
class BatchConfiguration(private val jobBuilderFactory: JobBuilderFactory,
                         private val stepBuilderFactory: StepBuilderFactory) {

    @Value("\${spring.batch.job.recipe.endpoint}")
    private lateinit var url: String

    @Value("\${spring.batch.job.thread.pattern.name}")
    private lateinit var threadPatternName: String

    @Bean("recipeJobLauncher")
    fun jobLauncher(jobRepository: JobRepository): JobLauncher {
        val jobLauncher = RecipeLauncher(jobRepository, this.threadPatternName)
        jobLauncher.afterPropertiesSet()
        return jobLauncher
    }

    @Bean
    fun reader(restTemplate: RestTemplate): Step {
        return this.stepBuilderFactory
                .get("recipeReader")
                .tasklet(RecipeBatchReader(restTemplate, this.url))
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