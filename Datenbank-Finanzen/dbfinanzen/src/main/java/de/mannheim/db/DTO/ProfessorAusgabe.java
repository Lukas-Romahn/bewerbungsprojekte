package de.mannheim.db.DTO;

public class ProfessorAusgabe {
    public String professorName;
    public double ausgaben;

    public ProfessorAusgabe(String professorName, double ausgaben) {
        this.professorName = professorName;
        this.ausgaben = ausgaben;
    }

    public String getName() {
        return professorName;
    }

    public double getAusgabe() {
        return ausgaben;
    }
}
