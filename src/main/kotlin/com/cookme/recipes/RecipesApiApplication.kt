package com.cookme.recipes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.web.client.RestTemplate

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@EnableMongoRepositories(basePackages = ["com.cookme.recipes.mongo.repository"])
class RecipesApiApplication {

    @Bean("restTemplate")
    fun getRestTemplate(): RestTemplate {
        return RestTemplate()
    }

}

fun main(args: Array<String>) {
    runApplication<RecipesApiApplication>(*args)
}
