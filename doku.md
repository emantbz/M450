# Aufgaben – Softwaretests

## Aufgabe 1 – Arten von Tests

Es gibt verschiedene Arten von Tests:

1. **Unit-Test:** Man testet eine einzelne Funktion oder Methode. Zum Beispiel, ob eine Preisberechnung richtig funktioniert.
2. **Integrationstest:** Mehrere Teile eines Programms werden zusammen getestet. Zum Beispiel Warenkorb und Bezahlung in einem Online-Shop.
3. **Systemtest:** Das ganze Programm wird getestet. Zum Beispiel ein kompletter Kaufvorgang von Anfang bis Ende.

Bei den Tests gibt man verschiedene Werte ein und schaut, ob das erwartete Ergebnis herauskommt.

---

## Aufgabe 2 – Softwarefehler und Softwaremangel

**Softwarefehler:**  
Ein Programm berechnet bei einem Produkt von 100 CHF plötzlich 1'000 CHF. Das Programm macht also etwas falsch.

**Softwaremangel:**  
Eine Software sollte laut Anforderungen PDF-Rechnungen erstellen können, diese Funktion fehlt aber.

**Beispiel für einen hohen Schaden:**  
Bei der Ariane-5-Rakete führte 1996 ein Softwarefehler zur Zerstörung der Rakete. Der Schaden betrug ungefähr 370 Millionen Dollar.

---

## Aufgabe 3 – Preisberechnung

Die Methode kann so aussehen:

```java
public static double calculatePrice(double baseprice, double specialprice,
        double extraprice, int extras, double discount) {

    double addon_discount;

    if (extras >= 3)
        addon_discount = 10;
    else if (extras >= 5)
        addon_discount = 15;
    else
        addon_discount = 0;

    if (discount > addon_discount)
        addon_discount = discount;

    return baseprice / 100 * (100 - discount)
            + specialprice
            + extraprice / 100 * (100 - addon_discount);
}