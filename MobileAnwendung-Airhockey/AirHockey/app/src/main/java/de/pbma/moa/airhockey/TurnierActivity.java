package de.pbma.moa.airhockey;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import de.pbma.moa.airhockey.databinding.ActivityTurnierBinding;

public class TurnierActivity extends AppCompatActivity {

    private ActivityTurnierBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTurnierBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration= new AppBarConfiguration.Builder(navController.getGraph()).build();

        SharedPreferences preferences= this.getSharedPreferences("PlayerInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        if(preferences.getString("Username","0").equals("0")) {
            editor.putString("Username","Gast");
        }
        editor.apply();
        Toolbar toolbar= findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar,
                navController,
                appBarConfiguration);
        ImageButton btnSettings= findViewById(R.id.btnSetting);
        btnSettings.setOnClickListener(v -> {
            navController.navigate(R.id.optionsFragment);
        });


    }
}