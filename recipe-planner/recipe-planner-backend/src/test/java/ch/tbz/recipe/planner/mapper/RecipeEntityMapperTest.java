package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import ch.tbz.recipe.planner.entities.RecipeEntity;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Testet den RecipeEntityMapper (Recipe <-> RecipeEntity), inkl. der verschachtelten Zutaten.
 * Hier werden die SoftAssertions mit SoftAssertions.assertSoftly(...) verwendet:
 * alle Prüfungen im Block laufen durch, Fehler werden am Ende gesammelt gemeldet.
 */
class RecipeEntityMapperTest {

    private final RecipeEntityMapper mapper = Mappers.getMapper(RecipeEntityMapper.class);

    private static final UUID RECIPE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID INGREDIENT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    @DisplayName("Entity -> Domain: alle Felder und Zutaten werden übernommen")
    void entityToDomain() {
        IngredientEntity tomato = new IngredientEntity(INGREDIENT_ID, "Tomato", "The big ones", Unit.PIECE, 5);
        RecipeEntity entity = new RecipeEntity(RECIPE_ID, "Lasagne al Forno", "Lecker", "https://img/lasagne.jpg", List.of(tomato));

        Recipe recipe = mapper.entityToDomain(entity);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(recipe.getId()).isEqualTo(RECIPE_ID);
            softly.assertThat(recipe.getName()).isEqualTo("Lasagne al Forno");
            softly.assertThat(recipe.getDescription()).isEqualTo("Lecker");
            softly.assertThat(recipe.getImageUrl()).isEqualTo("https://img/lasagne.jpg");
            softly.assertThat(recipe.getIngredients()).hasSize(1);

            Ingredient ingredient = recipe.getIngredients().get(0);
            softly.assertThat(ingredient.getId()).isEqualTo(INGREDIENT_ID);
            softly.assertThat(ingredient.getName()).isEqualTo("Tomato");
            softly.assertThat(ingredient.getComment()).isEqualTo("The big ones");
            softly.assertThat(ingredient.getUnit()).isEqualTo(Unit.PIECE);
            softly.assertThat(ingredient.getAmount()).isEqualTo(5);
        });
    }

    @Test
    @DisplayName("Domain -> Entity: alle Felder und Zutaten werden übernommen")
    void domainToEntity() {
        Ingredient mehl = new Ingredient(INGREDIENT_ID, "Mehl", null, Unit.GRAMM, 500);
        Ingredient milch = new Ingredient(UUID.randomUUID(), "Milch", "Vollmilch", Unit.DECILITRE, 3);
        Recipe recipe = new Recipe(RECIPE_ID, "Pfannkuchen", "Süss", "https://img/pfannkuchen.jpg", List.of(mehl, milch));

        RecipeEntity entity = mapper.domainToEntity(recipe);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(entity.getId()).isEqualTo(RECIPE_ID);
        softly.assertThat(entity.getName()).isEqualTo("Pfannkuchen");
        softly.assertThat(entity.getDescription()).isEqualTo("Süss");
        softly.assertThat(entity.getImageUrl()).isEqualTo("https://img/pfannkuchen.jpg");
        softly.assertThat(entity.getIngredients()).hasSize(2);
        softly.assertThat(entity.getIngredients())
                .extracting(IngredientEntity::getName)
                .containsExactly("Mehl", "Milch");
        softly.assertThat(entity.getIngredients())
                .extracting(IngredientEntity::getUnit)
                .containsExactly(Unit.GRAMM, Unit.DECILITRE);
        softly.assertThat(entity.getIngredients().get(1).getComment()).isEqualTo("Vollmilch");
        softly.assertAll(); // ohne diesen Aufruf würden Fehler nicht gemeldet
    }

    @Test
    @DisplayName("Hin- und Rückweg ergibt wieder das gleiche Rezept")
    void roundTrip() {
        Recipe original = new Recipe(RECIPE_ID, "Fried Rice", "Asiatisch", "url",
                List.of(new Ingredient(INGREDIENT_ID, "Reis", null, Unit.KILOGRAMM, 1)));

        Recipe result = mapper.entityToDomain(mapper.domainToEntity(original));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).isEqualTo(original);
            softly.assertThat(result).isNotSameAs(original);
            softly.assertThat(result.getIngredients()).isNotSameAs(original.getIngredients());
        });
    }

    @Test
    @DisplayName("null-Werte: Rezept null, Zutatenliste null, Zutat null")
    void nullHandling() {
        RecipeEntity withoutIngredients = new RecipeEntity(RECIPE_ID, "Leer", null, null, null);
        List<Ingredient> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        Recipe withNullIngredient = new Recipe(RECIPE_ID, "Test", null, null, listWithNull);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(mapper.entityToDomain(null)).isNull();
            softly.assertThat(mapper.domainToEntity(null)).isNull();

            Recipe recipe = mapper.entityToDomain(withoutIngredients);
            softly.assertThat(recipe.getName()).isEqualTo("Leer");
            softly.assertThat(recipe.getDescription()).isNull();
            softly.assertThat(recipe.getIngredients()).isNull();

            softly.assertThat(mapper.domainToEntity(new Recipe()).getIngredients()).isNull();
            softly.assertThat(mapper.domainToEntity(withNullIngredient).getIngredients()).containsExactly((IngredientEntity) null);

            List<IngredientEntity> entityListWithNull = new ArrayList<>();
            entityListWithNull.add(null);
            RecipeEntity entityWithNullIngredient = new RecipeEntity(RECIPE_ID, "Test", null, null, entityListWithNull);
            softly.assertThat(mapper.entityToDomain(entityWithNullIngredient).getIngredients()).containsExactly((Ingredient) null);
        });
    }

    @Test
    @DisplayName("leere Zutatenliste bleibt leer")
    void emptyIngredients() {
        Recipe recipe = new Recipe(RECIPE_ID, "Wasser", "Nur Wasser", "url", List.of());

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(mapper.domainToEntity(recipe).getIngredients()).isEmpty();
            softly.assertThat(mapper.entityToDomain(mapper.domainToEntity(recipe)).getIngredients()).isEmpty();
        });
    }
}
