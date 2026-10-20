package ch.tbz.bank.software;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Der Counter liest von System.in und schreibt auf System.out.
 * Für die Tests werden beide Streams umgeleitet, damit Benutzereingaben simuliert
 * und Ausgaben geprüft werden können.
 */
@DisplayName("Counter")
class CounterTest {

    private static final double DELTA = 1e-9;

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream out;

    private Bank bank;
    private Account usd;
    private Account eur;
    private Account chf;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));

        bank = new Bank();
        usd = bank.createAccount("Rockefeller", Currency.USD, 1000);
        eur = bank.createAccount("Gates", Currency.EUR, 2000);
        chf = bank.createAccount("Musk", Currency.CHF, 3000);
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /** Erstellt einen Counter, der die übergebenen Zeilen als Benutzereingaben liest. */
    private Counter counterWithInput(String... lines) {
        String input = String.join("\n", lines) + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new Counter(bank);
    }

    private String output() {
        return out.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Konstruktor begrüsst den Kunden")
    void constructorPrintsWelcome() {
        counterWithInput("q");
        assertTrue(output().contains("Willkommen am Schalter"));
    }

    @Test
    @DisplayName("sayGoodbye verabschiedet den Kunden")
    void sayGoodbye() {
        counterWithInput("q").sayGoodbye();
        assertTrue(output().contains("Auf Wiedersehen!"));
    }

    @Nested
    @DisplayName("chooseAccount")
    class ChooseAccount {

        @Test
        @DisplayName("'q' beendet und liefert 0")
        void quit() {
            assertEquals(0, counterWithInput("q").chooseAccount());
            assertTrue(output().contains("Auf Wiedersehen!"));
        }

        @Test
        @DisplayName("gültige Kontonummer wird zurückgegeben")
        void validAccountNumber() {
            int nr = counterWithInput(String.valueOf(eur.getId())).chooseAccount();
            assertEquals(eur.getId(), nr);
            assertTrue(output().contains("Nachname: Gates"));
        }

        @Test
        @DisplayName("unbekannte Kontonummer führt zu Fehlermeldung")
        void unknownAccountNumber() {
            assertEquals(0, counterWithInput("999999", "q").chooseAccount());
            assertTrue(output().contains("Ein Konto mit dieser Nummer ist nicht vorhanden!"));
        }

        @Test
        @DisplayName("ungültige Eingabe führt zu Fehlermeldung")
        void invalidInput() {
            assertEquals(0, counterWithInput("xyz", "", "q").chooseAccount());
            assertTrue(output().contains("! Ungültige Eingabe"));
        }

        @Test
        @DisplayName("'a' zeigt alle Konten")
        void showAllAccounts() {
            counterWithInput("a", "q").chooseAccount();
            assertTrue(output().contains("Nr. " + usd.getId() + ": Rockefeller (USD)"));
            assertTrue(output().contains("Nr. " + chf.getId() + ": Musk (CHF)"));
        }

        @Test
        @DisplayName("'e' erstellt ein neues Konto")
        void createAccount() {
            counterWithInput("e", "Branson", "chf", "q").chooseAccount();
            assertEquals(4, bank.getNumberOfAccounts());
            assertTrue(output().contains("Nachname: Branson"));
        }
    }

    @Nested
    @DisplayName("createAccount")
    class CreateAccount {

        @ParameterizedTest(name = "Eingabe \"{0}\" ergibt {1}")
        @CsvSource({"usd, USD", "EUR, EUR", "chf, CHF"})
        void knownCurrencies(String input, Currency expected) {
            Account a = counterWithInput("Neu", input).createAccount();
            assertEquals(expected, a.getCurrency());
            assertEquals("Neu", a.getUserLastName());
            assertEquals(0.0, a.getBalance(), DELTA);
        }

        @Test
        @DisplayName("unbekannte Währung fällt auf USD zurück")
        void unknownCurrencyFallsBackToUsd() {
            Account a = counterWithInput("Neu", "GBP").createAccount();
            assertEquals(Currency.USD, a.getCurrency());
            assertTrue(output().contains("es wird USD verwendet"));
        }

        @Test
        @DisplayName("ungültiges Währungsformat wird erneut abgefragt")
        void invalidCurrencyFormat() {
            Account a = counterWithInput("Neu", "Franken", "EUR").createAccount();
            assertEquals(Currency.EUR, a.getCurrency());
            assertTrue(output().contains("! Ungültige Eingabe !"));
        }
    }

    @Nested
    @DisplayName("editAccount")
    class EditAccount {

        @Test
        @DisplayName("'e' zahlt Betrag ein (ungültiger Betrag wird erneut abgefragt)")
        void deposit() {
            boolean result = counterWithInput("e", "abc", "250", "q").editAccount(usd.getId());
            assertFalse(result);
            assertEquals(1250.0, usd.getBalance(), DELTA);
            assertTrue(output().contains("! Ungültige Eingabe, bitte nochmals!"));
        }

        @Test
        @DisplayName("'a' hebt Betrag ab")
        void withdraw() {
            counterWithInput("a", "300", "q").editAccount(usd.getId());
            assertEquals(700.0, usd.getBalance(), DELTA);
        }

        @Test
        @DisplayName("'a' mit zu hohem oder ungültigem Betrag wird erneut abgefragt")
        void withdrawTooMuch() {
            counterWithInput("a", "5000", "abc", "100", "q").editAccount(usd.getId());
            assertEquals(900.0, usd.getBalance(), DELTA);
            assertTrue(output().contains("! Kontostand zu niedrig"));
            assertTrue(output().contains("! Ungültige Eingabe, bitte nochmals!"));
        }

        @Test
        @DisplayName("'k' zeigt Kontostand")
        void showBalance() {
            counterWithInput("k", "q").editAccount(usd.getId());
            assertTrue(output().contains("Aktueller Kontostand: 1"));
        }

        @Test
        @DisplayName("ungültige Aktion führt zu Fehlermeldung")
        void invalidAction() {
            counterWithInput("x", "q").editAccount(usd.getId());
            assertTrue(output().contains("! Ungültige Eingabe: Bitte eine der Aktionen auswählen!"));
        }

        @Test
        @DisplayName("'w' wechselt das Konto (liefert true)")
        void switchAccount() {
            assertTrue(counterWithInput("w").editAccount(usd.getId()));
        }

        @Test
        @DisplayName("'q' beendet die Anwendung (liefert false)")
        void quit() {
            assertFalse(counterWithInput("q").editAccount(usd.getId()));
            assertTrue(output().contains("Auf Wiedersehen!"));
        }

        @Test
        @DisplayName("'l' mit Bestätigung löscht das Konto")
        void deleteConfirmed() {
            assertTrue(counterWithInput("l", "j").editAccount(usd.getId()));
            assertNull(bank.getAccount(usd.getId()));
            assertEquals(2, bank.getNumberOfAccounts());
        }

        @Test
        @DisplayName("'l' ohne Bestätigung löscht nichts")
        void deleteCancelled() {
            assertTrue(counterWithInput("l", "n").editAccount(usd.getId()));
            assertSame(usd, bank.getAccount(usd.getId()));
            assertTrue(output().contains("! Aktion abgebrochen."));
        }
    }

    @Nested
    @DisplayName("Überweisung ('ü')")
    class Transfer {

        @Test
        @DisplayName("gleiche Währung: Betrag wird 1:1 überwiesen")
        void sameCurrency() {
            Account eur2 = bank.createAccount("Bezos", Currency.EUR, 0);
            counterWithInput("ü", String.valueOf(eur2.getId()), "500", "q").editAccount(eur.getId());
            assertEquals(1500.0, eur.getBalance(), DELTA);
            assertEquals(500.0, eur2.getBalance(), DELTA);
            assertTrue(output().contains("Neuer Kontostand: 1500"));
        }

        @Test
        @DisplayName("ungültige Kontonummer wird erneut abgefragt")
        void invalidTargetInput() {
            counterWithInput("ü", "abc", String.valueOf(chf.getId()), "100", "q").editAccount(usd.getId());
            assertTrue(output().contains("! Ungültige Eingabe ! Bitte eine Zahl eingeben!"));
            assertEquals(900.0, usd.getBalance(), DELTA);
        }

        @Test
        @DisplayName("Überweisung auf eigenes Konto wird abgebrochen")
        void sameAccount() {
            counterWithInput("ü", String.valueOf(usd.getId()), "q").editAccount(usd.getId());
            assertTrue(output().contains("Bitte ein anderes Konto als das momentane Konto auswählen!"));
            assertEquals(1000.0, usd.getBalance(), DELTA);
        }

        @Test
        @DisplayName("Überweisung auf unbekanntes Konto wird abgebrochen")
        void unknownAccount() {
            counterWithInput("ü", "999999", "q").editAccount(usd.getId());
            assertTrue(output().contains("Ein Konto mit dieser Nummer ist nicht vorhanden!"));
            assertEquals(1000.0, usd.getBalance(), DELTA);
        }

        @Test
        @DisplayName("Kontonummer ausserhalb int-Bereich führt zu Fehlermeldung")
        void numberTooLarge() {
            counterWithInput("ü", "99999999999", String.valueOf(chf.getId()), "1", "q").editAccount(usd.getId());
            assertTrue(output().contains("! Ungültige Eingabe, bitte nochmals!"));
        }
    }

    @Nested
    @DisplayName("transferAmount / Währungsumrechnung")
    class TransferAmount {

        @Test
        @DisplayName("USD → CHF mit Kurs 1.11")
        void usdToChf() {
            counterWithInput("100").transferAmount(usd, chf);
            assertEquals(900.0, usd.getBalance(), DELTA);
            assertEquals(3111.0, chf.getBalance(), DELTA);
        }

        @Test
        @DisplayName("USD → EUR mit Kurs 0.91")
        void usdToEur() {
            counterWithInput("100").transferAmount(usd, eur);
            assertEquals(2091.0, eur.getBalance(), DELTA);
        }

        @Test
        @DisplayName("CHF → USD mit Kurs 0.9")
        void chfToUsd() {
            counterWithInput("100").transferAmount(chf, usd);
            assertEquals(2900.0, chf.getBalance(), DELTA);
            assertEquals(1090.0, usd.getBalance(), DELTA);
        }

        @Test
        @DisplayName("EUR → CHF: keine Umrechnung vorhanden, Betrag bleibt gleich")
        void eurToChfNoConversion() {
            counterWithInput("100").transferAmount(eur, chf);
            assertEquals(3100.0, chf.getBalance(), DELTA);
            assertTrue(output().contains("! Es wurde keine Umrechnung vorgenommen."));
        }

        @Test
        @DisplayName("zu hoher und ungültiger Betrag werden erneut abgefragt")
        void invalidAmounts() {
            counterWithInput("5000", "abc", "100").transferAmount(usd, chf);
            assertTrue(output().contains("! Kontostand zu niedrig!"));
            assertTrue(output().contains("! Ungültige Eingabe, bitte nochmals!"));
            assertEquals(900.0, usd.getBalance(), DELTA);
        }
    }
}
