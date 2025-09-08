package de.pbma.moa.airhockey.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.databinding.FragmentAfterGameBinding;
import de.pbma.moa.database.Game;
import de.pbma.moa.tournament2.TournamentLogicHandler;


public class AfterGameFragment extends Fragment {
    FragmentAfterGameBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =FragmentAfterGameBinding.inflate(inflater,container,false);

        Intent gameIntent = requireActivity().getIntent();
        String isTurnier = gameIntent.getStringExtra("wasPlayed");
        String Spielergebnis = gameIntent.getStringExtra("SpielErgebnis");

        if(isTurnier != null && Spielergebnis != null && isTurnier.equals("CasualGame")){

            String player1 = gameIntent.getStringExtra("Player1");
            String player2 = gameIntent.getStringExtra("Player2");
            binding.tvPlayer1AfterGame.setText(player1);
            binding.tvPlayer2AfterGame.setText(player2);

            binding.tvScore1.setText(String.valueOf(Spielergebnis.split(":")[0]));
            binding.tvScore2.setText(String.valueOf(Spielergebnis.split(":")[1]));

        }else if(isTurnier != null && isTurnier.equals("Turnier")){
           Game game = TournamentLogicHandler.getInstance().getLastGameInfo();

            binding.tvPlayer1AfterGame.setText(game.playerOneName);
            binding.tvPlayer2AfterGame.setText(game.playerTwoName);

            binding.tvScore1.setText(String.valueOf(game.playerOneGoals));
            binding.tvScore2.setText(String.valueOf(game.playerTwoGoals));
        }
        binding.btnNextAfterGame.setOnClickListener(v -> {
            if(isTurnier.equals("Turnier")) {
                NavHostFragment.findNavController(this).popBackStack(R.id.afterGameTableFragment, true);
                NavHostFragment.findNavController(this).navigate(R.id.weiterSpielenFragment);
            }
            else{
                NavHostFragment.findNavController(this).navigate(R.id.lobbyFragment);
            }
        });

        return binding.getRoot();
    }
}