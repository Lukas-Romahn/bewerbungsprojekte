package de.mannheim.db;

import java.sql.SQLException;
import java.util.List;

import de.mannheim.db.DTO.Institut;
import de.mannheim.db.DTO.ProfessorAusgabe;

public class GesamtausgabenUI {
    DBConnect dbConnect;
    int wahl;

    public void menu() {
        try {
            dbConnect = DBConnect.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        do {
            System.out.println();
            System.out.println("=== GESAMTAUSGABEN ===");
            System.out.println("(1) Gesamtausgaben aller Institute (absteigend)");
            System.out.println("(2) Gesamtausgaben je Professor (absteigend)");
            System.out.println("(3) Durchschnittliche Ausgaben pro Professor je Institut");
            System.out.println("(4) Gesamtausgaben pro Investitionsart");
            System.out.println("(5) Zurück zur Startseite");
            System.out.print("Ihre Wahl: ");

            wahl = inputChecker.getInt();

            switch (wahl) {
                case 1 -> showAusgabenProInstitut();
                case 2 -> showAusgabenProProfessor();
                case 3 -> showRelativeAusgabenProInstitut();
                case 4 -> showAusgabenProInvestitionsart();
                case 5 -> new MainMenuUI().mainMenu();
                default -> System.out.println("Falsche Wahl, geben Sie eine Zahl im Bereich 1-5 ein");
            }

        } while (wahl != 5);
    }

    private void showAusgabenProInstitut() {
        System.out.println("\n=== GESAMTAUSGABEN ALLER INSTITUTE (ABSTEIGEND) ===");
        List<Institut> daten = dbConnect.getSumOfAllAusgabenFromAllInstitute();

        if (daten == null || daten.isEmpty()) {
            System.out.println("Keine Daten verfügbar.");
            return;
        }

        int maxNameLength = getLengthfromLongestInstitut(daten);

        System.out.printf("%-" + maxNameLength + "s | %-12s%n", "Institut", "Ausgaben (€)");
        printLine(maxNameLength + 15);
        if (daten != null) {

            for (Institut institut : daten) {
                System.out.printf("%-" + maxNameLength + "s | %12.2f%n",
                        institut.name(), Math.abs(institut.Kontostand()));
            }
            System.out.println();
        }
    }

    private void showAusgabenProProfessor() {
        System.out.println("\n=== GESAMTAUSGABEN JE PROFESSOR (ABSTEIGEND) ===");
        List<ProfessorAusgabe> daten = dbConnect.getSummOfAllProfs();

        if (daten == null || daten.isEmpty()) {
            System.out.println("Keine Daten verfügbar.");
            return;
        }

        int maxNameLength = getLengthfromLongestProfessor(daten);

        System.out.printf("%-" + maxNameLength + "s | %-12s%n", "Professor", "Ausgaben (€)");
        printLine(maxNameLength + 15);

        for (ProfessorAusgabe professor : daten) {
            System.out.printf("%-" + maxNameLength + "s | %12.2f%n",
                    professor.getName(), Math.abs(professor.getAusgabe()));
        }
        System.out.println();
    }

    private void showRelativeAusgabenProInstitut() {
        System.out.println("\n=== DURCHSCHNITTLICHE AUSGABEN PRO PROFESSOR JE INSTITUT ===");
        List<Institut> daten = dbConnect.getSumOfAllInstitutsRelativeToProfCount();

        if (daten == null || daten.isEmpty()) {
            System.out.println("Keine Daten verfügbar.");
            return;
        }

        int maxNameLength = getLengthfromLongestInstitut(daten);

        System.out.printf("%-" + maxNameLength + "s | %-12s%n", "Institut", "Ø / Prof (€)");
        printLine(maxNameLength + 15);

        for (Institut institut : daten) {
            System.out.printf("%-" + maxNameLength + "s | %12.2f%n",
                    institut.name(), Math.abs(institut.Kontostand()));
        }
        System.out.println();
    }

    private void showAusgabenProInvestitionsart() {
        System.out.println("\n=== GESAMTAUSGABEN PRO INVESTITIONSART ===");

        String[] investitionsarten = { "Hardware", "Software", "Infrastruktur", "Sonstiges" };
        int maxArtLength = "Investitionsart".length();

        // maximale Länge für Ausgabenformatierung
        for (String art : investitionsarten) {
            if (art.length() > maxArtLength) {
                maxArtLength = art.length();
            }
        }

        System.out.printf("%-" + maxArtLength + "s | %-12s%n", "Investitionsart", "Ausgaben (€)");
        printLine(maxArtLength + 15);

        for (String art : investitionsarten) {
            float ausgaben = dbConnect.getSumOfAllInvesitionenFromArt(art);
            System.out.printf("%-" + maxArtLength + "s | %12.2f%n", art, Math.abs(ausgaben));
        }
        System.out.println();
    }

    // gibt Längste Länge für Ausgaben
    private int getLengthfromLongestInstitut(List<Institut> daten) {
        int max = "Institut".length();
        for (Institut institut : daten) {
            if (institut.name().length() > max) {
                max = institut.name().length();
            }
        }
        return max;
    }

    private int getLengthfromLongestProfessor(List<ProfessorAusgabe> daten) {
        int max = "Professor".length();
        for (ProfessorAusgabe professor : daten) {
            if (professor.getName().length() > max) {
                max = professor.getName().length();
            }
        }
        return max;
    }

    private void printLine(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print('-');
        }
        System.out.println();
    }
}