package de.pbma.moa.airhockey.ui.Turnier;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import de.pbma.moa.airhockey.R;
import de.pbma.moa.tournament2.TournamentLogicHandler;

import java.util.ArrayList;

import de.pbma.moa.airhockey.TurnierAdapter;
import de.pbma.moa.airhockey.databinding.FragmentLoadTurnierBinding;
import de.pbma.moa.database.AppDatabase;
import de.pbma.moa.database.Tournament;


public class LoadTurnierTableFragment extends Fragment {

    FragmentLoadTurnierBinding binding;
    ArrayList<Tournament> itemsT= new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentLoadTurnierBinding.inflate(inflater,container,false);
        TurnierAdapter adapter = new TurnierAdapter(requireContext(), itemsT);
        binding.lvShowTournamentsToReJoin.setAdapter(adapter);
        binding.lvShowTournamentsToReJoin.setOnItemClickListener((parent, v, position, id) -> {
            Tournament selectedItem = (Tournament) adapter.getItem(position);
            TournamentLogicHandler.getInstance().loadTournamentById(requireContext(), selectedItem.getTurnierId());
            NavHostFragment.findNavController(this).navigate(R.id.action_loadTurnierTableFragment_to_weiterSpielenFragment);
        });
        TournamentThread tt= new TournamentThread(AppDatabase.getInstance(requireContext()),adapter,itemsT);

        tt.start();
        return binding.getRoot();
    }
}