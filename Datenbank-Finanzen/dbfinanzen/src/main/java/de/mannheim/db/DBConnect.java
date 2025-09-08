
package de.mannheim.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import de.mannheim.db.DTO.Institut;
import de.mannheim.db.DTO.Investition;
import de.mannheim.db.DTO.ProfessorAusgabe;

public class DBConnect {

    Connection con;
    private static String url;
    private static String username;
    private static String password;

    private static DBConnect instance;

    public static void initDBConnection(String url, String username, String password) {
        DBConnect.url = url;
        DBConnect.username = username;
        DBConnect.password = password;

    }

    public static DBConnect getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnect(url, username, password);

        }
        return instance;

    }

    private DBConnect(String url, String username, String password) throws SQLException {
        try {
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    // alle Institute
    public List<String> getInstitute() {
        List<String> institute = new ArrayList<>();

        try (Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("Select name from Institut")) {
            while (rs.next()) {

                institute.add(rs.getString("name"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return institute;
    }

    public List<String> getProfs() {
        List<String> profs = new ArrayList<>();

        try (Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name from Professor")) {
            while (rs.next()) {

                profs.add(rs.getString("name"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profs;

    }

    // Kontostand für ein Institut
    public Institut getKontostandFrom(String institutName) {
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(
                    "SELECT i.name, COALESCE(SUM(inv.betrag),0) AS kontostand " +
                            "FROM Investition inv " +
                            "RIGHT JOIN Institut i ON inv.kostenstelle = i.kostenstelle_id " +
                            "WHERE i.name= ? " +
                            "GROUP BY i.name;");
            ps.setString(1, institutName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Institut(rs.getString("name"), rs.getDouble("kontostand"));
            }
        } catch (SQLException e) {
            System.out.println("etwas ist schiefgelaufen");
        }

        return null;

    }

    // Gesamtkontostand über alle Kostenstellen
    public float getKontoStandSum() {
        try (Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(betrag), 0) AS gesamtkontostand FROM Investition")) {

            if (rs.next()) {
                return rs.getFloat("gesamtkontostand");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Kontostand aller Institute
    public List<Institut> getKontoStandFromAll() {
        List<Institut> result = new ArrayList<>();
        try (Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT i.name, COALESCE(SUM(inv.betrag), 0) AS kontostand " +
                                "FROM Investition inv " +
                                "RIGHT JOIN Institut as i ON inv.kostenstelle = i.kostenstelle_id " +
                                "GROUP BY i.name");) {

            while (rs.next()) {
                result.add(new Institut(rs.getString("name"), rs.getDouble("kontostand")));
            }
            return result;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    // Alle Investitionen eines Instituts
    public List<Investition> getAllInvestitionenFromInstitut(String institutName) {
        List<Investition> list = new ArrayList<>();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(
                    "SELECT i.name as institutname, p.name as professor, inv.betrag, inv.art " +
                            "FROM Investition inv " +
                            "RIGHT JOIN Institut i ON inv.kostenstelle = i.kostenstelle_id " +
                            "JOIN Professor p on p.professor_id = inv.professor " +
                            "WHERE i.name= ? ");
            ps.setString(1, institutName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Investition(rs.getString("institutname"), rs.getString("professor"), rs.getFloat("betrag"),
                        rs.getString("art")));
            }
            return list;
        } catch (SQLException e) {
            System.out.println("etwas ist schiefgelaufen");
        }

        return null;
    }

    // Alle Investitionen eines Professors
    public List<Investition> getAllInvestitionenFromProf(String name) {

        List<Investition> list = new ArrayList<>();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(
                    "SELECT i.name as institutname, p.name as professor, inv.betrag, inv.art " +
                            "FROM Investition inv " +
                            "JOIN Institut i ON inv.kostenstelle = i.kostenstelle_id " +
                            "JOIN Professor p on p.professor_id = inv.professor " +
                            "WHERE p.name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Investition(rs.getString("institutname"), rs.getString("professor"), rs.getFloat("betrag"),
                        rs.getString("art")));
            }
            return list;
        } catch (SQLException e) {
            System.out.println("etwas ist schiefgelaufen");
        }
        return null;

    }

    // Gesamtausgaben aller Invesitionen pro Investitionsart
    public float getSumOfAllInvesitionenFromArt(String art) {
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(
                    "SELECT sum(betrag) as summe " +
                            "FROM Investition inv " +
                            "WHERE inv.art = ? " +
                            "GROUP BY inv.art ");
            ps.setString(1, art);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getFloat("summe");
            }
        } catch (SQLException e) {
            System.out.println("etwas ist schiefgelaufen");
        }
        return 0;
    }

    private int getKostenstelleId(String institutName) {
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(
                    "SELECT kostenstelle_id FROM Institut WHERE name = ?");
            ps.setString(1, institutName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("kostenstelle_id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("etwas ist schiefgelaufen");
        }
        return 0;
    }

    private boolean pruefeMiesen(int kostenstelleId, double delta) {
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("SELECT COALESCE(SUM(betrag), 0) FROM Investition WHERE kostenstelle = ?");
            ps.setInt(1, kostenstelleId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            float saldo = rs.getFloat(1);
            return (saldo + delta) >= -1000;
        } catch (SQLException e) {
            System.out.println("etwas ist schiefgelaufen");
            System.out.println(e.getMessage());
        }
        return false;
    }

    private boolean pruefeFakultaetMiesen(double delta) {
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("SELECT COALESCE(SUM(betrag),0) FROM Investition");
            ResultSet rs = ps.executeQuery();
            rs.next();
            float saldo = rs.getFloat(1);
            return (saldo + delta) >= -5000;
        } catch (SQLException e) {
            System.out.println("etwas ist schiefgelaufen");
            System.out.println(e.getMessage());
        }
        return false;
    }

    private void insertInvestition(double betrag, String art, Integer profId, int kostenstelleId) {
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(
                    "INSERT INTO Investition (betrag, art, professor, kostenstelle) VALUES (?, ?, ?, ?)");
            ps.setDouble(1, betrag);
            ps.setString(2, art);
            if (profId != null) {
                ps.setInt(3, profId);
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, kostenstelleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("etwas ist schiefgelaufen");
            System.out.println(e.getMessage());
        }
    }

    // Geld auf ein Institutskonto einzahlen
    public void einzahlen(String institutName, double betrag) {
        try {
            if (institutName.equalsIgnoreCase("Fakultaet")) {
                // 50% auf Fakultät
                double anteilFakultaet = betrag / 2;
                int fakultatsKostenstelle = getKostenstelleId("Fakultaet");

                insertInvestition(anteilFakultaet, null, null, fakultatsKostenstelle);

                // 50% auf Institute verteilen

                PreparedStatement ps = con
                        .prepareStatement("SELECT COUNT(*) FROM Institut WHERE name <> 'Fakultaet'");

                ResultSet rs = ps.executeQuery();
                rs.next();
                int anzahl = rs.getInt(1);
                PreparedStatement ps2 = con
                        .prepareStatement("SELECT kostenstelle_id FROM Institut WHERE name <> 'Fakultaet'");
                rs = ps2.executeQuery();

                double anteilInstitut = betrag / 2 / anzahl;
                while (rs.next()) {
                    int ksId = rs.getInt("kostenstelle_id");
                    insertInvestition(anteilInstitut, null, null, ksId);
                }
            } else {
                int kostenstelleId = getKostenstelleId(institutName);
                insertInvestition(betrag, null, null, kostenstelleId);
            }
        } catch (SQLException e) {
            System.out.println("etwas ist schiefgelaufen");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // Geld von einem Institut auf ein anderes Umbuchen
    public boolean umbuchen(String vonInstitut, String zuInstitut, double betrag) {
        int vonKs = getKostenstelleId(vonInstitut);
        int zuKs = getKostenstelleId(zuInstitut);
        try {
            if (!pruefeMiesen(vonKs, -betrag)) {
                System.out.println("Kostenstelle von " + vonInstitut + " darf nicht tiefer als -1000€ fallen.");
                return false;
            }
            if (!pruefeFakultaetMiesen(-betrag)) {
                System.out.println("Fakultaet darf gesamthaft nicht unter -5000€ fallen.");
                return false;
            }

            insertInvestition(-betrag, "Sonstiges", null, vonKs);
            insertInvestition(betrag, "Sonstiges", null, zuKs);
        } catch (Exception e) {
            System.out.println("etwas ist schiefgelaufen");
            return false;
        }
        return true;
    }

    // Investition für einen Professor buchen
    public Boolean investition(String professorName, double betrag, String art) {
        int kostenstelleId;
        Integer profId = null;
        PreparedStatement ps;

        try {
            if (professorName.equalsIgnoreCase("Dekan")) {
                kostenstelleId = getKostenstelleId("Fakultaet");
                profId = 5;
            } else {

                ps = con.prepareStatement(
                        "SELECT p.professor_id, i.kostenstelle_id " +
                                "FROM Professor p JOIN Institut i ON p.institut = i.institut_id " +
                                "WHERE p.name = ?");
                ps.setString(1, professorName);
                ResultSet rs = ps.executeQuery();
                if (!rs.next())
                    throw new SQLException("Professor nicht gefunden: " + professorName);

                profId = rs.getInt("professor_id");
                kostenstelleId = rs.getInt("kostenstelle_id");
            }

            if (!pruefeMiesen(kostenstelleId, -betrag)) {
                System.out.println("Kostenstelle darf nicht tiefer als -1000€ fallen.");
                return false;
            }
            if (!pruefeFakultaetMiesen(-betrag)) {
                System.out.println("Fakultaet darf gesamthaft nicht unter -5000€ fallen.");
                return false;
            }

            insertInvestition(-betrag, art, profId, kostenstelleId);
        } catch (SQLException e) {
            System.out.println("etwas ist schiefgelaufen");
            System.out.println(e.getMessage());
        }
        return true;
    }

    // Gesamtausgaben aller Institute absteigend sortiert
    public List<Institut> getSumOfAllAusgabenFromAllInstitute() {
        List<Institut> list = new ArrayList<>();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(
                    "SELECT i.name as name, COALESCE(ABS(SUM(inv.betrag)), 0) as summe " +
                            "FROM Investition inv " +
                            "RIGHT JOIN Institut i ON i.kostenstelle_id = inv.kostenstelle " +
                            "WHERE inv.betrag < 0 " + // Nur negative Beträge (Ausgaben)
                            "GROUP BY i.name " +
                            "ORDER BY COALESCE(ABS(SUM(inv.betrag)), 0) DESC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Institut(rs.getString("name"), rs.getFloat("summe")));
            }
            return list;
        } catch (SQLException e) {
            System.out.println("Fehler beim Abrufen der Institutsausgaben: " + e.getMessage());
        }
        return null;
    }

    // Gesamtausgaben aller Professoren absteigend sortiert
    public List<ProfessorAusgabe> getSummOfAllProfs() {
        List<ProfessorAusgabe> list = new ArrayList<>();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(
                    "SELECT p.name, COALESCE(ABS(SUM(inv.betrag)),0) as summe " +
                            "FROM Investition inv " +
                            "JOIN Professor p ON p.professor_id = inv.professor " +
                            "WHERE inv.betrag < 0 " + // Nur negative Beträge (Ausgaben)
                            "GROUP BY p.name " +
                            "ORDER BY COALESCE(ABS(SUM(inv.betrag)),0 ) DESC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ProfessorAusgabe(rs.getString("name"), rs.getFloat("summe")));
            }
            return list;
        } catch (SQLException e) {
            System.out.println("Fehler beim Abrufen der Professorenausgaben: " + e.getMessage());
        }
        return null;
    }

    // Gesamtausgaben (relativ pro Professor) aller Institute absteigend sortiert
    public List<Institut> getSumOfAllInstitutsRelativeToProfCount() {
        List<Institut> list = new ArrayList<>();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(
                    "SELECT i.name as iName, " +
                            "       COALESCE(ABS(SUM(inv.betrag)),0) / COUNT(DISTINCT p.professor_id) as durchschnitt " +
                            "FROM Investition inv " +
                            "JOIN Professor p ON p.professor_id = inv.professor " +
                            "JOIN Institut i ON i.kostenstelle_id = inv.kostenstelle " +
                            "WHERE inv.betrag < 0 " + // Nur negative Beträge (Ausgaben)
                            "GROUP BY i.name " +
                            "ORDER BY COALESCE(ABS(SUM(inv.betrag)),0) / COUNT(DISTINCT p.professor_id) DESC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Institut(rs.getString("iName"), rs.getFloat("durchschnitt")));
            }
            return list;
        } catch (SQLException e) {
            System.out.println("Fehler beim Abrufen der relativen Ausgaben: " + e.getMessage());
        }
        return null;
    }

}
