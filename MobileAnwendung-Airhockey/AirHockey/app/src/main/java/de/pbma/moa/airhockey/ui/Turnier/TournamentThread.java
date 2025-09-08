package de.pbma.moa.airhockey.ui.Turnier;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import de.pbma.moa.airhockey.TurnierAdapter;
import de.pbma.moa.database.AppDatabase;
import de.pbma.moa.database.Tournament;

public class TournamentThread extends Thread{
    AppDatabase appDatabase;
    TurnierAdapter adapter;
    ArrayList<Tournament> items;

    public TournamentThread(AppDatabase appDatabase,TurnierAdapter adapter,ArrayList<Tournament> items){
        this.appDatabase=appDatabase;
        this.adapter=adapter;
        this.items=items;

    }

    @Override
    public void run() {
        List<Tournament> tournaments= appDatabase.tournamentDAO().getAllTournaments();
        new Handler(Looper.getMainLooper()).post(() -> {
            items.clear(); // Alte Daten entfernen
            items.addAll(tournaments); // Neue Daten hinzufügen
            adapter.addAll(tournaments);
            adapter.notifyDataSetChanged(); // Adapter aktualisieren
        });
    }
}
