package de.pbma.moa.airhockey;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.pbma.moa.database.Game;
import de.pbma.moa.tournament2.TournamentLogicHandler;

public class GameAdapter extends BaseAdapter {
    private List<Game> spiele;
    private Context context;

    public GameAdapter(Context context,List<Game> spiele){
        this.context=context;
        this.spiele=spiele;
    }

    @Override
    public int getCount() {
        return spiele.size();
    }

    @Override
    public Object getItem(int position) {
        return spiele.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.game_list_item,parent,false);
        }
        Game currentItem= (Game) getItem(position);

        TextView tvGameIndex= convertView.findViewById(R.id.tvGameIndex);
        String gameTag;
        if(currentItem.gameIndex==1) {
            gameTag="Finale";
        }else if(currentItem.gameIndex==2){
            gameTag="Halbfinale";
        }
        else if(currentItem.gameIndex==3) {
            if(spiele.size() == 2){
                gameTag = "Halbfinale";
            }else{
                gameTag="Viertelfinale";
            }
        }else{
            gameTag="Spiel " + currentItem.gameIndex;

        }
        tvGameIndex.setText(gameTag);
        TextView tvPlayer1= convertView.findViewById(R.id.tvPlayer1);
        tvPlayer1.setText(currentItem.playerOneName);
        TextView tvPlayer2= convertView.findViewById(R.id.tvPlayer2);
        tvPlayer2.setText(currentItem.playerTwoName);
        TextView tvPlayer1Goals= convertView.findViewById(R.id.tvPlayer1Goals);
        tvPlayer1Goals.setText(String.valueOf(currentItem.playerOneGoals));
        TextView tvPlayer2Goals= convertView.findViewById(R.id.tvPlayer2Goals);
        tvPlayer2Goals.setText(String.valueOf(currentItem.playerTwoGoals));
        TextView tvState= convertView.findViewById(R.id.tvGameState);
        if(currentItem.isFinished){
            tvState.setText("Abgeschlossen");
            tvState.setTextColor(context.getColor(R.color.green));
        }else{
            tvState.setText("Noch nicht Angetreten");
            tvState.setTextColor(context.getColor(R.color.red));
        }
    return convertView;
    }
}
