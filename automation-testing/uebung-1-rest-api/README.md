# Übung 1 – REST-Schnittstelle automatisiert testen

**Abgabe:** [students.postman_collection.json](students.postman_collection.json)

**Tool:** Postman-Collection, die mit **Newman** auf der Kommandozeile ausgeführt wird.

- Die Tests sind in einer Postman-Collection gespeichert. Man kann sie in Postman importieren, anschauen und dort per Klick ausführen.
- **Newman** ist der Kommandozeilen-Runner von Postman. Damit laufen dieselben Tests ohne GUI, zum Beispiel in einer CI-Pipeline.
- Jeder Request hat ein Test-Script in JavaScript, das die Antwort prüft (`pm.test(...)`).

**Getestete Fälle:**

| # | Request | Prüfung |
|---|---|---|
| 1 | `GET /students` | Status 200, JSON, Antwortzeit < 500 ms, mind. 5 Studenten, jeder hat `id`/`name`/`email`, Jonas ist vorhanden |
| 2 | `POST /students` | Neuer Student mit eindeutigem Namen → Status 200 |
| 3 | `GET /students` | Der neue Student aus Test 2 ist in der Liste, E-Mail stimmt, hat eine ID |
| 4 | `POST /students` mit kaputtem JSON | Status 400 Bad Request |
| 5 | `GET /gibtesnicht` | Status 404 Not Found |
| 6 | `GET /students` mit `Origin: localhost:4200` | CORS-Header erlaubt das Angular-Frontend |

**Ausführen:**

```bash
npm run test:api
```

**Resultat:** 6 Requests, 13 Assertions, 0 Fehler.

**In Postman zeigen:** *Import* → Datei `students.postman_collection.json` → Collection auswählen → *Run*.
