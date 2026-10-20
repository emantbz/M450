package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

/**
 * Testet den IngredientEntityMapper (Ingredient <-> IngredientEntity).
 * Die SoftAssertions werden über die JUnit-5-Extension injiziert und am Ende jedes Tests
 * automatisch ausgewertet (assertAll), es braucht also kein softly.assertAll().
 */
@ExtendWith(SoftAssertionsExtension.class)
class IngredientEntityMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    private final IngredientEntityMapper mapper = Mappers.getMapper(IngredientEntityMapper.class);

    private static final UUID ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    @DisplayName("Entity -> Domain: alle Felder werden übernommen")
    void entityToDomain() {
        IngredientEntity entity = new IngredientEntity(ID, "Tomato", "The big ones", Unit.PIECE, 5);

        Ingredient domain = mapper.entityToDomain(entity);

        softly.assertThat(domain.getId()).isEqualTo(ID);
        softly.assertThat(domain.getName()).isEqualTo("Tomato");
        softly.assertThat(domain.getComment()).isEqualTo("The big ones");
        softly.assertThat(domain.getUnit()).isEqualTo(Unit.PIECE);
        softly.assertThat(domain.getAmount()).isEqualTo(5);
    }

    @Test
    @DisplayName("Domain -> Entity: alle Felder werden übernommen")
    void domainToEntity() {
        Ingredient domain = new Ingredient(ID, "Mehl", "Weissmehl", Unit.GRAMM, 500);

        IngredientEntity entity = mapper.domainToEntity(domain);

        softly.assertThat(entity.getId()).isEqualTo(ID);
        softly.assertThat(entity.getName()).isEqualTo("Mehl");
        softly.assertThat(entity.getComment()).isEqualTo("Weissmehl");
        softly.assertThat(entity.getUnit()).isEqualTo(Unit.GRAMM);
        softly.assertThat(entity.getAmount()).isEqualTo(500);
    }

    @Test
    @DisplayName("Hin- und Rückweg ergibt wieder das gleiche Objekt")
    void roundTrip() {
        Ingredient original = new Ingredient(ID, "Milch", null, Unit.DECILITRE, 3);

        Ingredient result = mapper.entityToDomain(mapper.domainToEntity(original));

        softly.assertThat(result).isEqualTo(original);
        softly.assertThat(result).isNotSameAs(original);
    }

    @Test
    @DisplayName("null-Werte und leere Felder")
    void nullHandling() {
        softly.assertThat(mapper.entityToDomain(null)).isNull();
        softly.assertThat(mapper.domainToEntity(null)).isNull();
        softly.assertThat(mapper.entitiesToDomains(null)).isNull();
        softly.assertThat(mapper.domainsToEntities(null)).isNull();

        Ingredient empty = mapper.entityToDomain(new IngredientEntity());
        softly.assertThat(empty.getId()).isNull();
        softly.assertThat(empty.getName()).isNull();
        softly.assertThat(empty.getUnit()).isNull();
        softly.assertThat(empty.getAmount()).isZero();
    }

    @Test
    @DisplayName("Listen werden in der gleichen Reihenfolge gemappt")
    void listMapping() {
        List<IngredientEntity> entities = List.of(
                new IngredientEntity(UUID.randomUUID(), "Tomato", null, Unit.PIECE, 5),
                new IngredientEntity(UUID.randomUUID(), "Wasser", null, Unit.LITRE, 2));

        List<Ingredient> domains = mapper.entitiesToDomains(entities);

        softly.assertThat(domains).hasSize(2);
        softly.assertThat(domains).extracting(Ingredient::getName).containsExactly("Tomato", "Wasser");
        softly.assertThat(domains).extracting(Ingredient::getUnit).containsExactly(Unit.PIECE, Unit.LITRE);

        List<IngredientEntity> back = mapper.domainsToEntities(domains);
        softly.assertThat(back).extracting(IngredientEntity::getAmount).containsExactly(5, 2);
        softly.assertThat(mapper.entitiesToDomains(List.of())).isEmpty();
    }
}
