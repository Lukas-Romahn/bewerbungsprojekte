package de.pbma.moa.airhockey.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.pbma.moa.airhockey.R;
import de.pbma.moa.airhockey.databinding.FragmentOptionsBinding;


public class OptionsFragment extends Fragment {
    private String username;
    int targetGoals;
    private int playstyle;
    FragmentOptionsBinding binding;
    private int mDefaultColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding= FragmentOptionsBinding.inflate(inflater,container,false);

     binding.switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
        playstyle= isChecked? 1:0; //wenn isChecked true ist wird swPlaystyle 1 sonst 0

     });
        SharedPreferences preferences = getActivity().getSharedPreferences("PlayerInfo", Context.MODE_PRIVATE);
        String usernameSet=preferences.getString("Username","null");
        int targetGoalsSet=preferences.getInt("targetGoals", 0);
        if(preferences.getInt("PlayStyle",2)==1){
            binding.switch1.setChecked(true);
        }else{
            binding.switch1.setChecked(false);
        }


        if(!usernameSet .equals("null")){
            binding.etUsername.setText(usernameSet);
        }

        if(!(targetGoalsSet == 0)){
            binding.etGoalGoal.setText(String.valueOf(targetGoalsSet));
        }



     binding.btnSave.setOnClickListener( v->{
         SharedPreferences.Editor editor= preferences.edit();
         String username=binding.etUsername.getText().toString();
         String targetGoalText = binding.etGoalGoal.getText().toString();
         if(!targetGoalText.isEmpty()){
             try{
                 targetGoals = Integer.valueOf(targetGoalText);
             }catch(Exception e){
                 Toast.makeText(getContext(), "Es wird eine Zahl erwartet", Toast.LENGTH_SHORT).show();
                 return;
             }
             if(targetGoals <= 0){
                 Toast.makeText(getContext(), "nur positive Zahlen sind erlaubt", Toast.LENGTH_SHORT).show();
             }
            editor.putInt("targetGoals", targetGoals);
         }

         if (!username.isBlank()) {
             if(!username.contains("/")) {


                 if (binding.etUsername.getText().toString().length() >= 4) {

                     editor.putString("Username", binding.etUsername.getText().toString());
                     editor.putInt("PlayStyle",playstyle);
                     editor.apply();

                     Toast.makeText(getActivity(), "Die Einstellungen wurde gespeichert", Toast.LENGTH_SHORT).show();


                 } else {
                     Toast.makeText(getActivity(), "Der Username muss mindestens 4 Zeichen lang sein", Toast.LENGTH_LONG).show();

                 }
             }else{
                    Toast.makeText(getActivity(),"Der Username darf nicht das zeichen / enthalten",Toast.LENGTH_LONG).show();
                 }

         }else{
             Toast.makeText(getActivity(),"Geben sie einen Namen ein",Toast.LENGTH_LONG).show();

         }

     });
        return binding.getRoot();

    }


    @Override
    public void onPause() {
        super.onPause();

    }
}
