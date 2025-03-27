package com.polina.recipeservice.elasticsearch;

import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class RecipeSearchService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final RecipeSearchRepository recipeSearchRepository;

    public RecipeSearchService(ElasticsearchOperations elasticsearchOperations, RecipeSearchRepository recipeSearchRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.recipeSearchRepository = recipeSearchRepository;
    }

    public Optional<RecipeDocument> findRecipeById(String recipeId) {
        return recipeSearchRepository.findById(recipeId);
    }


    public Page<RecipeDocument> searchRecipes(String authorId, String title, String cuisine,
                                              Double minRating, String description, List<String> products,
                                              int page, int size, String sortBy) {
        Pageable pageable = getPageable(page, size, sortBy);
        Criteria criteria = new Criteria();
        if (title != null) {
            criteria = criteria.and("title").matches(title);}
        if (description != null) {
            criteria = criteria.and("description").matches(description);}
        if (authorId != null) {
            criteria = criteria.and("authorId").is(authorId);}
        if (cuisine != null) {
            criteria = criteria.and("cuisine").is(cuisine);}
        if (minRating != null) {
            criteria = criteria.and("averageRating").greaterThanEqual(minRating);}
        if (products != null && !products.isEmpty()) {
            Criteria productCriteria = new Criteria();
            for (String product : products) {
                productCriteria = productCriteria.and(new Criteria("products").matches(product));}
            criteria = criteria.and(productCriteria);
        }
        CriteriaQuery searchQuery = new CriteriaQuery(criteria).setPageable(pageable);
        return executeSearchQuery(searchQuery, pageable);
    }

    public Map<String, List<RecipeDocument>> groupRecipesByCuisine() {
        CriteriaQuery searchQuery = new CriteriaQuery(new Criteria());
        SearchHits<RecipeDocument> searchHits = elasticsearchOperations.search(searchQuery, RecipeDocument.class);
        List<RecipeDocument> allRecipes = searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
        return allRecipes.stream().collect(Collectors.groupingBy(RecipeDocument::getCuisine));
    }

    private Page<RecipeDocument> executeSearchQuery(CriteriaQuery searchQuery, Pageable pageable) {
        SearchHits<RecipeDocument> searchHits = elasticsearchOperations.search(searchQuery, RecipeDocument.class);
        List<RecipeDocument> recipes = searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
        return new PageImpl<>(recipes, pageable, searchHits.getTotalHits());
    }

    private Pageable getPageable(int page, int size, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, "averageRating");
        if ("newest".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return PageRequest.of(page, size, sort);
    }
}
