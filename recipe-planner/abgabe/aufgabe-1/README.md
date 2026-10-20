# Aufgabe 1 – Unit Testing

**Abgabe:**
- [RecipeControllerTest.java](RecipeControllerTest.java)
- [RecipeEntityMapperTest.java](RecipeEntityMapperTest.java)
- [IngredientEntityMapperTest.java](IngredientEntityMapperTest.java)

**Resultat:** 24 Tests, alle grün.

---

## 1. Controller-Tests mit MockMvc

- `@WebMvcTest(RecipeController.class)` startet nur die Web-Schicht von Spring, nicht die ganze App.
- Der `RecipeService` wird mit `@MockBean` gemockt. So wird nur der Controller getestet: URL, HTTP-Status und JSON.
- `MockMvc` schickt simulierte HTTP-Requests, es braucht keinen echten Server.

| Methode | Getestet |
|---|---|
| `GET /api/recipes` | Liste mit 2 Rezepten als JSON, leere Liste |
| `GET /api/recipes/recipe/{id}` | Rezept gefunden, unbekannte ID (200 ohne Inhalt), ungültige UUID → 400 |
| `POST /api/recipes` | Rezept speichern (mit `ArgumentCaptor` geprüft, was beim Service ankommt), kaputtes JSON → 400, unbekannte Einheit → 400 |
| CORS | `localhost:3000` (Frontend) erlaubt, fremde Seite → 403 |

## 2. Mapper-Tests mit SoftAssertions

Die Mapper wandeln zwischen **Domain-Klassen** (`Recipe`, `Ingredient`) und **Datenbank-Entities** (`RecipeEntity`, `IngredientEntity`) um. Sie werden von MapStruct generiert und im Test mit `Mappers.getMapper(...)` erstellt, ohne Spring.

Getestet wird pro Mapper:
- Entity → Domain und Domain → Entity: alle Felder werden übernommen
- Hin- und Rückweg ergibt wieder das gleiche Objekt
- `null`-Werte, leere Listen, Listen mit `null`-Einträgen
- `RecipeEntityMapper`: auch die verschachtelte Zutatenliste

Drei Arten, SoftAssertions zu verwenden (alle drei kommen vor):

```java
// 1. als Lambda-Block
SoftAssertions.assertSoftly(softly -> {
    softly.assertThat(recipe.getName()).isEqualTo("Lasagne al Forno");
    softly.assertThat(recipe.getIngredients()).hasSize(1);
});

// 2. von Hand, am Ende assertAll() nicht vergessen!
SoftAssertions softly = new SoftAssertions();
softly.assertThat(entity.getName()).isEqualTo("Pfannkuchen");
softly.assertAll();

// 3. mit JUnit-5-Extension, assertAll() passiert automatisch
@ExtendWith(SoftAssertionsExtension.class)
class IngredientEntityMapperTest {
    @InjectSoftAssertions
    private SoftAssertions softly;
}
```

### Vorteil von SoftAssertions

- **Normale Assertions** brechen beim **ersten** Fehler ab. Sind 3 Felder falsch, sieht man nur den ersten Fehler, korrigiert ihn, startet neu und sieht den nächsten.
- **SoftAssertions** führen **alle** Prüfungen aus und melden am Ende **alle Fehler auf einmal**.
- Das ist ideal für Mapper, weil man dort viele Felder eines Objekts prüft. Man sieht sofort, welche Felder falsch gemappt werden.
