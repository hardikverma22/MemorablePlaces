package com.example.android.memorableplaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class customListAdapter extends ArrayAdapter {


    public customListAdapter(Context context, ArrayList<String> places) {
        super(context,0, places);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_listview, parent, false);
        }

        TextView textView =(TextView)convertView.findViewById(R.id.listview_textview);
        String item = (String) getItem(position);
        textView.setText(item);

        MainActivity.listAdapter.notifyDataSetChanged();

        if(position==0 && MainActivity.places.get(0).toString().equals((String)getItem(0)))
        {
            Log.i("postion", String.valueOf(position));
            Log.i("MainActivity.places0", MainActivity.places.get(0).toString());
            Log.i("(String)getItem(0)", (String)getItem(0));
            Log.i("textView",textView.getText().toString());


            textView.setTextColor(Color.BLACK);


        }



        //Glide yay!!....New way
        String latitude="";
        String longitude="";
        latitude= String.valueOf(MainActivity.locations.get(position).latitude);
        longitude= String.valueOf(MainActivity.locations.get(position).longitude);
        String Url="https://maps.googleapis.com/maps/api/staticmap?center="+latitude+","+longitude+"&zoom=14&size=600x300";

        //Toast.makeText(convertView.getContext(),latitude.toString(),Toast.LENGTH_SHORT).show();
        //Toast.makeText(convertView.getContext(),longitude.toString(),Toast.LENGTH_SHORT).show();

        //Log.i("Latitude",latitude.toString());
        //Log.i("longitude",longitude.toString());
        ImageView imageView =(ImageView)convertView.findViewById(R.id.imageView);
        if(longitude.equals("0.0") && latitude.equals("0.0")){
            imageView.setImageResource(android.R.drawable.ic_input_add);
          //  Toast.makeText(convertView.getContext(),"5".toString(),Toast.LENGTH_SHORT).show();

        }else
        {
            Glide.with(convertView.getContext()).load(Url).into(imageView);
        }


        //downloading the imaged old way
        /*
         ImageView imageView =(ImageView)convertView.findViewById(R.id.imageView);
        ImageDownloader imgTask = new ImageDownloader();
        Bitmap bmp = null;
        String latitude="";
        String longitude="";
        if(position==0){
            imageView.setImageResource(R.drawable.backgroundpic);
        }else
        {
            latitude= String.valueOf(MainActivity.locations.get(position).latitude);
            longitude= String.valueOf(MainActivity.locations.get(position).longitude);
            String Url="https://maps.googleapis.com/maps/api/staticmap?center="+latitude+","+longitude+"&zoom=14&size=600x300";
            //String Url="https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=14&size=600x300";
            try {
                bmp=imgTask.execute(Url).get() ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            imageView.setImageBitmap(bmp);

        }

        */

        return convertView;
    }

}
