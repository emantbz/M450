# Aufgabe 2 – Reports

**Abgabe:**
- [pom.xml](pom.xml): Plugins, die die Reports erzeugen
- [surefire-report/surefire.html](surefire-report/surefire.html): Report der Testresultate
- [jacoco-report/index.html](jacoco-report/index.html): Coverage-Report

Die zwei Reports sind eine Kopie aus dem letzten Testlauf. Zum Anschauen einfach im Browser öffnen.

Mit einem Befehl werden die Tests ausgeführt und beide Reports erstellt:

```bash
cd recipe-planner/recipe-planner-backend
./mvnw verify
```

| Report | Tool | Datei | Zeigt |
|---|---|---|---|
| Testresultate | Maven Surefire Report Plugin | `target/reports/surefire.html` | Welche Tests gelaufen sind, bestanden/fehlgeschlagen, Dauer |
| Code Coverage | JaCoCo | `target/site/jacoco/index.html` | Welche Codezeilen und Verzweigungen von Tests ausgeführt wurden |

Öffnen (Mac):

```bash
open target/reports/surefire.html target/site/jacoco/index.html
```

## Wie funktioniert es?

- **Surefire** führt die Tests aus und schreibt die Resultate als XML (`target/surefire-reports/`).
- Das **Surefire-Report-Plugin** (`report-only`) macht daraus in der `verify`-Phase eine HTML-Seite.
- **JaCoCo** hängt sich als Java-Agent an die Tests (`prepare-agent`), merkt sich jede ausgeführte Codezeile und erstellt in der `verify`-Phase den HTML-Report (`report`).
- In der [lombok.config](../../recipe-planner-backend/lombok.config) ist eingestellt, dass von Lombok generierte Methoden (Getter, Setter, `equals` …) nicht mitgezählt werden. Sonst wäre die Coverage künstlich tief.

## Resultat

- **24 Tests**, 0 Fehler
- **Coverage:** 97,7 % Instructions, 100 % Branches
- Controller, Service und beide Mapper: 100 %
