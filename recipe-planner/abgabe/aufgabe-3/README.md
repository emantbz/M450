# Aufgabe 3 – Pipeline

**Abgabe:** [recipe-planner-backend.yml](recipe-planner-backend.yml)

Die Datei ist eine Kopie. Damit GitHub sie ausführt, liegt das Original im Repo unter `.github/workflows/recipe-planner-backend.yml`.

Da das Repository auf GitHub liegt, wird **GitHub Actions** verwendet.

## Ablauf

Die Pipeline startet automatisch bei **jedem Push** und jedem Pull Request. Man kann sie auch manuell im *Actions*-Tab starten.

1. **Code auschecken:** Der Code wird auf einen frischen Ubuntu-Server von GitHub kopiert.
2. **Java 21 einrichten:** inklusive Cache für die Maven-Abhängigkeiten, damit es beim nächsten Mal schneller geht.
3. **`./mvnw verify`:** kompilieren, Unit-Tests ausführen, Reports erstellen. Schlägt ein Test fehl, wird die Pipeline **rot**.
4. **Zusammenfassung:** Anzahl Tests pro Klasse und Coverage werden direkt auf der Seite des Pipeline-Laufs angezeigt.
5. **Reports hochladen:** Surefire-HTML, JaCoCo-HTML und die XML-Resultate werden als **Artifact `test-reports`** gespeichert (30 Tage).

Schritte 4 und 5 laufen auch, wenn Tests fehlschlagen (`if: always()`). So sieht man auch dann den Report.

## Report einsehen

1. Auf GitHub im Repository auf **Actions** klicken
2. Den Pipeline-Lauf (Commit) auswählen
3. Oben steht die **Zusammenfassung** mit Tests und Coverage
4. Unten bei **Artifacts** `test-reports` herunterladen, entpacken und `reports/surefire.html` bzw. `site/jacoco/index.html` öffnen
