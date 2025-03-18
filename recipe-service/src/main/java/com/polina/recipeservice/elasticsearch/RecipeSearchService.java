package com.polina.recipeservice.elasticsearch;

import com.polina.recipeservice.elasticsearch.RecipeDocument;
import com.polina.recipeservice.elasticsearch.RecipeSearchRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeSearchService {
    private final RecipeSearchRepository recipeSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public RecipeSearchService(RecipeSearchRepository recipeSearchRepository,
                               ElasticsearchOperations elasticsearchOperations) {
        this.recipeSearchRepository = recipeSearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }


    public void saveRecipe(RecipeDocument recipe) {
        recipeSearchRepository.save(recipe);
    }


    public void deleteRecipe(String id) {
        recipeSearchRepository.deleteById(id);
    }

    public List<RecipeDocument> getAllRecipes() {
        return (List<RecipeDocument>) recipeSearchRepository.findAll();
    }

    public List<RecipeDocument> filterRecipesByTitle(String title) {
        Criteria criteria = new Criteria("title").matches(title);
        CriteriaQuery searchQuery = new CriteriaQuery(criteria);
        SearchHits<RecipeDocument> searchHits = elasticsearchOperations.search(searchQuery,
                RecipeDocument.class);
        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

   
    public List<RecipeDocument> filterRecipesByCuisineAndRating(String cuisine, Double minRating) {
        Criteria criteria = new Criteria();
        if (cuisine != null) {
            criteria = criteria.and("cuisine").matches(cuisine);
        }
        if (minRating != null) {
            criteria = criteria.and("averageRating").greaterThanEqual(minRating);
        }

        CriteriaQuery searchQuery = new CriteriaQuery(criteria);
        SearchHits<RecipeDocument> searchHits = elasticsearchOperations.search(searchQuery,
                RecipeDocument.class);
        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }
}
