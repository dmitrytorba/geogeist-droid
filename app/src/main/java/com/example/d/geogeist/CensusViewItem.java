package com.example.d.geogeist;

import org.json.JSONException;
import org.json.JSONObject;

public class CensusViewItem {
    public String header;
    public String subtitle;
    public String map;
    public String populationChart;
    public String raceChart;
    public String householdChart;
    public String financeChart;

    public CensusViewItem(JSONObject data) throws JSONException {
        header = data.getString("name");
        JSONObject population = data.getJSONObject("population");
        JSONObject occupied = data.getJSONObject("occupied");
        Integer totalPeople = population.getInt("total");
        Integer houses = data.getInt("houses");
        subtitle = withSuffix(totalPeople) + " people in " + withSuffix(houses) + " houses";
        String mapData = data.optString("map");
        if (mapData != "") {
            map = MainActivity.IMG_URL + mapData;
        }
        populationChart = MainActivity.IMG_URL + population.getString("chart");
        raceChart = MainActivity.IMG_URL + occupied.getString("race_chart");
        householdChart = MainActivity.IMG_URL + occupied.getString("household_chart");
        financeChart = MainActivity.IMG_URL + occupied.getString("finance_chart");
    }


    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f%c",
                count / Math.pow(1000, exp),
                "KMGTPE".charAt(exp-1));
    }
}
