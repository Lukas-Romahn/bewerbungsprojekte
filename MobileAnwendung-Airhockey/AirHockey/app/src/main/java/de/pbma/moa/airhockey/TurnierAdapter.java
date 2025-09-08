package de.pbma.moa.airhockey;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

import de.pbma.moa.database.Tournament;


public class TurnierAdapter extends BaseAdapter {
        private Context context;
        private List<Tournament> items;

        private boolean showFillGames = false;
        public TurnierAdapter(Context context, List<Tournament> items) {
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
                int id = R.layout.tournament_list_item;
                convertView = LayoutInflater.from(context).inflate(R.layout.tournament_list_item, parent, false);
            }

            Tournament currentItem = (Tournament) getItem(position);

            TextView tvName = convertView.findViewById(R.id.tvName);
            TextView tvTid = convertView.findViewById(R.id.tvTid);
            TextView tvState =convertView.findViewById(R.id.tvState);
            TextView tvTimestamp = convertView.findViewById(R.id.tvTimeStampTournamentList);
            tvTid.setText(String.valueOf(currentItem.getTurnierId()));

            tvTimestamp.setText(currentItem.timestamp);
            tvName.setText(currentItem.getName());
            if(currentItem.getIsFinished()){
                tvState.setText("Turnier beendet");
               tvState.setTextColor(context.getColor(R.color.green));
            }else{
                tvState.setText("Turnier läuft");
                tvState.setTextColor(context.getColor(R.color.red));
            }

            return convertView;
        }

        public void addAll(List<Tournament> items){
            this.items = items;
            Collections.sort(this.items);
            notifyDataSetChanged();
        }
    }



