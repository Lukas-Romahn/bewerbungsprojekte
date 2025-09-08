package de.mannheim.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class EinzahlungUI {
    DBConnect dbConnect;
    Scanner scanner = inputChecker.getScanner();
    int wahl;

    public void menu() {
        try {
            dbConnect = DBConnect.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        do {
            System.out.println("(1) Einzahlung auf ein Institut");
            System.out.println("(2) zurück auf die Startseite");

            wahl = scanner.nextInt();
            switch (wahl) {
                case 1 -> einzahlen();
                case 2 ->{
                    return;
                }
            }
        } while (wahl != 0);
    }

    private void einzahlen() {
        List<String> institute = getInstitute();
        showInstitute(institute);

        int auswahl;
        do {
            auswahl = scanner.nextInt();
        } while (auswahl < 1 || auswahl > institute.size());

        String institutName = institute.get(auswahl - 1);

        double betrag;
        do {

            betrag = inputChecker.getDouble();

            if (betrag <= 0) {
                System.out.println("Betrag muss größer als 0 sein. Bitte erneut eingeben: ");
            }
        } while (betrag <= 0);

        dbConnect.einzahlen(institutName, betrag);
        System.out.println("Einzahlung über " + betrag + "€ auf " + institutName);
    }

    private List<String> getInstitute() {
        return dbConnect.getInstitute();
    }

    private void showInstitute(List<String> institute) {
        int i = 1;
        for (String s : institute) {
            System.out.println("(" + i + ")" + s);
            i++;
        }
    }
}
