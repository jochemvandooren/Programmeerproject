package mprog.nl.findmystuff;

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
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ImageView img;
    private GoogleMap mMap;
    String selectedobject;
    LatLng selectedobjectloc;
    LatLng lastloc;
    LatLng myloc;
    List<Marker> markerlist;
    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;
    //captured picture uri
    private Uri picUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button updateButton = (Button) findViewById(R.id.update);
        Button takePic = (Button) findViewById(R.id.takePic);

        //retrieve selected object from mainactivity
        Intent iin = getIntent();
        Bundle b = iin.getExtras();


        if (b != null) {
            selectedobject = (String) b.get("object");
            Log.d("mapsactivity", selectedobject);
            //display selected object
            TextView tv = (TextView) this.findViewById(R.id.textView2);
            tv.setText("Selected: " + selectedobject);
        } else{
            TextView tv = (TextView) this.findViewById(R.id.textView2);
            tv.setText("No object selected");
            //remove update location button if no object is selected
            updateButton.setVisibility(View.GONE);
            takePic.setVisibility(View.GONE);
        }
    }

    public void takePic(View view){
        try {
            //use standard intent to capture an image
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if(requestCode == CAMERA_CAPTURE){
                //get the bitmap for the captured image
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 80, bos);// can use something 70 in case u want to compress the image
                byte[] scaledData = bos.toByteArray();

                //save the image to Parse
                final ParseFile file = new ParseFile("testing",scaledData);
                file.saveInBackground();
                String encodedString = Base64.encodeToString(scaledData, Base64.DEFAULT);

                //Put image in correct row
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ObjectList");
                query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                query.whereEqualTo("object", selectedobject);

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> results, ParseException e) {
                        if (e == null) {
                            for (ParseObject result : results) {
                                //add all the objects to the list
                                result.put("img", file);
                                result.saveInBackground();
                            }
                        } else {
                            Log.d("mapsactivity", "query lukt niet");
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
        markerlist = new ArrayList<Marker>();
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
                        Marker marker = mMap.addMarker(new MarkerOptions().position(markerloc).title(result.getString("object")));
                        markerlist.add(marker);
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
                Log.d("mapsactivity", "query lukt niet");
                Log.d("mapsactivity", e.toString());
            }

        }
    });}


    public void updateLoc(final View view) {
        //search for row with currentuser and selectedobject
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ObjectList");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("object", selectedobject);
        Log.d("LCOATIE2", lastloc.toString());

        final ParseGeoPoint geoPoint = new ParseGeoPoint(lastloc.latitude , lastloc.longitude );

        //update location of the object where user == currentuser and object == selectedobject
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    for (ParseObject result : results) {
                        //update location of object in Parse
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
                        //mMap.addMarker(new MarkerOptions().position(lastloc).title(selectedobject));
                        //STILL HAVE TO DELETE THE OTHER MARKER



                    }
                } else {
                    Log.d("mapsactivity.updateloc", "query lukt niet");
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

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.infobox_style, null);
                TextView title = (TextView) v.findViewById(R.id.title);
                final ImageView imgView = (ImageView) v.findViewById(R.id.img);
                final ImageView backView = (ImageView) v.findViewById(R.id.back);
                Log.d("testxxx", "There was a problem downloading the data.");


                ParseQuery<ParseObject> query = ParseQuery.getQuery("ObjectList");
                query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                query.whereEqualTo("object", marker.getTitle());

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> results, ParseException e) {

                        if (e == null) {
                            for (ParseObject result : results) {
                                ParseFile image = (ParseFile) result.get("img");
                                image.getDataInBackground(new GetDataCallback() {
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {

                                            // Decode the Byte[] into bitmap
                                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            Log.d("testx", "There was a problem downloading the data." + bmp.toString());
                                            // Set the Bitmap into the imageView
                                            imgView.setImageResource(R.drawable.arrow_back);
                                        } else {
                                            Log.d("test", "There was a problem downloading the data.");
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("mapsactivity.updateloc", "query lukt niet");
                        }

                    }
                });


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
        Location myLocation = locationManager.getLastKnownLocation(provider);


        // Get latitude of the current location
        double latitude = myLocation.getLatitude();
        // Get longitude of the current location
        double longitude = myLocation.getLongitude();
        // Create a LatLng object for the current location
        myloc = new LatLng(latitude, longitude);

        //set locationlistener
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

    //enable tracking location changes
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            lastloc = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("LCOATIE", lastloc.toString());

            //if(mMap != null){
            //    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastloc, 16.0f));
            //}
        }
    };
}

