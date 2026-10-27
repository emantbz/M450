# Code Reviews – Recipe Planner

> **Hinweis:** Ich habe diese Übung alleine gemacht (kein Teampartner). Statt eines gegenseitigen Reviews wurden pro Pull Request **zwei unabhängige Reviews** gemacht und verglichen:
> 1. **Selbst-Review** mit etwas Abstand und anhand der Checkliste
> 2. **KI-Review** (Claude) als zweite, unabhängige Sicht
>
> Danach habe ich beide Reviews verglichen, die Punkte bewertet und die Erkenntnisse festgehalten.

## Meine Pull Requests

| Aufgabe | Branch | Base | PR |
|---|---|---|---|
| 1 – Rezept hinzufügen (Frontend) | `feature/add-recipe` | `main` | _Link einfügen_ |
| 2 – Rezept bearbeiten (Backend) | `feature/edit-recipe-backend` | `main` | _Link einfügen_ |
| 2 – Rezept bearbeiten (Frontend) | `feature/edit-recipe-frontend` | `feature/add-recipe` | _Link einfügen_ |

Der Frontend-PR von Aufgabe 2 baut auf Aufgabe 1 auf, weil er dasselbe Formular verwendet. Darum ist die Base `feature/add-recipe`. Sobald Aufgabe 1 in `main` gemergt ist, stellt man die Base auf `main` um.

## PR-Beschreibungen (zum Einfügen auf GitHub)

**PR 1 – Rezept hinzufügen**
> Das Formular „Add Recipe“ ist jetzt funktionsfähig: Name, Beschreibung, Bild-URL und Zutaten (Name, Einheit, Menge) werden per `POST /api/recipes` gespeichert. Danach geht es zurück zur Übersicht.
> - Validierung: Name ist Pflicht, Zutat braucht einen Namen und eine ganze Menge > 0
> - Fehlermeldung, wenn das Backend nicht erreichbar ist
> - API-Aufrufe in `src/apis/recipeApi.js`
> - Tests: `AddRecipe.test.js` (`npm test`)

**PR 2 – Rezept bearbeiten (Backend)**
> Neuer Endpunkt `PUT /api/recipes/recipe/{recipeId}` zum Bearbeiten eines Rezepts inkl. Zutaten.
> - 404, wenn es das Rezept nicht gibt; 400, wenn die ID im Body nicht zur URL passt
> - Zutaten mit ID werden aktualisiert, ohne ID neu angelegt, fehlende gelöscht
> - Tests: Controller (MockMvc), Service, Integrationstest mit H2 (`./mvnw verify`)

**PR 3 – Rezept bearbeiten (Frontend)**
> „Edit Details“ auf einer Rezeptkarte öffnet ein Formular mit den bestehenden Daten. Änderungen werden per `PUT` gespeichert.
> - Formular in `RecipeForm` ausgelagert, gemeinsam für Hinzufügen und Bearbeiten
> - IDs und Kommentare bestehender Zutaten bleiben erhalten
> - Tests: `EditRecipe.test.js` (`npm test`)
> - Braucht PR 2 (Backend) für den `PUT`-Endpunkt

## Checkliste fürs Review

- [ ] **Funktioniert es?** Lokal ausprobieren, auch Spezialfälle (leere Felder, negative Menge, Backend aus)
- [ ] **Lesbarkeit:** verständliche Namen für Variablen und Funktionen, keine kryptischen Abkürzungen
- [ ] **Kleine Funktionen:** jede Funktion macht eine Sache
- [ ] **Keine Duplikation:** gleicher Code nicht mehrfach (z.B. URL, Formular)
- [ ] **Fehlerbehandlung:** Was passiert, wenn das Backend einen Fehler liefert?
- [ ] **Validierung:** Werden falsche Eingaben abgefangen?
- [ ] **Tests:** Gibt es Tests, und testen sie das Richtige?
- [ ] **Keine Magic Numbers / hartcodierten Werte**
- [ ] **Kommentare:** erklären das *Warum*, nicht das *Was*
- [ ] **Kein toter Code**, keine `console.log`, keine ungenutzten Imports

Review-Kommentare schreibt man direkt im PR auf GitHub (*Files changed* → `+` neben der Zeile). Am besten konkret und freundlich, mit Begründung und Vorschlag.

## Review-Ergebnisse

_Nach beiden Reviews ausfüllen._

### Aufgabe 1 – Rezept hinzufügen

| Review | Wichtigste Kommentare |
|---|---|
| Selbst-Review | |
| KI-Review | |

**Vergleich:** Was haben beide gefunden? Was nur einer? Welche Punkte sind berechtigt, welche nicht?
-

### Aufgabe 2 – Rezept bearbeiten

| Review | Backend | Frontend |
|---|---|---|
| Selbst-Review | | |
| KI-Review | | |

**Vergleich:**
-

## Erkenntnisse

_Was habe ich aus den Reviews und dem Vergleich gelernt?_

-
