package de.mannheim.db;

import java.util.Locale;
import java.util.Scanner;

public class inputChecker {
    private static final Scanner scanner = new Scanner(System.in).useLocale(Locale.GERMANY);

    public static int getInt() {
        while (true) {
            System.out.print("Bitte eine ganze Zahl eingeben: ");
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {
                System.out.println("Ungültige Eingabe – bitte nur Zahlen verwenden.");
                scanner.next();
            }
        }
    }

    public static double getDouble() {
        while (true) {
            System.out.print("Bitte einen Betrag eingeben: ");
            if (scanner.hasNextDouble()) {
                return scanner.nextDouble();
            } else {
                System.out.println("Ungültige Eingabe – bitte nur Zahlen verwenden.");
                scanner.next();
            }
        }
    }
    
    public static Scanner getScanner(){
        return scanner;
    }

}
