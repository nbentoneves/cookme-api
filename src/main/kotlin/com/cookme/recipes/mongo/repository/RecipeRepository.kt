package com.cookme.recipes.mongo.repository

import com.cookme.recipes.mongo.documents.Recipe
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository : MongoRepository<Recipe, String>