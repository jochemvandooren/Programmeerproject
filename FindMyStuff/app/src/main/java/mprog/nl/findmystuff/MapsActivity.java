package mprog.nl.findmystuff;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String selectedobject;
    LatLng lastloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //retrieve selected object from mainactivity
        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {
            selectedobject = (String) b.get("object");
            Log.d("mapsactivity", selectedobject);
        }
    }

    //go to mainactivity
    public void gotoHome(View view) {
        Intent intent2 = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(intent2);
    }


    public void updateLoc(View view) {
        //search for row with currentuser and selectedobject
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ObjectList");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("object", selectedobject);
        Log.d("LCOATIE2", lastloc.toString());



        final ParseGeoPoint geoPoint = new ParseGeoPoint(lastloc.latitude , lastloc.longitude );


        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    for (ParseObject result : results) {
                        //update location of object
                        result.put("loc", geoPoint);
                        result.saveInBackground();


                    }
                } else {
                    Log.d("mapsactivity", "query lukt niet");
                }

            }
        });


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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(provider);


        // Get latitude of the current location
        double latitude = myLocation.getLatitude();

        // Get longitude of the current location
        double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        LatLng myloc = new LatLng(latitude, longitude);
        Log.d("locatie1 =", myloc.toString());

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myloc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

        //set locationlistener
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);


        //set markers for objects
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ObjectList");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    for (ParseObject result : results) {
                        //retrieve locations of objects
                        if (result.getParseGeoPoint("loc") != null) {
                            Log.d("Geopoints", result.getParseGeoPoint("loc").toString());
                            //convert geopoint to location and place marker
                            LatLng markerloc = new LatLng(result.getParseGeoPoint("loc").getLatitude(), result.getParseGeoPoint("loc").getLongitude());
                            mMap.addMarker(new MarkerOptions().position(markerloc).title(result.getString("object")));
                            result.saveInBackground();
                        }


                    }
                } else {
                    Log.d("mapsactivity", "query lukt niet");
                }

            }
        });



    }

    //enable tracking location changes
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            lastloc = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("LCOATIE", lastloc.toString());

            if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastloc, 16.0f));
            }
        }
    };
}

