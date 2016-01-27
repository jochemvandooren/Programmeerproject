package mprog.nl.findmystuff;

//Jochem van Dooren
//jochemvandooren@hotmail.nl
//10572929

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    Bitmap bmp;
    private GoogleMap mMap;
    String selectedobject;
    LatLng selectedobjectloc;
    LatLng lastloc;
    LatLng myloc;
    List<Marker> markerlist;
    final int CAMERA_CAPTURE = 1;
    HashMap<String, Bitmap> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button updateButton = (Button) findViewById(R.id.update);
        ImageButton takePic = (ImageButton) findViewById(R.id.takePic);

        //retrieve selected object from mainactivity
        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {
            //display selected object
            selectedobject = (String) b.get("object");
            TextView tv = (TextView) this.findViewById(R.id.textView2);
            tv.setText("Selected: " + selectedobject);
        } else{
            //if there is no selected object hide buttons
            TextView tv = (TextView) this.findViewById(R.id.textView2);
            tv.setText("No object selected");
            updateButton.setVisibility(View.GONE);
            takePic.setVisibility(View.GONE);
        }

        //retrieve images from Parse
        updateImages();
    }

    public void updateImages(){
        //create hashmap to put the images in
        images = new HashMap<String, Bitmap>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ObjectList");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    for (ParseObject result : results) {
                        ParseFile image = (ParseFile) result.get("img");
                        if (image != null) {
                            //convert bytearray to bitmap
                            try {
                                byte[] data = image.getData();
                                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                images.put((String) result.get("object"), bmp);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "There is a problem loading your images...",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void takePic(View view){
        try {
            //start camera intent
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            Toast.makeText(getApplicationContext(), "Your device has no camera!",
                    Toast.LENGTH_LONG).show();
        }
    }

    //retrieving the picture from the camera
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode == CAMERA_CAPTURE){
                //get the bitmap for the captured image and convert to bytearray
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);// can use something 70 in case u want to compress the image
                byte[] scaledData = bos.toByteArray();

                //save the image to Parse
                final ParseFile file = new ParseFile("testing",scaledData);
                file.saveInBackground();

                //Put image in correct row
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ObjectList");
                query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                query.whereEqualTo("object", selectedobject);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> results, ParseException e) {
                        if (e == null) {
                            for (ParseObject result : results) {
                                result.put("img", file);
                                result.saveInBackground();
                                updateImages();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "There is a problem saving your file to Parse...",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    //go to mainactivity
    public void gotoHome(View view) {
        Intent intent2 = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(intent2);
    }

    public void updateMarkers(){
        //retrieve locations from objects
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ObjectList");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
        public void done(List<ParseObject> results, ParseException e) {
            if (e == null) {
                for (ParseObject result : results) {
                    if (result.getParseGeoPoint("loc") != null) {
                        //convert geopoint to location and place marker
                        LatLng markerloc = new LatLng(result.getParseGeoPoint("loc").getLatitude(), result.getParseGeoPoint("loc").getLongitude());
                        mMap.addMarker(new MarkerOptions().position(markerloc).title(result.getString("object")));
                        result.saveInBackground();
                        //retrieve location of selectedobject and move to it
                        if (result.getString("object").equals(selectedobject)) {
                            selectedobjectloc = new LatLng(result.getParseGeoPoint("loc").getLatitude(), result.getParseGeoPoint("loc").getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(selectedobjectloc));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                        }
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "There is a problem loading your objects...",
                        Toast.LENGTH_LONG).show();
            }

        }
    });}

    public void updateLoc(final View view) {
        //convert location to geopoint
        final ParseGeoPoint geoPoint = new ParseGeoPoint(lastloc.latitude , lastloc.longitude );

        //update location in Parse for selected object
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ObjectList");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("object", selectedobject);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    for (ParseObject result : results) {
                        result.put("loc", geoPoint);
                        result.saveInBackground();

                        //remove markers and update markers.
                        mMap.clear();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateMarkers();
                            }
                        }, 2000);
                        updateMarkers();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "There is a problem updating your location to Parse...",
                            Toast.LENGTH_LONG).show();
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

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            //function gets called when user clicks on a marker
            @Override
            public View getInfoContents(final Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.infobox_style, null);
                final TextView title = (TextView) v.findViewById(R.id.title);
                final ImageView imgView = (ImageView) v.findViewById(R.id.img);

                //set content for infowindow
                Bitmap bmp = images.get(marker.getTitle());
                imgView.setImageBitmap(bmp);
                title.setText(marker.getTitle());

                return v;
            }
        });

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

        //get location on opening the map
        Location myLocation = locationManager.getLastKnownLocation(provider);
        // Get latitude of the current location
        double latitude = myLocation.getLatitude();
        // Get longitude of the current location
        double longitude = myLocation.getLongitude();
        // Create a LatLng object for the current location
        myloc = new LatLng(latitude, longitude);

        //set locationlistener to keep track of location
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);

        updateMarkers();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // Set up map camera based on selected object
                if (selectedobjectloc == null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myloc));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(selectedobjectloc));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                }
            }
        }, 2000);
    }

    //keep track of location
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            lastloc = new LatLng(location.getLatitude(), location.getLongitude());
        }
    };
}

