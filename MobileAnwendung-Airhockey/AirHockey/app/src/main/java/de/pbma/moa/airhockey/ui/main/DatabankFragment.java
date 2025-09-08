package de.pbma.moa.airhockey.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.List;
import java.util.stream.Collectors;

import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.databinding.FragmentDatabankBinding;
import de.pbma.moa.database.AppDatabase;
import de.pbma.moa.database.Game;

public class DatabankFragment extends Fragment {

    FragmentDatabankBinding binding;
    NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDatabankBinding.inflate(inflater, container, false);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        AppDatabase database = AppDatabase.getInstance(requireContext());
        String uuid = "1234";
        ListView highscore_list = binding.databankList;



        new Thread(() -> {

        }).start();

        return binding.getRoot();
    }
}