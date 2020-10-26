package com.cookme.recipes.batch

import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

class RecipeLauncher(jobRepository: JobRepository,
                     taskExecutorName: String) : SimpleJobLauncher() {

    init {
        val threadPoolTaskExecutor = ThreadPoolTaskExecutor()
        threadPoolTaskExecutor.setThreadNamePrefix(taskExecutorName)

        val taskExecutor = SimpleAsyncTaskExecutor(threadPoolTaskExecutor)

        this.setTaskExecutor(taskExecutor)
        this.setJobRepository(jobRepository)
    }

}