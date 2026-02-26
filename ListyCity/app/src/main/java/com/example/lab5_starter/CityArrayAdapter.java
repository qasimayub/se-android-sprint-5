package com.example.lab5_starter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CityArrayAdapter extends ArrayAdapter<City> {
    private ArrayList<City> cities;
    private Context context;
    private OnCityActionListener actionListener;

    // Interface to communicate button clicks to MainActivity
    public interface OnCityActionListener {
        void onEditCity(City city);
        void onDeleteCity(City city);
    }

    public CityArrayAdapter(Context context, ArrayList<City> cities, OnCityActionListener listener) {
        super(context, 0, cities);
        this.cities = cities;
        this.context = context;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_city, parent, false);
        }

        City city = cities.get(position);
        TextView cityName = view.findViewById(R.id.textCityName);
        TextView cityProvince = view.findViewById(R.id.textCityProvince);
        Button editBtn = view.findViewById(R.id.buttonEditRow);
        Button deleteBtn = view.findViewById(R.id.buttonDeleteRow);

        cityName.setText(city.getName());
        cityProvince.setText(city.getProvince());

        // Set listeners for the individual row buttons
        editBtn.setOnClickListener(v -> actionListener.onEditCity(city));
        deleteBtn.setOnClickListener(v -> actionListener.onDeleteCity(city));

        return view;
    }
}