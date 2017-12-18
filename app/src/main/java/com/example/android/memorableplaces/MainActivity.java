package com.example.android.memorableplaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> places = new ArrayList<String>();
    static ArrayList<LatLng> locations = new ArrayList<>();
    static customListAdapter listAdapter;
    static ListView listView;
    SharedPreferences sharedPreferences;

    ArrayList<String> longitude;
    ArrayList<String> latitude;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.reset){

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();

            places.clear();
            locations.clear();
            latitude.clear();
            longitude.clear();

            places.add("Add new Place...");
            locations.add(new LatLng(0,0));

            listAdapter.notifyDataSetChanged();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView =(ListView)findViewById(R.id.listview);

        latitude = new ArrayList<>();
        longitude= new ArrayList<>();

        places.clear();
        locations.clear();
        latitude.clear();
        longitude.clear();

        sharedPreferences = this.getSharedPreferences("com.example.android.memorableplaces",MODE_PRIVATE);
        try {
            places=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitude=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitude",ObjectSerializer.serialize(new ArrayList<String>())));
            longitude=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitude",ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (IOException e) {
            e.printStackTrace();
        }


        if(places.size()>0 && latitude.size()>0 && longitude.size()>0 && places.size() == latitude.size()){
            for(int i=0;i<latitude.size();i++){
                locations.add(new LatLng(Double.parseDouble(latitude.get(i)),Double.parseDouble(longitude.get(i))));

            }

        }else
        {
            places.add("Add new Place...");
            locations.add(new LatLng(0,0));


        }


        //listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,places);
        listAdapter = new customListAdapter(this,places);
        listView.setAdapter(listAdapter);





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);

                intent.putExtra("placeNumber",position);
                startActivity(intent);
            }
        });
    }
}
