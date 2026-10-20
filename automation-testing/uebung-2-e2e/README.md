# Übung 2 – GUI End-to-End testen

**Abgabe:** [students.spec.js](students.spec.js) (Tests) und [playwright.config.js](playwright.config.js) (Konfiguration)

**Tool:** [Playwright](https://playwright.dev/)

- Playwright startet einen echten Browser (Chromium) und bedient die Seite wie ein Benutzer: klicken, tippen, prüfen.
- Die Tests laufen gegen das echte Frontend **und** das echte Backend, also End-to-End.
- In der [playwright.config.js](playwright.config.js) ist eingestellt, dass Playwright Backend und Frontend automatisch startet, falls sie noch nicht laufen.
- Wenn ein Test fehlschlägt, werden automatisch ein Screenshot und ein Trace gespeichert.

**Getestete Fälle:**

| # | Test | Was passiert |
|---|---|---|
| 1 | Startseite | Titel, TBZ-Logo und beide Navigations-Buttons sind sichtbar |
| 2 | Studentenliste | Klick auf *List Students* → die 5 Start-Studenten aus dem Backend werden angezeigt, E-Mail ist ein `mailto:`-Link |
| 3 | Formular-Validierung | Hinweise „Name/Email is required“ sind sichtbar, *Submit* ist erst aktiv, wenn beide Felder ausgefüllt sind |
| 4 | Student erfassen | Formular ausfüllen → *Submit* → Weiterleitung zur Liste → neuer Student ist in der Tabelle |

**Ausführen:**

```bash
npm run test:e2e           # unsichtbar im Hintergrund (headless)
npm run test:e2e:headed    # Browser öffnet sich sichtbar – gut zum Vorzeigen
npx playwright show-report # HTML-Report anschauen
```

**Resultat:** 4 Tests, alle erfolgreich (ca. 10 s).
