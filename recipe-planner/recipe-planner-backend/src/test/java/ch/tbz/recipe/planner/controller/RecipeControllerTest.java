package ch.tbz.recipe.planner.controller;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.repository.RecipeRepository;
import ch.tbz.recipe.planner.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testet den RecipeController isoliert mit MockMvc.
 * Der Service wird gemockt, es wird also nur die Web-Schicht getestet (URLs, Status, JSON).
 */
@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecipeService service;

    @MockBean
    private RecipeEntityMapper mapper;

    // wird vom CommandLineRunner in RecipePlannerApplication (Testdaten) benötigt
    @MockBean
    private RecipeRepository repository;

    private static final UUID RECIPE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID INGREDIENT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private static Recipe lasagne() {
        Ingredient tomato = new Ingredient(INGREDIENT_ID, "Tomato", "The big ones", Unit.PIECE, 5);
        return new Recipe(RECIPE_ID, "Lasagne al Forno", "Lecker", "https://example.com/lasagne.jpg", List.of(tomato));
    }

    @Test
    @DisplayName("GET /api/recipes liefert alle Rezepte als JSON")
    void getRecipes() throws Exception {
        Recipe second = new Recipe(UUID.randomUUID(), "Fried Rice", "Asiatisch", "url", List.of());
        when(service.getRecipes()).thenReturn(List.of(lasagne(), second));

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(RECIPE_ID.toString()))
                .andExpect(jsonPath("$[0].name").value("Lasagne al Forno"))
                .andExpect(jsonPath("$[0].ingredients", hasSize(1)))
                .andExpect(jsonPath("$[0].ingredients[0].unit").value("PIECE"))
                .andExpect(jsonPath("$[1].name").value("Fried Rice"));

        verify(service).getRecipes();
    }

    @Test
    @DisplayName("GET /api/recipes ohne Rezepte liefert eine leere Liste")
    void getRecipesEmpty() throws Exception {
        when(service.getRecipes()).thenReturn(List.of());

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/recipes/recipe/{id} liefert das Rezept")
    void getRecipe() throws Exception {
        when(service.getRecipeById(RECIPE_ID)).thenReturn(lasagne());

        mockMvc.perform(get("/api/recipes/recipe/{id}", RECIPE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RECIPE_ID.toString()))
                .andExpect(jsonPath("$.name").value("Lasagne al Forno"))
                .andExpect(jsonPath("$.description").value("Lecker"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/lasagne.jpg"))
                .andExpect(jsonPath("$.ingredients[0].name").value("Tomato"))
                .andExpect(jsonPath("$.ingredients[0].amount").value(5));

        verify(service).getRecipeById(RECIPE_ID);
    }

    @Test
    @DisplayName("GET /api/recipes/recipe/{id} mit unbekannter ID liefert 200 ohne Inhalt (aktuelles Verhalten)")
    void getRecipeNotFound() throws Exception {
        UUID unknown = UUID.randomUUID();
        when(service.getRecipeById(unknown)).thenReturn(null);

        mockMvc.perform(get("/api/recipes/recipe/{id}", unknown))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("GET /api/recipes/recipe/{id} mit ungültiger UUID liefert 400")
    void getRecipeInvalidId() throws Exception {
        mockMvc.perform(get("/api/recipes/recipe/{id}", "keine-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST /api/recipes speichert das Rezept und liefert es zurück")
    void addRecipe() throws Exception {
        Recipe input = lasagne();
        input.setId(null);
        when(service.addRecipe(any(Recipe.class))).thenReturn(lasagne());

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RECIPE_ID.toString()))
                .andExpect(jsonPath("$.name").value("Lasagne al Forno"));

        // prüfen, dass der Controller das JSON korrekt an den Service weitergibt
        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);
        verify(service).addRecipe(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getName()).isEqualTo("Lasagne al Forno");
        assertThat(captor.getValue().getIngredients()).hasSize(1);
        assertThat(captor.getValue().getIngredients().get(0).getUnit()).isEqualTo(Unit.PIECE);
    }

    @Test
    @DisplayName("POST /api/recipes mit ungültigem JSON liefert 400")
    void addRecipeInvalidJson() throws Exception {
        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ kein json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST /api/recipes mit unbekannter Einheit liefert 400")
    void addRecipeInvalidUnit() throws Exception {
        String json = """
                { "name": "Test", "ingredients": [ { "name": "Mehl", "unit": "TONNEN", "amount": 1 } ] }
                """;

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CORS: Frontend auf localhost:3000 darf zugreifen")
    void corsAllowsFrontend() throws Exception {
        when(service.getRecipes()).thenReturn(List.of());

        mockMvc.perform(get("/api/recipes").header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    @DisplayName("CORS: fremde Origin wird abgelehnt")
    void corsRejectsOtherOrigin() throws Exception {
        mockMvc.perform(get("/api/recipes").header("Origin", "http://evil.example.com"))
                .andExpect(status().isForbidden());
    }
}
