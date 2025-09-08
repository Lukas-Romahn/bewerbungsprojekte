package de.pbma.moa.airhockey.ui.Turnier;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.databinding.FragmentMainBinding;
import de.pbma.moa.airhockey.databinding.FragmentTurnierBinding;

public class TurnierFragment extends Fragment {
    private FragmentTurnierBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentTurnierBinding.inflate(inflater, container, false);


        binding.btnJoinTurnier.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_turnierFragment_to_joinTurnierFragment);

        });

        binding.btnTurnierErstellen.setOnClickListener(v -> {

            NavHostFragment.findNavController(this).navigate(R.id.action_turnierFragment_to_createTurnierFragment);
        });
        binding.btnShowTurnier.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_turnierFragment_to_showTurnierFragment);
        });
        binding.btnTurnierWeiter.setOnClickListener(v ->{
            NavHostFragment.findNavController(this).navigate(R.id.action_turnierFragment_to_loadTurnierTableFragment);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}