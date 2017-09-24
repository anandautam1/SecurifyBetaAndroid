package org.farmate.securifybeta.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLngBounds.Builder;

import org.farmate.securifybeta.R;
import org.farmate.securifybeta.activity.LoginActivity;
import org.farmate.securifybeta.activity.StartActivity;
import org.farmate.securifybeta.database.LatLngBean;
import org.farmate.securifybeta.database.securifyUserDatabaseHelper;
import org.farmate.securifybeta.database.usersLocal;
import org.farmate.securifybeta.other.Album;
import org.farmate.securifybeta.other.AlbumsAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.farmate.securifybeta.activity.LoginActivity.generalHTTPQuest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, com.google.android.gms.location.LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // FIX INTENT
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View inflatedView;

    // map related objects

    private GoogleMap mMap;
    private SupportMapFragment mapFrag;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;

    private SupportMapFragment supportMapFragment;

    // state machine job running or no job is running
    private Boolean stateOnJobChange = false;

    private Context global_context_database;
    private SharedPreferences sharedPref;

    HashMap<Marker,LatLngBean> hashMapMarker = new HashMap<Marker,LatLngBean>();

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2 ) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        //if (mGoogleApiClient != null) {
          //  LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        //}
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    private void initializeMap() {
        // old code using fragment inside fragment does not work once switch from one frag to another
        if(getActivity()!= null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map_container);
            // if it has not been created before thus execute to make a fresh batch of fragment
            supportMapFragment = supportMapFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
            supportMapFragment.getMapAsync(this);
        }
    }

    private Button RequestButton;

    // for the dialog
    private final String ARG_PARAM_NAME = "Name_key";
    private final String ARG_ETA = "ETA_key";
    private final String ARG_PHONE = "Phone_Key";
    private final String ARG_LATI1 = "LATI_1_key";
    private final String ARG_LONG1 = "LONG_1_key";
    private final String ARG_LATI2 = "LATI_2_key";
    private final String ARG_LONG2 = "LONG_2_key";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_home, container, false);

        RequestButton = (Button) inflatedView.findViewById(R.id.requestButton);
        RequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check out all the markers to view all services
                SetZoomlevel(listLatLng);
                // assume latest database
                // send the latest location to the database
                // update to the latest database

                // perform a check on all the available lat long to look for the nearest person to have the id

                // get the nearest person based on the ID

                // get the ETA based on the google maps api

                Bundle args = new Bundle();
                args.putString(ARG_PARAM_NAME, "Djoko Prinatono");
                args.putString(ARG_ETA, "5 Minutes");
                args.putString(ARG_PHONE, "0409890566");
                args.putString(ARG_LATI1,"-37.809682");
                args.putString(ARG_LONG1,"144.971095");
                args.putString(ARG_LATI2,"-37.870940");
                args.putString(ARG_LONG2,"144.707988");

                // open up dialog by passing all the JSON
                FragmentManager fragmentManager = getChildFragmentManager();
                dialogRequestFragment requestPopUp = new dialogRequestFragment();
                requestPopUp.setArguments(args);
                requestPopUp.show(fragmentManager,"sam");

                // initialize the dialog to draw the path from the current position to the destination

                // confirm change the state of the google maps fragment

                // decline goes back to change to the initial fragment state
            }
        });

        return inflatedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        // by default the map has to be reinstated all the time to ensure up to date location
        initializeMap();
    }

    public void  SetZoomlevel(ArrayList<LatLng> listLatLng)
    {
        final GoogleMap googleMap = mMap;
        if (listLatLng != null && listLatLng.size() == 1)
        {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 10));
        }
        else if (listLatLng != null && listLatLng.size() > 1)
        {
            final Builder builder = LatLngBounds.builder();
            for (int i = 0; i < listLatLng.size(); i++)
            {
                builder.include(listLatLng.get(i));
            }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map_container);
                // set camera to view all the marker within the map
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), inflatedView.findViewById(R.id.map_container)
                                .getWidth(), inflatedView.findViewById(R.id.map_container).getHeight(), 80));
            }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // check for permission again just to be sure
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // load custom style
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.map_style_json));

            if (!success) {
                //Log.e(TAG, "Style parsing failed.");
                Toast.makeText(getActivity(), "Style parsing failed.", Toast.LENGTH_LONG).show();
            }
        } catch (Resources.NotFoundException e) {
            //Log.e(TAG, "Can't find style. Error: ", e);
            Toast.makeText(getActivity(), "Can't find style. Error: ", Toast.LENGTH_LONG).show();
        }
        // set all the marker from the database
            setData();
    }



    private ArrayList<LatLng>listLatLng;

    void LoadingLocations(ArrayList<LatLngBean> arrayList)
    {
            if(arrayList.size()>0)
            {
                try
                {
                    listLatLng=new ArrayList<LatLng>();
                    for (int i = 0; i < arrayList.size(); i++)
                    {
                        LatLngBean bean=arrayList.get(i);
                        if(bean.getLatitude().length()>0 && bean.getLongitude().length()>0)
                        {
                            double lat=Double.parseDouble(bean.getLatitude());
                            double lon=Double.parseDouble(bean.getLongitude());

                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat,lon))
                                    .title(bean.getTitle())
                                    .snippet(bean.getSnippet())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            //Add Marker to Hashmap
                            hashMapMarker.put(marker,bean);

                            //Set Zoom Level of Map pin
                            LatLng object=new LatLng(lat, lon);
                            listLatLng.add(object);
                        }
                    }
                    //SetZoomlevel(listLatLng);
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker position)
                    {
                        LatLngBean bean=hashMapMarker.get(position);
                        Toast.makeText(getActivity(), bean.getTitle(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                Toast.makeText(getActivity(),"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
    }

    private void setData() {
        ArrayList<LatLngBean> arrayList = new ArrayList<LatLngBean>();
        // create an instance of the database helper
        if(getActivity() != null) {
            securifyUserDatabaseHelper db = new securifyUserDatabaseHelper(getActivity());
            // create an instance of userslist which has all the user database inside it
            List<usersLocal> userList = db.getAllUser();
            // get all location of the technicians which are online within the area THAT IS NOT USER 1
            if (getActivity() != null) {
                sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                int userID = Integer.valueOf(sharedPref.getString(getString(R.string.userIdLoggedIn), ""));
                // iterate through all the user list
                for (int i = 0; i < userList.size(); i++) {
                    // instatitate a local user object fro the list
                    usersLocal user = userList.get(i);
                    if (user.getUserID() != userID) {
                        LatLngBean bean = new LatLngBean();
                        String insta_1_long = String.valueOf(user.getGps_long());
                        String insta_2_lati = String.valueOf(user.getGps_lati());
                        if (insta_1_long.length() > 0 && insta_2_lati.length() > 0) {
                            bean.setTitle(user.getFname() + " " + user.getLname());
                            bean.setSnippet(user.getPhone());
                            bean.setLongitude(insta_1_long);
                            bean.setLatitude(insta_2_lati);
                        }
                        arrayList.add(bean);
                    }
                }
                // advance feature is to change the amount of
                LoadingLocations(arrayList);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        if(getActivity() != null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    // fucntion to check on location permission
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private String currentLati;
    private String currentLong;
    private String userID;
    private String isOnline;

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        // PERFORM HTTP REQUEST
        // get the numerical value of the current location
        Double currentLatiDoub = latLng.latitude;
        currentLati = currentLatiDoub.toString();
        Double currentLongDoub = latLng.longitude;
        currentLong = currentLongDoub.toString();
        // get from the database
        if(getActivity()!=null) {
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            if (sharedPref != null) {
                userID = sharedPref.getString(getString(R.string.userIdLoggedIn), "");
                isOnline = "1";
                SERVERUpdateCurrentLocation asyncTask = new SERVERUpdateCurrentLocation();
                asyncTask.execute(new String[]{getString(R.string.ServerURI)});
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(getActivity()!= null) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
            }
        }

        // when connected straight up goes to request to the latest location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();

            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    // Async syntax AsyncTask <TypeOfVarArgParams, ProgressValue, ResultValue>
    private class SERVERUpdateCurrentLocation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // we use the OkHttp library from https://github.com/square/okhttp
            final String uri_target = new String(getString(R.string.ServerURI));
            final String jobActivityEx = new String(getString(R.string.jobActivity_Ex));
            final String page_target_salt = new String("updateUserLocation.php?");
            // get password salt first generate 7 long random char

            // uri request builder
            Uri buildUrSalt = Uri.parse(uri_target + jobActivityEx + page_target_salt)
                    .buildUpon()
                    .appendQueryParameter("userID", userID)
                    .appendQueryParameter("currentLati", currentLati)
                    .appendQueryParameter("currentLong", currentLong)
                    .appendQueryParameter("isOnline", isOnline)
                    .build();
            String result = generalHTTPQuest(buildUrSalt.toString());
            int status_result = -1;
            try {
                JSONObject reader = new JSONObject(result);
                status_result = reader.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status_result == -1) {
                return "Cannot Connect To The Server";
            } else if (status_result == 0) {
                return "Invalid ID Register";
            } else if (status_result == 1) {
                return "Update Success";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Update Success")) {
                if (getActivity() != null) {
                    // declare the global context for the activity context
                    global_context_database = getActivity().getApplicationContext();
                    if (global_context_database != null) {
                        StoreJSonDataInToSQLiteClass asyncTask = new StoreJSonDataInToSQLiteClass(global_context_database);
                        if (asyncTask != null) {
                            asyncTask.execute(new String[]{getString(R.string.ServerURI)});
                        }
                    }
                }
            }
        }
    }

    private class StoreJSonDataInToSQLiteClass extends AsyncTask<String, Void, String> {

        public Context context;

        private securifyUserDatabaseHelper databaseHelper;

        final String COLUMN_USERID = "userID";
        final String COLUMN_FNAME = "fname";
        final String COLUMN_LNAME = "lname";
        final String COLUMN_EMAIL = "email";
        final String COLUMN_PHONE = "phone";
        final String COLUMN_PASS_HASHED = "pass_hashed";
        final String COLUMN_PASS_SALT = "pass_salt";
        final String COLUMN_ROLE = "role";
        final String COLUMN_GPS_LONG = "gps_long";
        final String COLUMN_GPS_LATI = "gps_lati";
        final String COLUMN_ISONLINE = "isOnline";
        final String COLUMN_LASTUPDATED = "lastUpdated";

        public StoreJSonDataInToSQLiteClass(Context context) {

            this.context = context;
            databaseHelper = new securifyUserDatabaseHelper(context);
        }

        @Override
        protected String doInBackground(String... urls) {

            // we use the OkHttp library from https://github.com/square/okhttp
            final String uri_target= new String(getString(R.string.ServerURI));
            final String page_target_salt = new String("getAllUser.php?");
            // get password salt first
            // uri request builder
            Uri buildUrSalt = Uri.parse(uri_target + page_target_salt);
            String result = generalHTTPQuest(buildUrSalt.toString());
            int status_result = -1;

            try {
                JSONArray jsonArray = null;
                jsonArray = new JSONArray(result);
                JSONObject jsonObject;
                for (int i = 0; i < jsonArray.length(); i++) {

                    usersLocal userLocal = new usersLocal();
                    jsonObject = jsonArray.getJSONObject(i);

                    // debug only
                    //String temp = new String(jsonObject.getString(COLUMN_USERID));
                    //int tempInt = Integer.parseInt(temp);

                    userLocal.setUserID(Integer.parseInt(jsonObject.getString(COLUMN_USERID)));
                    userLocal.setFname(jsonObject.getString(COLUMN_FNAME));
                    userLocal.setLname(jsonObject.getString(COLUMN_LNAME));
                    userLocal.setEmail(jsonObject.getString(COLUMN_EMAIL));
                    userLocal.setPhone(jsonObject.getString(COLUMN_PHONE));
                    userLocal.setPass_hashed(jsonObject.getString(COLUMN_PASS_HASHED));
                    userLocal.setPass_salt(jsonObject.getString(COLUMN_PASS_SALT));
                    userLocal.setRole(jsonObject.getString(COLUMN_ROLE));
                    userLocal.setGps_long(Double.parseDouble(jsonObject.getString(COLUMN_GPS_LONG)));
                    userLocal.setGps_lati(Double.parseDouble(jsonObject.getString(COLUMN_GPS_LATI)));
                    userLocal.setIsOnline(Integer.parseInt(jsonObject.getString(COLUMN_ISONLINE)));
                    userLocal.setLastUpdated(jsonObject.getString(COLUMN_LASTUPDATED));
                    // make sure its addUser in this field
                    databaseHelper.addUser(userLocal);
                }
                status_result = 1;
                databaseHelper.closeDB();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status_result == -1) {
                return "Cannot Connect To The Server";
            }
            else if(status_result == 0) {
                return "Query Error";
            }
            else {
                return "Database Synchronized";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            // update preference based on the passed email address
            // inside fragment thus
            if (result.equals("Database Synchronized")) {
                setData();
            }
        }
    }
    // https://rosettacode.org/wiki/Haversine_formula#Java
    private static Double CalculateDistance(String Lat1, String Lon1, String Lat2, String Lon2) {
        final double EarthRadius = 6372.8; // Earth Radius In kilometers
        Double lat1 = Double.parseDouble(Lat1);
        Double lon1 = Double.parseDouble(Lon1);
        Double lat2 = Double.parseDouble(Lat2);
        Double lon2 = Double.parseDouble(Lon2);
        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distance = EarthRadius * c;
        return distance;
    }
}
