package com.programacion.robertomtz.cdmx_go.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.programacion.robertomtz.cdmx_go.Classes.Negocio;
import com.programacion.robertomtz.cdmx_go.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    private double latitud;
    private double longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            Negocio negocio = (Negocio) bundle.get("negocio");
            latitud = Double.parseDouble(negocio.getLugar().trim().split(",")[0].trim());
            longitud = Double.parseDouble(negocio.getLugar().trim().split(",")[1].trim());
            LatLng latlng = new LatLng(latitud, longitud);
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latlng, 16);
            if (marcador != null)
                marcador.remove();
            marcador = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title(negocio.getNombre())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_market)));
            mMap.animateCamera(miUbicacion);

        }else
            miUbicacion();

    }

    private void agregaMarcador(double lat, double lon) {
        LatLng coordenadas = new LatLng(lat, lon);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null)
            marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title(getResources().getString(R.string.mi_ubicacion))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_market)));
        mMap.animateCamera(miUbicacion);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void miUbicacion() {
            int permissionCheck = ContextCompat.checkSelfPermission(MapsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                actualizarUbicacion(location);
                //actualizamos la ubicacion cad 15 segundos
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locationListener);

            }else{
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                        1000);
            }

        }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            agregaMarcador(latitud, longitud);
        }
    }


}
