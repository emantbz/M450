# Automation Testing – Spring Boot & Angular

Pro Übung gibt es einen Ordner mit den Dateien zum Abgeben:

| Übung | Tool | Ordner |
|---|---|---|
| 1 – REST-API | Postman / Newman | [uebung-1-rest-api/](uebung-1-rest-api/) |
| 2 – GUI (End-to-End) | Playwright | [uebung-2-e2e/](uebung-2-e2e/) |
| 3 – Lasttest | autocannon | [uebung-3-lasttest/](uebung-3-lasttest/) |

Die lauffähige Applikation inkl. Test-Setup liegt in [../spring-boot-angular/](../spring-boot-angular/). Ausgeführt werden die Tests dort im Ordner `tests/`.

---

## Applikation lokal starten

**Backend** (Port 8081):

```bash
cd spring-boot-angular
./mvnw spring-boot:run
```

**Frontend** (Port 4200):

```bash
cd spring-boot-angular/src/main/js/my-app
npm install
npx ng serve
```

Danach ist die App unter http://localhost:4200 erreichbar.

**Anpassungen, damit es läuft:**
- Im ZIP fehlte der Ordner `.mvn`, darum habe ich den Maven Wrapper neu erzeugt.
- Die Lombok-Version wurde in der `pom.xml` auf 1.18.38 erhöht. Die alte Version kompiliert nicht mit Java 21.

**Die REST-Schnittstelle:**

| Methode | URL | Beschreibung |
|---|---|---|
| `GET` | `/students` | Liefert alle Studenten als JSON |
| `POST` | `/students` | Speichert einen neuen Studenten (`{ "name": "...", "email": "..." }`) |

**Test-Tools installieren (einmalig):**

```bash
cd spring-boot-angular/tests
npm install
npx playwright install chromium
```
