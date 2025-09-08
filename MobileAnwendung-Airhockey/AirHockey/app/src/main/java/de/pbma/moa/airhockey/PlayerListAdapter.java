package de.pbma.moa.airhockey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PlayerListAdapter extends BaseAdapter {
    private List<String> players;
    private Context context;

    public PlayerListAdapter(Context context, List<String> players){
        this.context=context;
        this.players=players;
    }
    @Override
    public int getCount() {
       return players.size();
    }

    @Override
    public Object getItem(int position) {
        return players.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.player_list_item,parent,false);
        }
        String currentItem= (String) getItem(position);

        TextView tvPlayerInfo=convertView.findViewById(R.id.tvPlayerInfoListItem);
        TextView tvPlayer=convertView.findViewById(R.id.tvPlayerListItem);

        tvPlayerInfo.setText(String.valueOf(position+1));
        tvPlayer.setText(currentItem);

        return convertView;
    }
}
