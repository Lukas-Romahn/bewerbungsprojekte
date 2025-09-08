package de.pbma.moa.airhockey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import java.util.UUID;

import de.pbma.moa.airhockey.databinding.ActivityLobbyBinding;
import de.pbma.moa.airhockey.ui.Turnier.WeiterSpielenFragmentDirections;
import de.pbma.moa.airhockey.ui.main.LobbyFragment;
import de.pbma.moa.airhockey.ui.main.LobbyFragmentDirections;
import de.pbma.moa.database.AppDatabase;
import de.pbma.moa.database.Game;
import de.pbma.moa.database.Tournament;

public class LobbyActivity extends AppCompatActivity {
    ActivityLobbyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLobbyBinding.inflate(getLayoutInflater());

        try {
            setContentView(binding.getRoot());
        } catch (Exception e) {
            e.printStackTrace();
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

        SharedPreferences preferences = this.getSharedPreferences("PlayerInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (preferences.getString("Username", "0").equals("0")) {
            editor.putString("Username", "Gast");
        }
        editor.apply();

        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar,
                navController,
                appBarConfiguration);

        NavInflater navInflater = navController.getNavInflater();
        NavGraph turnierGraph = navInflater.inflate(R.navigation.lobby_navgraph);
        navController.setGraph(turnierGraph);

        Intent intent = getIntent();
        String lookInSharedPrefrences = intent.getStringExtra("wasPlayed");
        String playerType = intent.getStringExtra("playerType");

        if(lookInSharedPrefrences == null) {
            lookInSharedPrefrences = "";
        }

        if(playerType == null) {
            playerType = "";
        }

        if(lookInSharedPrefrences.equals("Turnier")){
            String turnierID = intent.getStringExtra("tournamentCode");
            if(playerType.equals("Client")){
                LobbyFragmentDirections.ActionLobbyFragmentToLobbyTurnierFragment action = LobbyFragmentDirections.actionLobbyFragmentToLobbyTurnierFragment(turnierID);
                navController.navigate(action);
            }else{
                navController.navigate(R.id.action_lobbyFragment_to_afterGameTableFragment);
            }
        }
        if (lookInSharedPrefrences.equals("CasualGame")) {
            if(playerType.equals("Table"))
                navController.navigate(R.id.action_lobbyFragment_to_afterGameTableFragment);
            }

        ImageButton btnSettings = findViewById(R.id.btnSetting);
        btnSettings.setOnClickListener(v -> {

            navController.navigate(R.id.optionsFragment);
        });

        String myUUID;
        if (preferences.getString("UUID", "null").equals("null")) {
            myUUID = UUID.randomUUID().toString();

            editor.putString("UUID", myUUID);
            editor.apply();

        }
    }
}