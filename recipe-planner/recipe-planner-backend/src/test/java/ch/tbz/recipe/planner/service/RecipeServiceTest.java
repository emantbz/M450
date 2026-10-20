package ch.tbz.recipe.planner.service;

import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.entities.RecipeEntity;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testet den RecipeService mit gemocktem Repository und echtem Mapper.
 */
class RecipeServiceTest {

    private final RecipeEntityMapper mapper = Mappers.getMapper(RecipeEntityMapper.class);
    private RecipeRepository repository;
    private RecipeService service;

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {
        repository = mock(RecipeRepository.class);
        service = new RecipeService(mapper, repository);
    }

    @Test
    @DisplayName("getRecipes mappt alle Entities zu Domain-Objekten")
    void getRecipes() {
        when(repository.findAll()).thenReturn(List.of(
                new RecipeEntity(ID, "Lasagne", null, null, List.of()),
                new RecipeEntity(UUID.randomUUID(), "Risotto", null, null, List.of())));

        assertThat(service.getRecipes()).extracting(Recipe::getName).containsExactly("Lasagne", "Risotto");
    }

    @Test
    @DisplayName("getRecipeById findet das Rezept")
    void getRecipeById() {
        when(repository.findById(ID)).thenReturn(Optional.of(new RecipeEntity(ID, "Lasagne", null, null, List.of())));

        assertThat(service.getRecipeById(ID).getName()).isEqualTo("Lasagne");
    }

    @Test
    @DisplayName("getRecipeById liefert null, wenn es die ID nicht gibt")
    void getRecipeByIdNotFound() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThat(service.getRecipeById(UUID.randomUUID())).isNull();
    }

    @Test
    @DisplayName("addRecipe speichert und liefert das gespeicherte Rezept zurück")
    void addRecipe() {
        when(repository.save(any(RecipeEntity.class))).thenAnswer(inv -> {
            RecipeEntity e = inv.getArgument(0);
            e.setId(ID); // die DB vergibt die ID
            return e;
        });

        Recipe saved = service.addRecipe(new Recipe(null, "Neu", "Beschreibung", "url", List.of()));

        assertThat(saved.getId()).isEqualTo(ID);
        assertThat(saved.getName()).isEqualTo("Neu");
        verify(repository).save(any(RecipeEntity.class));
    }
}
