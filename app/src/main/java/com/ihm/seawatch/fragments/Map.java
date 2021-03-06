package com.ihm.seawatch.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.ihm.seawatch.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class Map extends Fragment {

    private ArrayList<OverlayItem> items = new ArrayList<>();

    private MapView mMapView;
    private LocationManager mLocationManager;

    static final String[] LOCATION_PERMS = { Manifest.permission.ACCESS_FINE_LOCATION };
    static final int LOCATION_REQUEST = 1340;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMapView = this.requireView().findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.MAPNIK); // Render
        mLocationManager = (LocationManager) this.requireContext().getSystemService(Context.LOCATION_SERVICE);

        Context context = this.requireContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        SQLiteDatabase sqLiteDatabase = requireContext().openOrCreateDatabase("geopoints.db", Context.MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Incidents",null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                double latitude = cursor.getDouble(0);
                double longitude = cursor.getDouble(1);
                String details = cursor.getString(2);
                String date = cursor.getString(3);
                String temperature = cursor.getString(4);
                String courant = cursor.getString(5);
                String vent = cursor.getString(6);
                String precipitations = cursor.getString(9);
                String allInfos = details + "///" + date + "///" + temperature + "///" + courant + "///" + vent + "///" + precipitations;
                items.add(new OverlayItem("Incident", allInfos, new GeoPoint(latitude, longitude)));
            }
        }
        sqLiteDatabase.close();

        // My Location
        GpsMyLocationProvider provider = new GpsMyLocationProvider(context);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        provider.setLocationUpdateMinDistance(100); // [m]  // Set the minimum distance for location updates
        provider.setLocationUpdateMinTime(10000);   // [ms] // Set the minimum time interval for location updates

        if (ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);;
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(provider, mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(mLocationOverlay);

        // Needed for pinch zooms
        mMapView.setMultiTouchControls(true);

        IMapController mapController = mMapView.getController();
        mapController.setZoom(14.0);
        mapController.setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));

        //items.add(new OverlayItem("Incident", "Ceci est un message de test pour un incident", new GeoPoint(location.getLatitude() + 0.02, location.getLongitude() + 0.01)));
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<>(this.requireContext(), items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                String details = item.getSnippet();
                Bundle bundle = new Bundle();
                bundle.putString("AllInformations", details);
                NavHostFragment.findNavController(Map.this)
                        .navigate(R.id.action_SecondFragment_to_FourthFragment, bundle);
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        });
        mOverlay.setFocusItemsOnTap(true);
        mMapView.getOverlays().add(mOverlay);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Open the mark button on the map
        // The button visibility is set to gone by me and changed in the fragment_map_details.xml layout
        view.findViewById(R.id.mark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(Map.this)
                        .navigate(R.id.action_SecondFragment_to_FourthFragment);
            }
        });

        // Return to home
        view.findViewById(R.id.button_mapToHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(Map.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mMapView.setTileSource(TileSourceFactory.MAPNIK); // Render
        mMapView.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            NavHostFragment.findNavController(Map.this)
                    .navigate(R.id.reload);
        }
    }
}
