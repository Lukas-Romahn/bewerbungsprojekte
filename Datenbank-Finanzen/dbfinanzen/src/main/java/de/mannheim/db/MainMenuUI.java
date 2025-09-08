package de.mannheim.db;

import java.sql.SQLException;
import java.util.Scanner;

public class MainMenuUI {
    Scanner scanner = new Scanner(System.in);
    int wahl;
    KontoStandUI kStandUI;
    InvestitionUI investUI;
    GesamtausgabenUI gesUI;
    EinzahlungUI einzUI;
    UmbuchungUI umbuUI;
    ConfigReader configReader;
    DBConnect dbConnect;

    public MainMenuUI() {

        kStandUI = new KontoStandUI();
        investUI = new InvestitionUI();
        gesUI = new GesamtausgabenUI();
        einzUI = new EinzahlungUI();
        umbuUI = new UmbuchungUI();

    }

    public void initMainMenu() {

        kStandUI = new KontoStandUI();
        investUI = new InvestitionUI();
        gesUI = new GesamtausgabenUI();
        einzUI = new EinzahlungUI();
        configReader = new ConfigReader();
        getDBData(dbConnect);
    }

    private void getDBData(DBConnect db) {
        boolean connected = false;
        do {
            System.out.println("geben sie den Benutzer für die Datenbank ein");
            String username = scanner.next();
            System.out.println("Geben sie das Password ein");
            String password = scanner.next();
            DBConnect.initDBConnection(configReader.readConfig(), username, password);
            try {
                db = DBConnect.getInstance();
                connected = true;
            } catch (SQLException e) {
                connected = false;
                System.out.println("Verbindung fehlgeschlagen: " + e.getMessage());

            }
        } while (!connected);
    }

    public void mainMenu() {

        do {
            System.out.println("-------------------------------------");
            System.out.println("Willkommen beim Kostenstellenverwaltung");
            System.out.println("-------------------------------------");
            System.out.println("Wie kann ich ihnen helfen?");
            System.out.println("(1) Kontostände sehen");
            System.out.println("(2) Investitionen verwalten");
            System.out.println("(3) Gesamtausgaben sehen");
            System.out.println("(4) Geld einzahlen");
            System.out.println("(5) Geld umbuchen");
            System.out.println("(0) Beenden");

            wahl = inputChecker.getInt();

            switch (wahl) {

                case 1 -> kStandUI.menu();
                case 2 -> investUI.menu();
                case 3 -> gesUI.menu();
                case 4 -> einzUI.menu();
                case 5 -> umbuUI.menu();
                case 0 -> {
                    System.out.println("Bis zum nächsten Mal!");
                    return;
                }
                default -> System.out.println("Falsche Wahl. Bitte nochmal versuchen!");

            }

        } while (wahl != 0);

    }

}
