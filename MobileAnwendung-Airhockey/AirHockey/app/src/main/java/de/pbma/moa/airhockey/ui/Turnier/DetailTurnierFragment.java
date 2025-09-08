package de.pbma.moa.airhockey.ui.Turnier;

import android.icu.util.EthiopicCalendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.pbma.moa.airhockey.GameAdapter;
import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.TurnierAdapter;
import de.pbma.moa.airhockey.databinding.FragmentTurnierDetailBinding;
import de.pbma.moa.database.AppDatabase;
import de.pbma.moa.database.Game;
import de.pbma.moa.database.GameDAO;
import de.pbma.moa.database.Tournament;

public class DetailTurnierFragment extends Fragment {
    FragmentTurnierDetailBinding binding;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding=FragmentTurnierDetailBinding.inflate(inflater,container,false);


            String name= DetailTurnierFragmentArgs.fromBundle(getArguments()).getName();
            String tid= DetailTurnierFragmentArgs.fromBundle(getArguments()).getTid();

            AppDatabase appDatabase= AppDatabase.getInstance(requireContext());
            List<Game> spiele=new ArrayList<>();

            GameAdapter adapter= new GameAdapter(requireContext(),spiele);
            new TournamentIDThread(tid,appDatabase,spiele,adapter).start();


            binding.lvGamelist.setAdapter(adapter);





        return binding.getRoot();
    }

    public class TournamentIDThread extends Thread{
        private AppDatabase appDatabase;
        private String tid;
        private List<Game> spiele;
        private GameAdapter adapter;

        public TournamentIDThread(String tid, AppDatabase appDatabase, List<Game> spiele, GameAdapter adapter){
            this.appDatabase=appDatabase;
            this.tid=tid;
            this.spiele=spiele;
            this.adapter=adapter;
        }
        @Override
        public void run() {
            List<Game> gamesFromDB=appDatabase.gameDAO().getGamesFromTournament(tid);
            new Handler(Looper.getMainLooper()).post(()->{
                spiele.clear();
                spiele.addAll(gamesFromDB);
                spiele.removeIf(item -> item.fillGame);
                adapter.notifyDataSetChanged();
            });

        }
    }




}
