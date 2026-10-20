# Unit-Tests Banken Simulation

Für die Banken Simulation wurden drei Testklassen mit JUnit 5 geschrieben. Die Dateien liegen im Ordner [bank-tests/](bank-tests/).

**Resultat:** 51 Tests, alle erfolgreich (1 absichtlich übersprungen), Code Coverage ca. 81 %.

---

## 1. [AccountTest.java](bank-tests/AccountTest.java)

Testet die Klasse `Account` (ein einzelnes Bankkonto).

- Prüft, ob Name, Währung und Startguthaben richtig gesetzt werden
- Prüft, ob jedes neue Konto eine neue, fortlaufende ID bekommt
- Einzahlen erhöht den Kontostand
- Abheben funktioniert, wenn genug Geld da ist, auch wenn man genau den ganzen Kontostand abhebt (Grenzwert)
- Abheben von mehr Geld, als auf dem Konto ist, wird abgelehnt
- Ein Test ist mit `@Disabled` deaktiviert: Er zeigt einen Bug, denn man kann negative Beträge einzahlen

---

## 2. [BankTest.java](bank-tests/BankTest.java)

Testet die Klasse `Bank`, die alle Konten in einer Liste verwaltet.

- Neues Konto erstellen: Es wird der Liste hinzugefügt
- Konto über die Kontonummer suchen (gefunden / nicht gefunden → `null`)
- Konto löschen: Es ist danach nicht mehr in der Liste
- Die Ausgabe-Methoden (`print...`) zeigen die richtigen Daten an
- Damit man die Ausgaben prüfen kann, wird `System.out` in `@BeforeEach` umgeleitet und in `@AfterEach` wieder zurückgesetzt

---

## 3. [CounterTest.java](bank-tests/CounterTest.java)

Testet die Klasse `Counter` (den Bankschalter mit den Menüs).

- Der Counter liest Eingaben von der Tastatur (`System.in`). Im Test werden die Eingaben simuliert: Die Methode `counterWithInput("e", "250", "q")` gibt dem Counter die Eingaben so, als hätte der Benutzer sie getippt
- **Hauptmenü:** Konto wählen, alle Konten anzeigen, Konto erstellen, beenden, ungültige Eingaben
- **Kontomenü:** einzahlen, abheben, Kontostand, löschen (mit und ohne Bestätigung), Konto wechseln, beenden
- **Überweisung:** auf ein anderes Konto, auf das eigene Konto (nicht erlaubt), auf ein unbekanntes Konto, zu hoher Betrag
- **Währungsumrechnung:** USD→CHF (1.11), USD→EUR (0.91), CHF→USD (0.9), EUR→CHF (keine Umrechnung)
- Mit `@Nested` sind die Tests nach Menü gruppiert, mit `@ParameterizedTest` werden mehrere Währungen mit einem Test geprüft

