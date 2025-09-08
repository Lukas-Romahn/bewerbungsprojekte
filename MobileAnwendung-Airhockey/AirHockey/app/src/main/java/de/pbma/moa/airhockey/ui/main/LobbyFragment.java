package de.pbma.moa.airhockey.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.databinding.FragmentMainBinding;

public class LobbyFragment extends Fragment {

    FragmentMainBinding binding;
    NavController navController;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentMainBinding.inflate(inflater,container,false);



        binding.btnCreateLobby.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_lobbyFragment_to_lobbyCreateFragment);

        });

        binding.btnJoinLobby.setOnClickListener(v -> {

            NavHostFragment.findNavController(this).navigate(R.id.action_lobbyFragment_to_lobbyJoinFragment);
        });
        binding.btnTurnier.setOnClickListener(v -> {

            NavHostFragment.findNavController(this).navigate(R.id.action_lobbyFragment_to_turnierFragment);
        });

                return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}