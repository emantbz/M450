# Übung 1

## Abstrakte Testfälle

| ID | Bedingung Kaufpreis | Erwarteter Rabatt |
|---|---|---|
| AT01 | Kaufpreis < 15'000 CHF | 0 % |
| AT02 | 15'000 CHF <= Kaufpreis <= 20'000 CHF | 5 % |
| AT03 | 20'000 CHF < Kaufpreis < 25'000 CHF | 7 % |
| AT04 | Kaufpreis >= 25'000 CHF | 8.5 % |


## Konkrete Testfälle

| ID | Kaufpreis | Erwarteter Rabatt |
|---|---:|---:|
| KT01 | 10'000 CHF | 0 % |
| KT02 | 14'999 CHF | 0 % |
| KT03 | 15'000 CHF | 5 % |
| KT04 | 18'000 CHF | 5 % |
| KT05 | 20'000 CHF | 5 % |
| KT06 | 20'001 CHF | 7 % |
| KT07 | 23'000 CHF | 7 % |
| KT08 | 24'999 CHF | 7 % |
| KT09 | 25'000 CHF | 8.5 % |
| KT10 | 30'000 CHF | 8.5 % |




# Übung 2 – Black-Box-Tests Autovermietung

Getestete Webseite: SIXT

## Funktionale Black-Box-Testfälle

| ID | Beschreibung | Erwartetes Resultat | Effektives Resultat | Status | Mögliche Ursache |
|---|---|---|---|---|---|
| TC01 | Fahrzeug für einen bestimmten Zeitraum suchen | Verfügbare Fahrzeuge werden angezeigt | Verfügbare Fahrzeuge werden angezeigt | OK | - |
| TC02 | Abhol- und Rückgabeort auswählen | Die ausgewählten Orte werden korrekt übernommen | Die Orte wurden korrekt übernommen | OK | - |
| TC03 | Gültiges Datum eingeben | Die Suche kann mit dem ausgewählten Datum durchgeführt werden | Die Suche wurde erfolgreich durchgeführt | OK | - |
| TC04 | Fahrzeug auswählen | Ein verfügbares Fahrzeug kann ausgewählt werden | Das Fahrzeug konnte ausgewählt werden | OK | - |
| TC05 | Ungültige Eingaben testen | Das System verhindert die Eingabe oder zeigt eine Fehlermeldung | Das System hat die ungültige Eingabe korrekt behandelt | OK | - |

## Ergebnis

Alle fünf getesteten Funktionen haben wie erwartet funktioniert.
Es wurden keine Fehler gefunden.


# Übung 3

## Black-Box Testfälle

| Testfall | Test | Status |
|---|---|---|
| Einzahlen | 500€ einzahlen → Kontostand +500 
| Abheben | 100€ abheben → Kontostand -100 
| Transfer | USD→CHF konvertieren 
| Konto erstellen | Neue ID vergeben 
| Konto löschen | Aus Liste entfernt 

## White-Box Testfälle

| Methode | Test |
|---|---|---|
| Account.deposit() | Betrag wird addiert 
| Account.withdraw() | Betrag wird abgezogen 
| Bank.createAccount() | Neue ID wird vergeben 
| Bank.getAccount() | Konto wird gefunden 
| Counter.transferAmount() | Geld wird transferiert 

## Kritische Bugs

1. **Leere Eingabe → Crash** (StringIndexOutOfBoundsException)
2. **Negative Beträge erlaubt** (z.B. -500 einzahlen)
3. **Wechselkurs unvollständig** (CHF→EUR funktioniert nicht)
4. **Lösch-Bestätigung Crash** (leere Eingabe bei "j/n")

## Best Practices

- Verwende `BigDecimal` für Geld (nicht `double`)
- Validiere Input vor Verarbeitung
- Schreibe Unit-Tests