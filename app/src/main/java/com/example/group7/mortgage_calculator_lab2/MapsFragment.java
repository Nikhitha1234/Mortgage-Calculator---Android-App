package com.example.group7.mortgage_calculator_lab2;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment fragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mapsView = inflater.inflate(R.layout.activity_maps, container, false);
        fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //fragment.getMapAsync(this);
        return mapsView;
    }

    @Override
    public void onStart() {
        fragment.getMapAsync(this);
        super.onStart();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        SaveDataHelper mDbHelper = new SaveDataHelper(getActivity().getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ArrayList<String> cities = new ArrayList<String>();
        Cursor cursorProperties = db.rawQuery("SELECT * FROM "+ SaveDataContract.SaveDataEntry.TABLE_NAME, null);

        //if the cursor has some data
        if (cursorProperties.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
                cities.add(
                        cursorProperties.getString(cursorProperties.getColumnIndex(SaveDataContract.SaveDataEntry.COLUMN_NAME_CITY))
                );
            } while (cursorProperties.moveToNext());
        }
        //closing the cursor
        cursorProperties.close();

        Log.v("1",cities.toString());

        // Add a marker in Sydney and move the camera

        LatLng sydney = getLocationFromAddress(this.getContext(),cities.get(1));
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        mMap.addMarker(new MarkerOptions().position(sydney).title(cities.get(1)).snippet("Property Details layout"));
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Sydney").snippet("Property Details layout"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

      /*  mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsFragment.this.getContext());
                //searching marker id in locationDetailses and getting all the information of a particular marker
               // for (int i = 0; i<locationDetailses.size(); i++) {
                    //matching id so, alert dialog can show specific data
               //     if (marker.getId().equals(locationDetailses.get(i).getMarkerID())){
                        builder.setTitle("City: ");
                        builder.setMessage("Wind Speed: ");
               //     }
             //   }

                builder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

                // Add the button

                return false;
            }
        });*/
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            System.out.println(address);


            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }








}
