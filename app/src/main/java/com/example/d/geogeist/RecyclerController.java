package com.example.d.geogeist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class RecyclerController extends RecyclerView.Adapter<RecyclerController.ViewHolder> {

    private List<String> dataset = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
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
        holder.textView.setText(dataset.get(position));
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
                String tractName = tract.getString("name");
                dataset.add(tractName);
            }

            JSONObject place = data.optJSONObject("place");
            if (place != null) {
                String placeName = place.getString("name");
                dataset.add(placeName);
            }

            JSONObject county = data.optJSONObject("county");
            if (county != null) {
                String countyName = county.getString("name");
                dataset.add(countyName);
            }

            JSONObject state = data.optJSONObject("state");
            if (state != null) {
                String stateName = state.getString("name");
                dataset.add(stateName);
            }
            this.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
