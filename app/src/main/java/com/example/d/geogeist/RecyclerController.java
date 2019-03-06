package com.example.d.geogeist;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class RecyclerController extends RecyclerView.Adapter<RecyclerController.ViewHolder> {

    private List<CensusViewItem> dataset = new ArrayList<>();
    private ImageLoader loader;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView header;
        public TextView subtitle;
        public NetworkImageView map;
        public NetworkImageView populationChart;
        public NetworkImageView raceChart;
        public NetworkImageView householdChart;
        public NetworkImageView financeChart;


        public ViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            map = itemView.findViewById(R.id.map);
            populationChart = itemView.findViewById(R.id.populationChart);
            raceChart = itemView.findViewById(R.id.raceChart);
            householdChart = itemView.findViewById(R.id.householdChart);
            financeChart = itemView.findViewById(R.id.financeChart);
        }
    }

    @Override
    public RecyclerController.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        loader = VolleySingleton.getInstance(context).getImageLoader();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CensusViewItem item = dataset.get(position);
        holder.header.setText(item.header);
        holder.subtitle.setText(item.subtitle);
        if (item.map != null) {
            holder.map.setImageUrl(item.map, loader);
        } else {
            holder.map.setVisibility(View.GONE);
        }
        holder.populationChart.setImageUrl(item.populationChart, loader);
        holder.householdChart.setImageUrl(item.householdChart, loader);
        holder.raceChart.setImageUrl(item.raceChart, loader);
        holder.financeChart.setImageUrl(item.financeChart, loader);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void loadData(JSONObject data) {
        try {
            dataset.clear();

            JSONObject tract = data.optJSONObject("tract");
            if (tract != null) {
                CensusViewItem item = new CensusViewItem(tract);
                dataset.add(item);
            }

            JSONObject place = data.optJSONObject("place");
            if (place != null) {
                CensusViewItem item = new CensusViewItem(place);
                dataset.add(item);
            }

            JSONObject county = data.optJSONObject("county");
            if (county != null) {
                CensusViewItem item = new CensusViewItem(county);
                dataset.add(item);
            }

            JSONObject state = data.optJSONObject("state");
            if (state != null) {
                CensusViewItem item = new CensusViewItem(state);
                dataset.add(item);
            }

            this.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
