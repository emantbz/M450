# Übung 3 – Lasttest

**Abgabe:** diese Zusammenfassung, [load-test.js](load-test.js) (Lasttest-Script) und [results.json](results.json) (Resultate)

## Tool: autocannon

[autocannon](https://github.com/mcollina/autocannon) ist ein Lasttest-Tool für HTTP, geschrieben in Node.js. Ich habe es statt JMeter gewählt, weil es in denselben `npm`-Ordner passt wie Newman und Playwright und die Szenarien als normales JavaScript geschrieben werden.

**Funktionalitäten:**
- Viele gleichzeitige Verbindungen (`connections`) über eine feste Dauer (`duration`) oder eine feste Anzahl Requests (`amount`)
- Alle HTTP-Methoden, eigene Header und Bodies
- Dynamische Requests: `setupRequest` erzeugt für jede Anfrage einen neuen Body, zum Beispiel einen eindeutigen Namen
- Mehrere Requests hintereinander als Ablauf (z.B. erst Login, dann Daten abrufen)
- *Pipelining*: mehrere Requests pro Verbindung, ohne auf die Antwort zu warten
- Rate-Limit (`overallRate`), um eine bestimmte Last (Requests/s) zu simulieren statt „so viel wie möglich“
- Auswertung: Requests/s, Durchsatz (Bytes/s), Latenz (Durchschnitt, p50, p90, p99, max), Fehler, Timeouts, Status-Codes
- Nutzbar als CLI (`npx autocannon -c 100 -d 20 http://localhost:8081/students`) oder als Bibliothek im eigenen Script

**Vergleich mit den anderen Tools:**

| | autocannon | JMeter | Postman |
|---|---|---|---|
| Bedienung | Kommandozeile / JavaScript | GUI zum Zusammenklicken, CLI für die Ausführung | GUI (Collection Runner → *Performance*) |
| Szenarien | JavaScript-Code | Test-Plan (XML) mit Thread Groups, Samplers, Listeners | bestehende Collection wiederverwenden |
| Stärken | leichtgewichtig, sehr hohe Last, gut für CI | sehr mächtig, viele Protokolle (HTTP, JDBC, FTP …), Diagramme | einfachster Einstieg, wenn man Postman schon nutzt |
| Schwächen | nur HTTP, keine Diagramme | schwerfällig, braucht viel Einarbeitung | begrenzte Anzahl virtueller Benutzer |

## Szenarien

Das Script [load-test.js](load-test.js) führt drei Szenarien hintereinander aus, jeweils 20 Sekunden lang:

1. **GET /students** mit 100 gleichzeitigen Verbindungen (5 Studenten in der DB)
2. **POST /students** mit 50 gleichzeitigen Verbindungen, jede Anfrage erzeugt einen neuen Studenten
3. **GET /students** nochmals mit 100 Verbindungen, jetzt mit den vielen neuen Daten

```bash
npm run test:load
```

Die Resultate werden zusätzlich in [results.json](results.json) gespeichert.

## Resultate

(MacBook, Backend frisch gestartet)

| Szenario | Requests total | Requests/s | Latenz avg | Latenz p99 | Fehler | Studenten in DB |
|---|---|---|---|---|---|---|
| 1) GET – Ausgangslage | 468'975 | 23'449 | 81 ms | 161 ms | 4'293 | 5 |
| 2) POST – neue Studenten | 503'310 | 25'166 | 87 ms | 208 ms | 4'936 | 503'362 |
| 3) GET – nach dem POST-Test | **0** | **0** | – | – | 101 **Timeouts** | 503'362 |

## Erkenntnisse

- **Mit wenig Daten ist das Backend sehr schnell:** über 23'000 Requests pro Sekunde bei unter 100 ms Latenz.
- **Der POST-Endpunkt hat keinen Schutz:** In 20 Sekunden wurden über 500'000 Studenten angelegt. Es gibt keine Validierung, kein Rate-Limit und keine Prüfung auf Duplikate.
- **`GET /students` skaliert nicht:** Der Endpunkt liefert **immer alle** Studenten auf einmal (`findAll()`). Mit 500'000 Einträgen ist eine Antwort mehrere MB gross, und keine einzige Anfrage kam innerhalb von 10 Sekunden zurück. Im ersten Durchlauf mit 330'000 Einträgen kamen noch 20 Anfragen durch, mit einer Latenz von ca. 6,7 s.
  → **Lösung:** Pagination (`GET /students?page=0&size=20`, in Spring mit `PagingAndSortingRepository`).
- **Die ca. 1 % Fehler in Szenario 1 und 2 sind `ECONNRESET`:** Tomcat schliesst eine Keep-Alive-Verbindung standardmässig nach 100 Requests (`server.tomcat.max-keep-alive-requests`). Das ist ein Effekt des Lasttests und kein Absturz des Servers.
- Die Datenbank ist eine H2-**In-Memory**-DB. Nach einem Neustart des Backends sind die Testdaten wieder weg.
