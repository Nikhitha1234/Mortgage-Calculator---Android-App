package com.example.group7.mortgage_calculator_lab2;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    SupportMapFragment fragment;
    SaveDataHelper mDbHelper;
    String curPropId;
    Marker curMarker;
    private GoogleMap mMap;

    public ViewPager mViewPager;
    Fragment calcFragment;
    MainActivity.SectionsPagerAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mapsView = inflater.inflate(R.layout.activity_maps, container, false);
        mViewPager = (ViewPager) container;
        pagerAdapter =  (MainActivity.SectionsPagerAdapter) mViewPager.getAdapter();
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
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(17.0f);

        mDbHelper = new SaveDataHelper(getActivity().getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //db.execSQL("delete from "+ SaveDataContract.SaveDataEntry.TABLE_NAME);

//        db.delete(SaveDataContract.SaveDataEntry.TABLE_NAME, SaveDataContract.SaveDataEntry.COLUMN_NAME_CITY + "=?", new String[]{""});

        ArrayList<String> propIds = new ArrayList();
        ArrayList<String> propAddresses = new ArrayList();
        Cursor cursorProperties = db.rawQuery("SELECT * FROM " + SaveDataContract.SaveDataEntry.TABLE_NAME, null);

        if (cursorProperties.moveToFirst()) {
            //looping through all the records
            do {
                propAddresses.add(
                        cursorProperties.getString(cursorProperties.getColumnIndex(SaveDataContract.SaveDataEntry.COLUMN_NAME_STREET))
                                + " "
                                + cursorProperties.getString(cursorProperties.getColumnIndex(SaveDataContract.SaveDataEntry.COLUMN_NAME_CITY))
                );
                propIds.add(
                        cursorProperties.getString(cursorProperties.getColumnIndex(SaveDataContract.SaveDataEntry._ID))
                );
            } while (cursorProperties.moveToNext());
        }
        cursorProperties.close();

       // Log.v("DB Ids", propIds.toString());

        for (int counter = 0; counter < propIds.size(); counter++) {
            LatLng propLocation = getLocationFromAddress(this.getContext(), propAddresses.get(counter));
            mMap.addMarker(new MarkerOptions().position(propLocation).title(propIds.get(counter))
                    //.snippet("Property Details layout")
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLng(propLocation));
        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                curMarker = marker;
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsFragment.this.getContext());
                LayoutInflater inflater = MapsFragment.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.maps_dialog, null);
                curPropId = marker.getTitle();
                SQLiteDatabase readableDb = mDbHelper.getWritableDatabase();
                Cursor cursorProperties = readableDb.
                        rawQuery(
                                "SELECT * FROM " + SaveDataContract.SaveDataEntry.TABLE_NAME + " WHERE _ID = ?", new String[]{curPropId});



                if (cursorProperties.moveToFirst()) {

                    builder.setTitle("Property Details");

                    TextView proptype = dialogView.findViewById(R.id.dialog_propTypeText);
                    proptype.setText(cursorProperties.getString(1));

                    TextView street = dialogView.findViewById(R.id.dialog_streetText);
                    street.setText(cursorProperties.getString(2));

                    TextView city = dialogView.findViewById(R.id.dialog_cityText);
                    city.setText(cursorProperties.getString(3));

                    TextView apr = dialogView.findViewById(R.id.dialog_aprText);
                    apr.setText(cursorProperties.getString(5) + "%");

                    TextView loanAmt = dialogView.findViewById(R.id.dialog_loanHdText);
                    loanAmt.setText(cursorProperties.getString(4));

                    TextView monthlypmt = dialogView.findViewById(R.id.dialog_monthlypmtText);
                    monthlypmt.setText(cursorProperties.getString(6));

                    pagerAdapter.setPropertyId(cursorProperties.getInt(0));

                }

                builder.setView(dialogView);

                builder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Redirect to calc view and fetch data
                        pagerAdapter.notifyDataSetChanged();
                        mViewPager.setCurrentItem(0);

                    }
                });

                builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();
                        db.delete(SaveDataContract.SaveDataEntry.TABLE_NAME, SaveDataContract.SaveDataEntry._ID + "=?", new String[]{curPropId});
                        curMarker.remove();
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

                // Add the button

                return true;
            }
        });
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null && address.isEmpty()) {
                return null;
            }
            System.out.println(address);


            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


}
