package de.mannheim.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class UmbuchungUI {
    DBConnect dbConnect;
    Scanner scanner = new Scanner(System.in);
    int wahl;

    public void menu() {
        try {
            dbConnect = DBConnect.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        scanner.useLocale(Locale.US);

        do {
            System.out.println("(1) Umbuchung von einem Institut auf ein anderes");
            System.out.println("(2) zurück zur Startseite");

            wahl = scanner.nextInt();
            switch (wahl) {
                case 1 -> umbuchen();
                case 2 -> { return; }
            }

        } while (wahl != 0);
    }

    private void umbuchen() {
        List<String> institute = getInstitute();
        System.out.println("Von welchem Instiut soll abgebucht werden?");
        showInstitute(institute);

        int von;
        do {
            von = scanner.nextInt();
        } while (von < 1 || von > institute.size());

        String vonInstitut = institute.get(von - 1);

        System.out.println("Auf welches Institut soll gutgeschrieben werden?");

        int zu;
        do {
            zu = scanner.nextInt();
        } while (zu < 1 || zu > institute.size());

        String zuInstitut = institute.get(zu - 1);

        if (vonInstitut.equals(zuInstitut)) {
            System.out.println("Umbuchung auf sich selbst ist nicht erlaubt.");
            return;
        }

        System.out.println("Bitte Betrag eingeben: ");
        double betrag;
        do {
            while (!scanner.hasNextDouble()) {
                System.out.println("Ungültige Eingabe. Bitte Zahl eingeben: ");
                scanner.next();
            }
            betrag = scanner.nextDouble();

            if (betrag <= 0) {
                System.out.println("Betrag muss größer als 0 sein. Bitte erneut eingeben: ");
            }
        } while (betrag <= 0);

        if (dbConnect.umbuchen(vonInstitut, zuInstitut, betrag)) {
            System.out.println("Umbuchung über " + betrag + "€ von " + vonInstitut + " nach " + zuInstitut);
        }
    }

    private List<String> getInstitute() {
        return dbConnect.getInstitute();
    }

    private void showInstitute(List<String> institute) {
        int i = 1;
        for (String s : institute) {
            System.out.println("(" + i + ") " + s);
            i++;
        }
    }

}
