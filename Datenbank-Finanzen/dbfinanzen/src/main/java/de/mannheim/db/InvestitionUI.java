package de.mannheim.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import de.mannheim.db.DTO.Investition;

public class InvestitionUI {

    DBConnect dbConnect;
    int wahl;
    Scanner scanner = new Scanner(System.in);

    public void menu() {
        try {
            dbConnect = DBConnect.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        do {
            System.out.println("(1) Alle Investitionen eines Instituts anzeigen");
            System.out.println("(2) Alle Investitionen eines Professors");
            System.out.println("(3) Investition tätigen");
            System.out.println("(4) zurück zur Startseite");

            wahl = inputChecker.getInt();
            switch (wahl) {
                case 1 -> showInvestitionfromInstitut();
                case 2 -> showInvestitionFromProf();
                // case 2 ->;
                case 3 -> showDoInvestInterface();
                case 4 -> { return;}

            }

        } while (wahl != 0);
    }

    private void showInvestitionfromInstitut() {
        String institutname = choseOption(getInstitute());
        List<Investition> investitionen = dbConnect.getAllInvestitionenFromInstitut(institutname);
        System.out.printf("%-" + institutname.length() + "s | %-18s | %-15s | %-15s%n", "Institut", "Professor",
                "Betrag", "Art");
        for (int i = 0; i < institutname.length() + 50; i++) {
            System.out.print('-');
        }
        System.out.println();

        for (Investition inv : investitionen) {
            System.out.printf(
                    "%-20s | %-18s | %-15.2f | %-15s%n",
                    inv.institutname(),
                    inv.professor(),
                    inv.betrag(),
                    inv.art());
        }
        System.out.println();
    }

    private void showInvestitionFromProf() {

        System.out.println();
        List<String> Profs = dbConnect.getProfs();
        System.out.println("Bitte wählen Sie den Prof aus: \n");

        String selectedProf = choseOption(Profs);
        System.out.println();

        List<Investition> investitionen = dbConnect.getAllInvestitionenFromProf(selectedProf);
        int length = getLengthfromLongestElement(investitionen);
        if (investitionen == null) {
            System.out.println();
            System.out.println("Es gibt keinen Professor mit diesem Namen");
            System.out.println();
        }
        if (length == 0) {
            System.out.println("Der Professor hat keine Investitionen getätigt");
            System.out.println();
            return;
        }
        System.out.printf("%-" + length + "s | %-18s | %-15s | %-15s%n", "Institut", "Professor", "Betrag", "Art");
        for (int i = 0; i < length + 45; i++) {
            System.out.print('-');
        }
        System.out.println();
        for (Investition inv : investitionen) {
            System.out.printf(
                    "%-20s | %-18s | %-15.2f | %-15s%n",
                    inv.institutname(),
                    inv.professor(),
                    inv.betrag(),
                    inv.art());
        }
        System.out.println();
    }

    private int getLengthfromLongestElement(List<Investition> institute) {
        int max = 0;
        for (Investition s : institute) {
            int temp = s.institutname().length();
            if (temp > max) {
                max = temp;
            }
        }
        return max;
    }

    private List<String> getInstitute() {
        List<String> inst = dbConnect.getInstitute();
        return inst;
    }

    private void listOptions(List<String> institut) {
        int i = 1;
        for (String ins : institut) {

            System.out.println("(" + i + ") " + ins);
            i++;
        }
    }

    private String choseOption(List<String> options) {
        listOptions(options);
        do {
            wahl = scanner.nextInt();
        } while (wahl <= 0 || wahl > options.size());
        return options.get(wahl - 1);
    }

    private void showDoInvestInterface() {
        List<String> Profs = dbConnect.getProfs();
        System.out.println("Bitte wählen Sie den String aus, auf den die Investition verbucht werden soll: \n");

        String selectedProf = choseOption(Profs);

        double betrag;
        do {

            betrag = inputChecker.getDouble();

            if (betrag <= 0) {
                System.out.println("Betrag muss größer als 0 sein. Bitte erneut eingeben: ");
            }
        } while (betrag <= 0);

        String investArt = choseOption(List.of("Hardware", "Infrastruktur", "Software", "Sonstiges"));

        if (dbConnect.investition(selectedProf, betrag, investArt)) {
            // System.out.printf("\nInvestition von " + selectedProf + " der Art " + investArt + " über einen Betrag von "
            //         + betrag+ "€ \n");
            System.out.printf("\nInvestition von %s der Art %s über einen Betrag von %.2f€ \n\n", selectedProf, investArt, betrag);
        }

    }

}
