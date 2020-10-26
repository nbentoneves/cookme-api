package com.cookme.recipes.api

import com.cookme.recipes.scheduler.SchedulerConfiguration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tools")
class ToolsController(@Autowired private val schedulerConfiguration: SchedulerConfiguration) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ToolsController::class.java)
    }

    /**
     * Request must be done like: /tools/enableRecipeJob
     *
     */
    @GetMapping("/enableRecipeJob")
    fun enableRecipeJob(): ResponseEntity<Boolean> {

        LOGGER.info("opr=enableRecipeJob, msg='Enable parse recipe job'")

        schedulerConfiguration.setIsJobEnable(true)
        return ResponseEntity(schedulerConfiguration.getIsJobEnable(), HttpStatus.OK)

    }

    /**
     * Request must be done like: /tools/disableRecipeJob
     *
     */
    @GetMapping("/disableRecipeJob")
    fun disableRecipeJob(): ResponseEntity<Boolean> {

        LOGGER.info("opr=disableRecipeJob, msg='Disable parse recipe job'")

        schedulerConfiguration.setIsJobEnable(false)
        return ResponseEntity(schedulerConfiguration.getIsJobEnable(), HttpStatus.OK)

    }

}