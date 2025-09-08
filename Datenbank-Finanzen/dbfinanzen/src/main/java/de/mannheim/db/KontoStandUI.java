package de.mannheim.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import de.mannheim.db.DTO.Institut;

public class KontoStandUI {
    DBConnect dbConnect;
    int wahl;
    Scanner scanner = new Scanner(System.in);

    public void menu() {
        try {
            dbConnect = DBConnect.getInstance();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        do {

            System.out.println();
            System.out.println("(1) Kontostand eines Instituts sehen");
            System.out.println("(2) Kontostand über alle Kostenstellen sehen");
            System.out.println("(3) Kontostände aller Institute sehen");
            System.out.println("(4) zurück zur Startseite");

            wahl = inputChecker.getInt();
            switch (wahl) {
                case 1 -> choseInstitut();
                case 2 -> showKontoStandSum();
                case 3 -> showKontoStandfromAll();
                case 4 -> {return;}

            }

        } while (wahl != 0);
    }

    private void showKontoStandSum() {
        float sum = dbConnect.getKontoStandSum();
        System.out.println();
        System.out.println(sum);
    }

    private void showKontoStandfromAll() {
        List<Institut> insitute = dbConnect.getKontoStandFromAll();
        int length = getLengthfromLongestElement(getInstitute());
        System.out.printf("%-" + length + "s | %-10s%n", "Institut", "Kontostand");
        for (int i = 0; i < length + 13; i++) {
            System.out.print('-');
        }
        System.out.print('\n');
        for (Institut ins : insitute) {
            System.out.printf("%-" + length + "s | %-10.2f%n", ins.name(), ins.Kontostand());
        }
    }

    private List<String> getInstitute() {
        List<String> inst = dbConnect.getInstitute();
        return inst;
    }

    private void showInstitute(List<String> institute) {
        int i = 1;
        System.out.println();
        for (String s : institute) {

            System.out.println("(" + i + ")" + s);
            i++;
        }
    }

    private void choseInstitut() {
        List<String> institute = getInstitute();
        showInstitute(institute);
        do {
            wahl = scanner.nextInt();
        } while (wahl < 1 || wahl > institute.size());
        Institut institut = dbConnect.getKontostandFrom(institute.get(wahl - 1));
        if (institut == null) {
            System.out.println("Dieses Insititut hat keinen Kontostand Zahlen sie geld ein");
            return;
        }
        System.out.println();
        System.out.println(institut.name() + " : " + institut.Kontostand());
    }

    private int getLengthfromLongestElement(List<String> institute) {
        int max = 0;
        for (String s : institute) {
            int temp = s.length();
            if (temp > max) {
                max = temp;
            }
        }
        return max;
    }

}
