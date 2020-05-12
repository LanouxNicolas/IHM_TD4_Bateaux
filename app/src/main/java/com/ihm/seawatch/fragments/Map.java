package com.ihm.seawatch.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ihm.seawatch.R;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class Map extends Fragment {

    private static final String PREFS_NAME = "org.andnav.osm.prefs";
    private static final String PREFS_TILE_SOURCE = "tilesource";
    private static final String PREFS_LATITUDE_STRING = "latitudeString";
    private static final String PREFS_LONGITUDE_STRING = "longitudeString";
    private static final String PREFS_ORIENTATION = "orientation";
    private static final String PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble";

    private static final int MENU_ABOUT = Menu.FIRST + 1;

    private SharedPreferences mPrefs;
    private MapView mMapView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = rootView.findViewById(R.id.map);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMapView.setTileSource(TileSourceFactory.MAPNIK); // Render

        final Context context = this.getActivity();
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // My Location
        // Note you have handle the permissions yourself, the overlay did not do it for you
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(mLocationOverlay);

        // Needed for pinch zooms
        mMapView.setMultiTouchControls(true);

        // The rest of this is restoring the last map location the user looked at
        final String latitudeString = mPrefs.getString(PREFS_LATITUDE_STRING, "1.0");
        final String longitudeString = mPrefs.getString(PREFS_LONGITUDE_STRING, "1.0");
        final double latitude = Double.parseDouble(latitudeString);
        final double longitude = Double.parseDouble(longitudeString);
        mMapView.setExpectedCenter(new GeoPoint(latitude, longitude));
        Log.d("Location", latitudeString + " " + longitudeString);
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
        // Save the current location
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
        edit.putFloat(PREFS_ORIENTATION, mMapView.getMapOrientation());
        edit.putString(PREFS_LATITUDE_STRING, String.valueOf(mMapView.getMapCenter().getLatitude())); // TODO: Get current latitude
        edit.putString(PREFS_LONGITUDE_STRING, String.valueOf(mMapView.getMapCenter().getLongitude())); // TODO: Get current longitude
        edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, (float) mMapView.getZoomLevelDouble());
        edit.apply();

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
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE, TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
            mMapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException e) {
            mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }
        mMapView.onResume();
    }
}
