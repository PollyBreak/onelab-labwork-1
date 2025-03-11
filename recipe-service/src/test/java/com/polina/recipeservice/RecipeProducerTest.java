package com.polina.recipeservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.kafka.RecipeProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeProducerTest {

    @InjectMocks
    private RecipeProducer recipeProducer;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        recipeProducer = new RecipeProducer();
        recipeProducer.kafkaTemplate = kafkaTemplate;
        recipeProducer.objectMapper = objectMapper;
    }

    @Test
    void sendRecipeCreated_Success() throws Exception {
        RecipeDTO recipe = RecipeDTO.builder().title("Test Recipe").build();

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"title\":\"Test Recipe\"}");

        recipeProducer.sendRecipeCreated(recipe);

        verify(kafkaTemplate).send(eq("recipe.created"), anyString());
    }

    @Test
    void sendRecipeCreated_Failure() throws Exception {
        RecipeDTO recipe = RecipeDTO.builder().title("Test Recipe").build();

        doThrow(new JsonProcessingException("Serialization error") {}).when(objectMapper).writeValueAsString(any());

        recipeProducer.sendRecipeCreated(recipe);

        verify(kafkaTemplate, never()).send(any(), anyString());
    }
}
