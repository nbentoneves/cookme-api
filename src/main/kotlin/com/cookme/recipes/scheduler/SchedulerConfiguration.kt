package com.cookme.recipes.scheduler

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled


@Configuration
@EnableScheduling
class SchedulerConfiguration(private val jobLauncher: JobLauncher,
                             private val jobParseRecipes: Job) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SchedulerConfiguration::class.java)
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    @Throws(Exception::class)
    fun recipeJob() {

        //FIXME: This should run in a different thread
        LOGGER.info("opr=recipeJob, msg='Running recipe job'")



        Thread {
            val jobExecution = jobLauncher.run(jobParseRecipes, JobParameters())

            jobExecution.status.batchStatus

        }.start()

    }


}