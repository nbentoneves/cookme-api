package com.cookme.recipes.scheduler

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled


@Configuration
@EnableScheduling
class SchedulerConfiguration(private val recipeJobLauncher: JobLauncher,
                             private val jobParseRecipes: Job) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SchedulerConfiguration::class.java)
    }

    private var isJobEnable: Boolean = true

    @Scheduled(cron = "\${spring.batch.job.recipe.cronjob}")
    fun recipeJob() {

        if (isJobEnable) {
            LOGGER.info("opr=recipeJob, msg='Running recipe job'")

            recipeJobLauncher.run(jobParseRecipes, JobParameters())
        } else {
            LOGGER.info("opr=recipeJob, msg='Job is disable'")
        }
    }

    public fun setIsJobEnable(flag: Boolean) {
        this.isJobEnable = flag
    }

    public fun getIsJobEnable(): Boolean {
        return this.isJobEnable
    }

}