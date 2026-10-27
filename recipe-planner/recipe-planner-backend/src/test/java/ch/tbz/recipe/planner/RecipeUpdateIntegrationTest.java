package ch.tbz.recipe.planner;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testet das Bearbeiten eines Rezepts mit der echten Datenbank (H2),
 * damit geprüft wird, dass auch die Zutaten korrekt geändert, hinzugefügt und gelöscht werden.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RecipeUpdateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeService service;

    @Test
    @DisplayName("Rezept bearbeiten: Felder ändern, Zutat ändern, Zutat löschen, Zutat hinzufügen")
    void updateRecipeWithIngredients() throws Exception {
        Recipe created = service.addRecipe(new Recipe(null, "Pfannkuchen", "Alt", "url", List.of(
                new Ingredient(null, "Mehl", null, Unit.GRAMM, 200),
                new Ingredient(null, "Zucker", null, Unit.GRAMM, 50))));
        Ingredient mehl = created.getIngredients().stream().filter(i -> i.getName().equals("Mehl")).findFirst().orElseThrow();

        mehl.setAmount(300);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(mehl);                                            // geändert
        ingredients.add(new Ingredient(null, "Milch", null, Unit.DECILITRE, 5)); // neu, Zucker fehlt -> gelöscht
        Recipe changed = new Recipe(created.getId(), "Pfannkuchen deluxe", "Neu", "url2", ingredients);

        mockMvc.perform(put("/api/recipes/recipe/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changed)))
                .andExpect(status().isOk());

        Recipe stored = service.getRecipeById(created.getId());
        assertThat(stored.getName()).isEqualTo("Pfannkuchen deluxe");
        assertThat(stored.getDescription()).isEqualTo("Neu");
        assertThat(stored.getImageUrl()).isEqualTo("url2");
        assertThat(stored.getIngredients())
                .extracting(Ingredient::getName, Ingredient::getAmount)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("Mehl", 300),
                        org.assertj.core.groups.Tuple.tuple("Milch", 5));
        assertThat(stored.getIngredients()).filteredOn(i -> i.getName().equals("Mehl"))
                .extracting(Ingredient::getId).containsExactly(mehl.getId());
    }

    @Test
    @DisplayName("Rezept mit unbekannter ID bearbeiten liefert 404 und legt nichts an")
    void updateUnknownRecipe() throws Exception {
        int before = service.getRecipes().size();

        mockMvc.perform(put("/api/recipes/recipe/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Gibt es nicht\" }"))
                .andExpect(status().isNotFound());

        assertThat(service.getRecipes()).hasSize(before);
    }
}
