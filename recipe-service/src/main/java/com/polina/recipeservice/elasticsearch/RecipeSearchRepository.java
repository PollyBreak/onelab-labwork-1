package com.polina.recipeservice.elasticsearch;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeSearchRepository extends ElasticsearchRepository<RecipeDocument, String> {
}
