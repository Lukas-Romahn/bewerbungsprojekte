package de.pbma.moa.airhockey.ui.Turnier;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

import de.pbma.moa.airhockey.ListItem;
import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.TurnierAdapter;
import de.pbma.moa.airhockey.databinding.FragmentShowTurnierBinding;
import de.pbma.moa.database.AppDatabase;
import de.pbma.moa.database.Game;
import de.pbma.moa.database.Tournament;

public class ShowTurnierFragment extends Fragment {
    FragmentShowTurnierBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding=FragmentShowTurnierBinding.inflate(inflater,container,false);

        ListView listView = binding.lvTurnierlist;
        AppDatabase appDatabase= AppDatabase.getInstance(requireContext());

        ArrayList<Tournament>itemsT= new ArrayList<>();
        TurnierAdapter adapter = new TurnierAdapter(getContext(), itemsT);
        listView.setAdapter(adapter);
        TournamentThread tt= new TournamentThread(appDatabase,adapter,itemsT);
        tt.start();

        listView.setOnItemClickListener((parent, v, position, id) -> {
            Tournament selectedItem = (Tournament) adapter.getItem(position);

            ShowTurnierFragmentDirections.ActionShowTurnierFragmentToDetailTurnierFragment action =
                    ShowTurnierFragmentDirections
                            .actionShowTurnierFragmentToDetailTurnierFragment(selectedItem.getName(), selectedItem.getTurnierId());
            NavHostFragment.findNavController(this).navigate(action);

        });

        return binding.getRoot();

    }
}