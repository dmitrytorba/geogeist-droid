package com.example.d.geogeist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class RecyclerController extends RecyclerView.Adapter<RecyclerController.ViewHolder> {

    private List<CensusViewItem> dataset = new ArrayList<>();

    public static class CensusViewItem {
        public String header;
        public String subtitle;
        public List<String> images = new ArrayList<>();

        public CensusViewItem(JSONObject data) throws JSONException {
            header = data.getString("name");
            JSONObject population = data.getJSONObject("population");
            JSONObject occupied = data.getJSONObject("occupied");
            Integer totalPeople = population.getInt("total");
            Integer houses = data.getInt("houses");
            subtitle = withSuffix(totalPeople) + " people in " + withSuffix(houses) + " houses";
            String map = data.optString("map");
            if (map != null) {
                images.add(MainActivity.IMG_URL + map);
            }
            images.add(MainActivity.IMG_URL + population.getString("chart"));
            images.add(MainActivity.IMG_URL + occupied.getString("race_chart"));
            images.add(MainActivity.IMG_URL + occupied.getString("household_chart"));
            images.add(MainActivity.IMG_URL + occupied.getString("finance_chart"));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView header;
        public TextView subtitle;
        public ListView imagesView;
        public ArrayAdapter<String> imagesAdapter;

        public ViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            imagesView = itemView.findViewById(R.id.image_list);
            imagesAdapter = new ArrayAdapter<>(imagesView.getContext(), android.R.layout.simple_list_item_1);
            imagesView.setAdapter(imagesAdapter);
        }
    }

    public RecyclerController() {

    }

    @Override
    public RecyclerController.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
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
        holder.imagesAdapter.clear();
        holder.imagesAdapter.addAll(item.images);
        holder.imagesAdapter.notifyDataSetChanged();
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

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f%c",
                count / Math.pow(1000, exp),
                "KMGTPE".charAt(exp-1));
    }
}
