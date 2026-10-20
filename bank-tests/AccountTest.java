package ch.tbz.bank.software;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Account")
class AccountTest {

    private static final double DELTA = 1e-9;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("Muster", Currency.CHF, 100.0);
    }

    @Test
    @DisplayName("Konstruktor setzt Name, Währung und Startguthaben")
    void constructorSetsFields() {
        assertAll(
                () -> assertEquals("Muster", account.getUserLastName()),
                () -> assertEquals(Currency.CHF, account.getCurrency()),
                () -> assertEquals(100.0, account.getBalance(), DELTA),
                () -> assertTrue(account.getId() > 0)
        );
    }

    @Test
    @DisplayName("jedes neue Konto erhält eine eindeutige, fortlaufende ID")
    void idsAreUniqueAndIncreasing() {
        Account second = new Account("Zweit", Currency.EUR, 0);
        assertEquals(account.getId() + 1, second.getId());
    }

    @Test
    @DisplayName("Einzahlung erhöht den Kontostand")
    void depositIncreasesBalance() {
        account.deposit(50.5);
        assertEquals(150.5, account.getBalance(), DELTA);
    }

    @Test
    @DisplayName("Abheben mit genügend Guthaben")
    void withdrawWithEnoughBalance() {
        assertTrue(account.withdraw(40));
        assertEquals(60.0, account.getBalance(), DELTA);
    }

    @Test
    @DisplayName("Abheben des gesamten Guthabens ist erlaubt (Grenzwert)")
    void withdrawExactBalance() {
        assertTrue(account.withdraw(100));
        assertEquals(0.0, account.getBalance(), DELTA);
    }

    @Test
    @DisplayName("Abheben über dem Kontostand wird abgelehnt")
    void withdrawTooMuch() {
        assertFalse(account.withdraw(100.01));
        assertEquals(100.0, account.getBalance(), DELTA);
    }

    @Test
    @DisplayName("printBalance gibt Kontostand mit Währung aus")
    void printBalance() {
        PrintStream original = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        try {
            account.printBalance();
        } finally {
            System.setOut(original);
        }
        assertTrue(out.toString().contains("Aktueller Kontostand: 100"));
        assertTrue(out.toString().contains("CHF"));
    }

    @Test
    @DisplayName("pseudoDeleteAccount setzt alle Felder zurück")
    void pseudoDeleteAccount() {
        account.pseudoDeleteAccount();
        assertAll(
                () -> assertNull(account.getUserLastName()),
                () -> assertNull(account.getCurrency()),
                () -> assertEquals(0.0, account.getBalance(), DELTA),
                () -> assertEquals(0, account.getId())
        );
    }

    @Test
    @Disabled("Bekannter Bug: negative Beträge können eingezahlt werden")
    @DisplayName("negative Einzahlung sollte abgelehnt werden")
    void negativeDepositShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-500));
    }
}
