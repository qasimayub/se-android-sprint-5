package com.example.lab5_starter;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener, CityArrayAdapter.OnCityActionListener {
    private Button addCityButton;
    private ListView cityListView;

    private ArrayList<City> cityArrayList;
    private CityArrayAdapter cityArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference citiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        addCityButton = findViewById(R.id.buttonAddCity);
        cityListView = findViewById(R.id.listviewCities);

        cityArrayList = new ArrayList<>();
        cityArrayAdapter = new CityArrayAdapter(this, cityArrayList, this);
        cityListView.setAdapter(cityArrayAdapter);

        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (value != null) {
                cityArrayList.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    cityArrayList.add(snapshot.toObject(City.class));
                }
                cityArrayAdapter.notifyDataSetChanged();
            }
        });

        addCityButton.setOnClickListener(view -> {
            new CityDialogFragment().show(getSupportFragmentManager(), "Add City");
        });
    }
    @Override
    public void onEditCity(City city) {
        CityDialogFragment cityDialogFragment = CityDialogFragment.newInstance(city);
        cityDialogFragment.show(getSupportFragmentManager(), "City Details");
    }

    @Override
    public void onDeleteCity(City city) {
        citiesRef.document(city.getName())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Deleted " + city.getName(), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Firestore", "Delete failed", e));
    }
    @Override
    public void addCity(City city) {
        citiesRef.document(city.getName()).set(city);
    }
    @Override
    public void updateCity(City city, String newName, String newProvince) {
        String oldName = city.getName();
        if (!oldName.equals(newName)) {
            citiesRef.document(oldName).delete();
            city.setName(newName);
            city.setProvince(newProvince);
            citiesRef.document(newName).set(city);
        } else {
            city.setProvince(newProvince);
            citiesRef.document(oldName).update("province", newProvince);
        }
    }
}