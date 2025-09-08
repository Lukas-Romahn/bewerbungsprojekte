package de.pbma.moa.airhockey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Collections;
import java.util.List;

import de.pbma.moa.database.Tournament;
import de.pbma.moa.tournament2.Player;


public class PlayerScoreAdapter extends BaseAdapter {
        private Context context;
        private List<Player> items;

        public PlayerScoreAdapter(Context context, List<Player> items) {
            this.context = context;
            this.items = items;
            Collections.sort(items);
        }


        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.tournament_player_list_item, parent, false);
            }

            Player currentItem = (Player) getItem(position);
            TextView tvPlayerName = convertView.findViewById(R.id.tvPlayerNameTournamentList);
            TextView tvPlayerScore = convertView.findViewById(R.id.tvPlayerScoreTournamentList);

            tvPlayerName.setText(currentItem.Name);
            tvPlayerScore.setText(String.valueOf(currentItem.score));

            return convertView;
        }

    }



