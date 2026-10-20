package ch.tbz.bank.software;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bank")
class BankTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream out;

    private Bank bank;
    private Account gates;
    private Account musk;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        bank = new Bank();
        gates = bank.createAccount("Gates", Currency.EUR, 2000);
        musk = bank.createAccount("Musk", Currency.CHF, 23500);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("createAccount legt Konto an und fügt es der Bank hinzu")
    void createAccount() {
        Account a = bank.createAccount("Bezos", Currency.USD, 100.5);
        assertEquals(3, bank.getNumberOfAccounts());
        assertSame(a, bank.getAccount(a.getId()));
        assertEquals("Bezos", a.getUserLastName());
    }

    @Test
    @DisplayName("neue Bank hat keine Konten")
    void newBankIsEmpty() {
        assertEquals(0, new Bank().getNumberOfAccounts());
    }

    @Test
    @DisplayName("getAccount findet Konto anhand der Nummer")
    void getAccountFound() {
        assertSame(musk, bank.getAccount(musk.getId()));
    }

    @Test
    @DisplayName("getAccount liefert null für unbekannte Nummer")
    void getAccountNotFound() {
        assertNull(bank.getAccount(-1));
    }

    @Test
    @DisplayName("deleteAccount entfernt Konto aus der Liste")
    void deleteAccount() {
        bank.deleteAccount(gates);
        assertEquals(1, bank.getNumberOfAccounts());
        assertNull(bank.getAccount(gates.getId()));
        assertTrue(out.toString().contains("Konto mit Nummer " + gates.getId() + " wurde gelöscht."));
    }

    @Test
    @DisplayName("printAccountDetails zeigt Nummer, Name und Kontostand")
    void printAccountDetails() {
        bank.printAccountDetails(gates);
        String output = out.toString();
        assertTrue(output.contains("Kontonummer: " + gates.getId()));
        assertTrue(output.contains("Nachname: Gates"));
        assertTrue(output.contains("EUR"));
    }

    @Test
    @DisplayName("printAccountDetails meldet gelöschtes Konto")
    void printAccountDetailsDeleted() {
        bank.deleteAccount(gates);
        bank.printAccountDetails(gates);
        assertTrue(out.toString().contains("Das Konto " + gates.getId() + " existiert nicht mehr!"));
    }

    @Test
    @DisplayName("printBalance zeigt neuen Kontostand")
    void printBalance() {
        bank.printBalance(musk);
        assertTrue(out.toString().contains("Neuer Kontostand: 23"));
        assertTrue(out.toString().contains("CHF"));
    }

    @Test
    @DisplayName("printAccountsList listet alle Konten")
    void printAccountsList() {
        bank.printAccountsList();
        String output = out.toString();
        assertTrue(output.contains("Nr. " + gates.getId() + ": Gates (EUR)"));
        assertTrue(output.contains("Nr. " + musk.getId() + ": Musk (CHF)"));
    }

    @Test
    @DisplayName("printOtherAccounts lässt das aktuelle Konto weg")
    void printOtherAccounts() {
        bank.printOtherAccounts(gates);
        String output = out.toString();
        assertFalse(output.contains("Gates"));
        assertTrue(output.contains("Nr. " + musk.getId() + ": Musk"));
    }
}
