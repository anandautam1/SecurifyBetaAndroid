package org.farmate.securifybeta.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.farmate.securifybeta.R;
import org.farmate.securifybeta.activity.LoginActivity;
import org.farmate.securifybeta.activity.MainActivity;
import org.farmate.securifybeta.activity.StartActivity;
import org.farmate.securifybeta.app.Config;
import org.farmate.securifybeta.database.LatLngBean;
import org.farmate.securifybeta.database.securifyUserDatabaseHelper;
import org.farmate.securifybeta.database.usersLocal;
import org.farmate.securifybeta.other.Album;
import org.farmate.securifybeta.other.AlbumsAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

    HashMap<Marker, LatLngBean> hashMapMarker = new HashMap<Marker, LatLngBean>();

    public HomeFragment() {
        // Required empty public constructor
    }

    // firebase integration to homefragment
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView photoReceived;

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        if (getActivity() != null) {
            SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
            String regId = pref.getString("regId", null);

            Log.e(TAG, "Firebase reg id: " + regId);

            if (!TextUtils.isEmpty(regId)) {
                // txtRegId.setText("Firebase Reg Id: " + regId);
                // that means registration of the device went fine at this point
                // update the database with the user ID to sync up with the database
                // TODO: Work on the way to update the firebase id on each time the app has been started
                if (getActivity() != null) {
                    // old revision 3/10/2017
                    // sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    // new preferences schema
                    sharedPref = getActivity().getSharedPreferences("<Pref Name>", MODE_PRIVATE);
                    String userID = sharedPref.getString(getString(R.string.userIdLoggedIn), "");
                    // userID then firebase ID // checked worked
                    SendFireBaseID asyncTask = new SendFireBaseID(userID, regId);
                    asyncTask.execute(new String[]{getString(R.string.ServerURI)});
                } else {
                    //txtRegId.setText("Firebase Reg Id is not received yet!");
                }
            }
        }
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // for the dialog
    private final String ARG_CLIENT_PARAM_NAME = "Client_Name_key";
    private final String ARG_TECHNI_PARAM_NAME = "Techni_Name_key";
    private final String ARG_ETA = "ETA_key";
    private final String ARG_DISTANCE = "Distance_key";
    private final String ARG_PHONE = "Phone_Key";
    private final String ARG_LATI1_CLIENT = "LATI_1_key";
    private final String ARG_LONG1_CLIENT = "LONG_1_key";
    private final String ARG_LATI2_TECHNI = "LATI_2_key";
    private final String ARG_LONG2_TECHNI = "LONG_2_key";
    private final String ARG_IS_CONFIRMED = "CONFIRMATION_STATUS";
    private final String ARG_CLIENT_FIREBASE_ID = "CLIENT_FIREBASE";
    private final String ARG_CLIENT_USER_ID = "CLIENT_USERID";
    private final String ARG_TECHNI_FIREBASE_ID = "TECHNI_FIREBASE";
    private final String ARG_TECHNI_USER_ID = "TECHNI_USERID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // when creating the home fragment automatically alerted with the notification
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    String Client_Name_key = intent.getStringExtra(ARG_CLIENT_PARAM_NAME);
                    String Techni_Name_key = intent.getStringExtra(ARG_TECHNI_PARAM_NAME);
                    String ETA_key = intent.getStringExtra(ARG_ETA);
                    String Distance_key = intent.getStringExtra(ARG_DISTANCE);
                    String Arg_phone = intent.getStringExtra(ARG_PHONE);
                    String Client_Firebase_ID = intent.getStringExtra(ARG_CLIENT_FIREBASE_ID);
                    String Client_User_ID = intent.getStringExtra(ARG_CLIENT_USER_ID);
                    String Arg_lati1 = intent.getStringExtra(ARG_LATI1_CLIENT);
                    String Arg_long1 = intent.getStringExtra(ARG_LONG1_CLIENT);
                    String Is_Confirmed = intent.getStringExtra(ARG_IS_CONFIRMED);

                    String Techni_Firebase_ID = intent.getStringExtra(ARG_TECHNI_FIREBASE_ID);
                    String Techni_User_ID = intent.getStringExtra(ARG_TECHNI_USER_ID);
                    String Arg_lati2 = intent.getStringExtra(ARG_LATI2_TECHNI);
                    String Arg_long2 = intent.getStringExtra(ARG_LONG2_TECHNI);

                    Bundle args = new Bundle();
                    // check if it has been confirmed properly then show the client firebase ID
                    args.putString(ARG_CLIENT_FIREBASE_ID, Client_Firebase_ID);
                    args.putString(ARG_CLIENT_USER_ID, Client_User_ID);
                    args.putString(ARG_TECHNI_FIREBASE_ID, Techni_Firebase_ID);
                    args.putString(ARG_TECHNI_USER_ID, Techni_User_ID);
                    args.putString(ARG_CLIENT_PARAM_NAME, Client_Name_key);
                    args.putString(ARG_TECHNI_PARAM_NAME, Techni_Name_key);
                    args.putString(ARG_ETA, ETA_key);
                    args.putString(ARG_DISTANCE, Distance_key);
                    args.putString(ARG_PHONE, Arg_phone);
                    args.putString(ARG_LATI1_CLIENT, Arg_lati1);
                    args.putString(ARG_LONG1_CLIENT, Arg_long1);
                    args.putString(ARG_LATI2_TECHNI, Arg_lati2);
                    args.putString(ARG_LONG2_TECHNI, Arg_long2);
                    args.putString(ARG_IS_CONFIRMED, Is_Confirmed);

                    if (Is_Confirmed.equals("0")) {

                        // open up dialog by passing all the JSON
                        FragmentManager fragmentManager = getChildFragmentManager();
                        dialogRequestFragment requestPopUp = new dialogRequestFragment();
                        requestPopUp.setArguments(args);
                        requestPopUp.show(fragmentManager, "sam1");

                    } else if (Is_Confirmed.equals("1")) {

                        // open up dialog by passing all the JSON
                        FragmentManager fragmentManager = getChildFragmentManager();
                        dialogRequestFragment requestPopUp = new dialogRequestFragment();
                        requestPopUp.setArguments(args);
                        requestPopUp.show(fragmentManager, "sam2");
                    }

                }
            }
        };
        displayFirebaseRegId();
    }

    @Override
    public void onResume() {

        // register GCM registration complete receiver
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Config.REGISTRATION_COMPLETE));

            // register new push message receiver
            // by doing this, the activity will be notified each time a new message arrives
            LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Config.PUSH_NOTIFICATION));
        }

        super.onResume();
    }

    @Override
    public void onPause() {

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
        }

        super.onPause();
        // would like to keep the registration broacast running
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private void initializeMap() {
        // old code using fragment inside fragment does not work once switch from one frag to another
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map_container);
            // if it has not been created before thus execute to make a fresh batch of fragment
            supportMapFragment = supportMapFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
            supportMapFragment.getMapAsync(this);
        }
    }
    private Button RequestButton;
    private TextView StatusETA;
    private TextView DistanceETA;
    // private TextView QualityStatus;

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) global_context_database.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflatedView = inflater.inflate(R.layout.fragment_home, container, false);
        RequestButton = (Button) inflatedView.findViewById(R.id.requestButton);
        StatusETA = (TextView) inflatedView.findViewById(R.id.statusETA);
        DistanceETA = (TextView) inflatedView.findViewById(R.id.distanceETA);

        RequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check out all the markers to view all services
                int TechUserID = 0;
                SetZoomlevel(listLatLng);
                // estimate who will be the closest based on the local database sync
                TechUserID = closestTechnician();
                // check method whether via online or offline
                    if (TechUserID == -1 || TechUserID == 0) {
                        Toast.makeText(getActivity(), "Error Finding Users", Toast.LENGTH_LONG).show();
                        return;
                    } else if (getActivity() != null) {
                        // prev revision
                        //sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        // current version
                        sharedPref = getActivity().getSharedPreferences("<Pref Name>", MODE_PRIVATE);
                        int userID = Integer.valueOf(sharedPref.getString(getString(R.string.userIdLoggedIn), ""));
                        if (getActivity() != null) {
                            securifyUserDatabaseHelper db = new securifyUserDatabaseHelper(getActivity());
                            List<usersLocal> UserList = db.getUserOnUserID(userID);
                            usersLocal ChosenUser = new usersLocal();
                            for (int i = 0; i < UserList.size(); i++) {
                                ChosenUser = UserList.get(i);
                            }
                            List<usersLocal> Clientlist = db.getUserOnUserID(TechUserID);
                            usersLocal ChosenTech = new usersLocal();
                            for (int i = 0; i < Clientlist.size(); i++) {
                                ChosenTech = Clientlist.get(i);
                            }
                            if (isOnline()) {
                                // calculate ETA from the current location and the next location
                                // create ASYNC with two of the user as a user list and divided them out within the task
                                ArrayList<usersLocal> sendList = new ArrayList<usersLocal>();
                                sendList.add(ChosenUser);
                                sendList.add(ChosenTech);
                                getDistanceGoogleDialog AsyncTask = new getDistanceGoogleDialog(sendList);
                                // calculate ETA from the current location and the next location
                                // send async on request
                                // at the end of async request create dialog
                                // for the dialog
                                AsyncTask.execute(new String[]{getString(R.string.ServerURI)});
                            } else {
                                if(getActivity() != null) {
                                    Toast.makeText(getActivity(), "Using Offline Method", Toast.LENGTH_LONG).show();
                                    Bundle args = new Bundle();
                                    args.putString(ARG_CLIENT_PARAM_NAME, ChosenUser.getFname() + " " + ChosenUser.getLname());
                                    args.putString(ARG_TECHNI_PARAM_NAME, ChosenTech.getFname() + " " + ChosenTech.getLname());
                                    // need to be calculated using the local method

                                    //private static Double CalculateDistance(String Lat1, String Lon1, String Lat2, String Lon2)
                                    double distance = CalculateDistance(
                                            String.valueOf(ChosenUser.getGps_lati()),
                                            String.valueOf(ChosenUser.getGps_long()),
                                            String.valueOf(ChosenTech.getGps_lati()),
                                            String.valueOf(ChosenTech.getGps_long()));

                                    String DistanceStringCalc = String.format("%.3f", distance);

                                    // average speed of 40km/hr there for
                                    double timeETALocal = (distance / 40 * 60) + 5;
                                    String EstimateCalc = String.format("%.3f", timeETALocal);

                                    args.putString(ARG_ETA, DistanceStringCalc + " " + "KM");
                                    args.putString(ARG_DISTANCE, EstimateCalc + " " + "Minutes");
                                    //
                                    args.putString(ARG_PHONE, ChosenTech.getPhone());
                                    args.putString(ARG_LATI1_CLIENT, String.valueOf(ChosenUser.getGps_lati()));
                                    args.putString(ARG_LONG1_CLIENT, String.valueOf(ChosenUser.getGps_long()));
                                    args.putString(ARG_LATI2_TECHNI, String.valueOf(ChosenTech.getGps_lati()));
                                    args.putString(ARG_LONG2_TECHNI, String.valueOf(ChosenTech.getGps_long()));
                                    args.putString(ARG_IS_CONFIRMED, String.valueOf(0));
                                    FragmentManager fragmentManager = getChildFragmentManager();
                                    // offline version
                                    dialogRequestFragmentOffline requestPopUp = new dialogRequestFragmentOffline();
                                    requestPopUp.setArguments(args);
                                    requestPopUp.show(fragmentManager, "sam3");
                                }
                            }
                        }
                    }

            }
            // calculate ETA from the current location and the next location
            // send async on request
            // at the end of async request create dialog
            // for the dialog
        });

        // get closest technician
        int TechUserID = closestTechnician();
        if (TechUserID == -1 || TechUserID == 0) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Error Finding Users", Toast.LENGTH_LONG).show();
            }
        } else if (getActivity() != null) {
            // initial version 3/10/2017
            // sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            // current test
            sharedPref = getActivity().getSharedPreferences("<Pref Name>", MODE_PRIVATE);
            int userID = Integer.valueOf(sharedPref.getString(getString(R.string.userIdLoggedIn), ""));
            if (getActivity() != null) {
                securifyUserDatabaseHelper db = new securifyUserDatabaseHelper(getActivity());

                List<usersLocal> UserList = db.getUserOnUserID(userID);
                usersLocal ChosenUser = new usersLocal();
                for (int i = 0; i < UserList.size(); i++) {
                    ChosenUser = UserList.get(i);
                }

                List<usersLocal> Clientlist = db.getUserOnUserID(TechUserID);
                usersLocal ChosenTech = new usersLocal();
                for (int i = 0; i < Clientlist.size(); i++) {
                    ChosenTech = Clientlist.get(i);
                }
                // calculate ETA from the current location and the next location
                // create ASYNC with two of the user as a user list and divided them out within the task
                ArrayList<usersLocal> sendList = new ArrayList<usersLocal>();
                sendList.add(ChosenUser);
                sendList.add(ChosenTech);
                getDistanceGoogleHUD AsyncTask = new getDistanceGoogleHUD(sendList);
                // calculate ETA from the current location and the next location
                // send async on request
                // at the end of async request create dialog
                // for the dialog
                AsyncTask.execute(new String[]{getString(R.string.ServerURI)});
                // update the ETA field everytime
            }
        }
        return this.inflatedView;
    }

    // get the distance matrix API
    //https://maps.googleapis.com/maps/api/distancematrix/json?origins=54.406505,18.67708&destinations=54.446251,18.570993
    //&mode=driving&language=en-EN&sensor=false&key=AIzaSyBs1XJE34XAi6oy4x-1EkI4-fBOtBnTzAM
    public String distanceMakeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/distancematrix/json");
        urlString.append("?origins=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destinations=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&mode=driving&language=en-EN&sensor=false");
        urlString.append("&key=" + getString(R.string.googleAPICredetialServer));
        return urlString.toString();
    }

    //                                                         // post return type // doInBackground return type
    private class getDistanceGoogleDialog extends AsyncTask<String, String, String> {
        private ArrayList<usersLocal> chosenUsers = new ArrayList<usersLocal>();
        private usersLocal chosenClient;
        private usersLocal chosenTechnician;
        private double SourceLat;
        private double SourceLog;
        private double DestLat;
        private double DesLog;
        private String result;
        Exception error;

        // constructor
        public getDistanceGoogleDialog(ArrayList<usersLocal> Arg_chosenUsers) {
            chosenUsers = Arg_chosenUsers;
            for (int i = 0; i < chosenUsers.size(); i++) {
                if (i == 0) {
                    chosenClient = chosenUsers.get(i);
                } else if (i == 1) {
                    chosenTechnician = chosenUsers.get(i);
                }
            }
            // client lat long
            SourceLat = chosenClient.getGps_lati();
            SourceLog = chosenClient.getGps_long();
            // techni lat long
            DestLat = chosenTechnician.getGps_lati();
            DesLog = chosenTechnician.getGps_long();
        }

        // no try catch forthe sake of progressing wiht the task need to add it later !!
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("LOADING");
                progressDialog.setMessage("Getting Direction");
                progressDialog.show();
            }
        }


        @Override
        protected String doInBackground(String... urls) {
            // we use the OkHttp library from https://github.com/square/okhttp
            final String uri_target = distanceMakeURL(SourceLat, SourceLog, DestLat, DesLog);
            // get password salt first
            // uri request builder
            Uri buildUrSalt = Uri.parse(uri_target)
                    .buildUpon()
                    .build();
            this.result = generalHTTPQuest(buildUrSalt.toString());
            return this.result;
        }

        @Override
        protected void onPostExecute(String AYSNCresult) {
            progressDialog.dismiss();
            boolean JSON_flag = false;
            // update preference based on the passed email address
            // inside fragment thus
            //if (AYSNCresult) {
            // get the boolean to be overwritten
            JSON_flag = getEstimation(AYSNCresult);
            if (JSON_flag) {
                // TODO: FINISH THE DIALOG POPUP
                Bundle args = new Bundle();
                args.putString(ARG_CLIENT_PARAM_NAME, chosenClient.getFname() + " " + chosenClient.getLname());
                args.putString(ARG_TECHNI_PARAM_NAME, chosenTechnician.getFname() + " " +chosenTechnician.getLname());
                args.putString(ARG_ETA, durationStringEstimation);
                args.putString(ARG_DISTANCE, distanceStringEstimation);
                args.putString(ARG_PHONE, chosenTechnician.getPhone());
                args.putString(ARG_LATI1_CLIENT, String.valueOf(chosenClient.getGps_lati()));
                args.putString(ARG_LONG1_CLIENT, String.valueOf(chosenClient.getGps_long()));
                args.putString(ARG_LATI2_TECHNI, String.valueOf(chosenTechnician.getGps_lati()));
                args.putString(ARG_LONG2_TECHNI, String.valueOf(chosenTechnician.getGps_long()));
                args.putString(ARG_IS_CONFIRMED, String.valueOf(0));
                if (getActivity() != null) {
                    SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);
                    if (regId == null) {
                        Toast.makeText(getActivity(), "Not Registered On Firebase", Toast.LENGTH_LONG).show();
                    } else {
                        args.putString(ARG_CLIENT_FIREBASE_ID, regId);
                        args.putString(ARG_CLIENT_USER_ID, String.valueOf(chosenClient.getUserID()));
                        args.putString(ARG_TECHNI_FIREBASE_ID, "");
                        args.putString(ARG_TECHNI_USER_ID, String.valueOf(chosenTechnician.getUserID()));
                        FragmentManager fragmentManager = getChildFragmentManager();
                        dialogRequestFragment requestPopUp = new dialogRequestFragment();
                        requestPopUp.setArguments(args);
                        requestPopUp.show(fragmentManager, "sam");
                    }
                }
            }
        }
    }

        private String distanceStringEstimation = null;
        private String durationStringEstimation = null;

        //The parameter is the server response
        private boolean getEstimation(String result) {
            Exception error;
            try {
                //Parsing json
                final JSONObject jsonRespRouteDistance = new JSONObject(result);
                String status = jsonRespRouteDistance.getString("status");
                if (status.equals("OK")) {

                    JSONObject jsonRespData = jsonRespRouteDistance.getJSONArray("rows")
                            .getJSONObject(0)
                            .getJSONArray("elements")
                            .getJSONObject(0);
                    if(jsonRespData.getString("status").equals("OK")) {
                        JSONObject jsonDistance = jsonRespData.getJSONObject("distance");
                        this.distanceStringEstimation = jsonDistance.get("text").toString();

                        JSONObject jsonDuration = jsonRespData.getJSONObject("duration");
                        this.durationStringEstimation = jsonDuration.get("text").toString();
                        return true;
                    }
                    else{
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (JSONException e) {
                error = e;
                return false;
            }
        }

    // Async syntax AsyncTask <TypeOfVarArgParams, ProgressValue, ResultValue>
    private class SERVERUpdateCurrentLocation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            if(getActivity() != null) {
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
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if(isAdded()){
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
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // by default the map has to be reinstated all the time to ensure up to date location
        initializeMap();
    }

    // dodgy code probably fix it tho

    private int closestTechnician() {
        int currentTechnicianID;
        int userID;
        if (getActivity() != null) {
            // initial version 3/10/2017
            // sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            // testing version
            sharedPref = getActivity().getSharedPreferences("<Pref Name>", MODE_PRIVATE);
            userID = Integer.valueOf(sharedPref.getString(getString(R.string.userIdLoggedIn), ""));
        } else {
            return currentTechnicianID = -1;
        }
        securifyUserDatabaseHelper db;
        // get all the users list based on the database
        if (getActivity() != null) {
            db = new securifyUserDatabaseHelper(getActivity());
        } else {
            return currentTechnicianID = -1;
        }
        // create an instance of userslist which has all the user database inside it
        // get all the users that has the current userID
        List<usersLocal> ListcurrentUserID = db.getUserOnUserID(userID);
        usersLocal currentUser = null;
        for (int i = 0; i < ListcurrentUserID.size(); i++) {
            currentUser = ListcurrentUserID.get(i);
        }
        List<usersLocal> userList = db.getAllUserExceptUserID(userID);
        // check if the currentUser has been initialized properly.
        double shortestDistance = -1;
        currentTechnicianID = 0;
        for (int i = 0; i < userList.size(); i++) {
            double temp_distance = -1;
            usersLocal user = userList.get(i);
            // only filter whom are online
            if (user.getIsOnline() != 0) {
                // eliminate the calculation from the current user
                //private static Double CalculateDistance(String Lat1, String Lon1, String Lat2, String Lon2)
                String lat1 = String.valueOf(currentUser.getGps_lati());
                String lon1 = String.valueOf(currentUser.getGps_long());
                String lat2 = String.valueOf(user.getGps_lati());
                String lon2 = String.valueOf(user.getGps_long());
                temp_distance = CalculateDistance(lat1, lon1, lat2, lon2);
                // if its within the first execution it will be the shortest
                if (i == 0) {
                    shortestDistance = temp_distance;
                    currentTechnicianID = user.getUserID();
                }
                // if not the first execution and the shortestDistance is greater than calc
                else if (i != 0 && shortestDistance > temp_distance) {
                    // asign shortest distance = calc_temporary distance.
                    shortestDistance = temp_distance;
                    // assign the user ID
                    currentTechnicianID = user.getUserID();
                    //closestTechnician = user;
                }
            }
        }
        return currentTechnicianID;
    }

    public void SetZoomlevel(ArrayList<LatLng> listLatLng) {
        final GoogleMap googleMap = mMap;
        if (listLatLng != null && listLatLng.size() == 1) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 10));
        } else if (listLatLng != null && listLatLng.size() > 1) {
            final Builder builder = LatLngBounds.builder();
            for (int i = 0; i < listLatLng.size(); i++) {
                builder.include(listLatLng.get(i));
            }
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map_container);
                // set camera to view all the marker within the map
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), inflatedView.findViewById(R.id.map_container)
                        .getWidth(), inflatedView.findViewById(R.id.map_container).getHeight(), 80));
            }
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
        } else {
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


    // make the list of all drawn latlngbean list as private so it remembers and can delete what has bee plotted
    private ArrayList<LatLngBean> usersListLatLngBean;

    private void setData() {
        // remove the old marker
        // if its the first execution then there is no need to remove the marker

        RemovingLocations(listMarker);
        // instantiate a new markerlist
        this.usersListLatLngBean = new ArrayList<LatLngBean>();
        // create an instance of the database helper
        if (getActivity() != null) {
            securifyUserDatabaseHelper db = new securifyUserDatabaseHelper(getActivity());
            // create an instance of userslist which has all the user database inside it
            List<usersLocal> userList = db.getAllUser();
            // get all location of the technicians which are online within the area THAT IS NOT USER 1
            if (getActivity() != null) {
                // initial 3/10/2017
                // sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                // currently testing
                sharedPref = getActivity().getSharedPreferences("<Pref Name>", MODE_PRIVATE);
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
                            bean.setIsOnline(user.getIsOnline());
                        }
                        // add to the list of bean
                        usersListLatLngBean.add(bean);
                    }
                }
                // advance feature is to change the amount of
                LoadingLocations(usersListLatLngBean);
            }
        }
    }

    private ArrayList<LatLng> listLatLng;
    private ArrayList<Marker> listMarker = new ArrayList<Marker>();

    private void LoadingLocations(ArrayList<LatLngBean> arrayList) {
        if (arrayList.size() > 0) {
            try {
                listLatLng = new ArrayList<LatLng>();
                for (int i = 0; i < arrayList.size(); i++) {
                    LatLngBean bean = arrayList.get(i);
                    if (bean.getLatitude().length() > 0 && bean.getLongitude().length() > 0) {
                        double lat = Double.parseDouble(bean.getLatitude());
                        double lon = Double.parseDouble(bean.getLongitude());
                        // create a new list of marker
                        // only create maker for those who are online by the local database
                        if(bean.getIsOnline() == 1)
                        {
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lon))
                                .title(bean.getTitle())
                                .snippet(bean.getSnippet())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            // add the marker on the local list
                            this.listMarker.add(marker);
                            //Add Marker to Hashmap
                            hashMapMarker.put(marker, bean);
                            //Set Zoom Level of Map pin
                            LatLng object = new LatLng(lat, lon);
                            listLatLng.add(object);
                        }

                    }
                }
                //SetZoomlevel(listLatLng);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker position) {
                    LatLngBean bean = hashMapMarker.get(position);
                    if(getActivity() != null) {
                        Toast.makeText(getActivity(), bean.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            //Toast.makeText(getActivity(), "Sorry! unable to load markers", Toast.LENGTH_SHORT).show();
        }
    }

    private void RemovingLocations(ArrayList<Marker> markerArgument) {
        if (markerArgument.size() > 0) {
            try {
                for (int i = 0; i < markerArgument.size(); i++) {
                    // get the current value being pointed
                    Marker markerToRemove = markerArgument.get(i);
                    // remove the marker
                    markerToRemove.remove();
                }
                this.listMarker = new ArrayList<Marker>();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            //Toast.makeText(getActivity(), "Sorry! unable to remove marker", Toast.LENGTH_SHORT).show();
        }
    }


    protected synchronized void buildGoogleApiClient() {
        if (getActivity() != null) {
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
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
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
    public void onLocationChanged(Location location) {
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
        if (getActivity() != null) {
            // currently testing 3/10/2017
            //sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            // testing
            sharedPref = getActivity().getSharedPreferences("<Pref Name>", MODE_PRIVATE);
            if (sharedPref != null) {
                userID = sharedPref.getString(getString(R.string.userIdLoggedIn), "");
                isOnline = "1";
                SERVERUpdateCurrentLocation asyncTask = new SERVERUpdateCurrentLocation();
                asyncTask.execute(new String[]{getString(R.string.ServerURI)});
            }
        }
    }

    private class getDistanceGoogleHUD extends AsyncTask<String, String, String> {
        private ArrayList<usersLocal> chosenUsers = new ArrayList<usersLocal>();
        private usersLocal chosenClient;
        private usersLocal chosenTechnician;
        private double SourceLat;
        private double SourceLog;
        private double DestLat;
        private double DesLog;
        private String result;
        Exception error;

        // constructor
        public getDistanceGoogleHUD(ArrayList<usersLocal> Arg_chosenUsers) {
            chosenUsers = Arg_chosenUsers;
            for (int i = 0; i < chosenUsers.size(); i++) {
                if (i == 0) {
                    chosenClient = chosenUsers.get(i);
                } else if (i == 1) {
                    chosenTechnician = chosenUsers.get(i);
                }
            }
            // client lat long
            SourceLat = chosenClient.getGps_lati();
            SourceLog = chosenClient.getGps_long();
            // techni lat long
            DestLat = chosenTechnician.getGps_lati();
            DesLog = chosenTechnician.getGps_long();
        }

        @Override
        protected String doInBackground(String... urls) {
            if(getActivity() != null) {
                // we use the OkHttp library from https://github.com/square/okhttp
                final String uri_target = distanceMakeURL(SourceLat, SourceLog, DestLat, DesLog);
                // get password salt first
                // uri request builder
                Uri buildUrSalt = Uri.parse(uri_target)
                        .buildUpon()
                        .build();
                this.result = generalHTTPQuest(buildUrSalt.toString());
                return this.result;
            }
            return "";
        }

        @Override
        protected void onPostExecute(String AYSNCresult) {
            if (isAdded()) {
                boolean JSON_flag = false;
                // update preference based on the passed email address
                // inside fragment thus
                //if (AYSNCresult) {
                // get the boolean to be overwritten
                JSON_flag = getEstimation(AYSNCresult);
                if (JSON_flag) {
                    // TODO: FINISH THE DIALOG POPUP
                    StatusETA.setText("ETA: " + durationStringEstimation);
                    DistanceETA.setText("Distance: " + distanceStringEstimation);
                }
            }
        }
    }


        @Override
        public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (getActivity() != null) {
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String DirectionMakeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origins=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destinations=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=" + getString(R.string.googleAPICredetialServer));
        return urlString.toString();
    }

    private class getDirectionGoogle extends AsyncTask<String, Void, Boolean> {
        private double SourceLat;
        private double SourceLog;
        private double DestLat;
        private double DesLog;
        private String result;
        Exception error;

        // constructor
        public getDirectionGoogle(double sourcelat, double sourcelog, double destlat, double destlog) {
            SourceLat = sourcelat;
            SourceLog = sourcelog;
            DestLat = destlat;
            DesLog = destlog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("LOADING");
                progressDialog.setMessage("Getting Direction");
                progressDialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                // we use the OkHttp library from https://github.com/square/okhttp
                final String uri_target = DirectionMakeURL(SourceLat, SourceLog, DestLat, DesLog);
                // get password salt first
                // uri request builder
                Uri buildUrSalt = Uri.parse(uri_target)
                        .buildUpon()
                        .build();
                this.result = generalHTTPQuest(buildUrSalt.toString());
                return true;
            } catch (Exception e) {
                error = e;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean AYSNCresult) {
            progressDialog.dismiss();
            // update preference based on the passed email address
            // inside fragment thus
            if (AYSNCresult) {
                drawPath(this.result);
            }
        }
    }

    //The parameter is the server response
    public void drawPath(String result) {
        try {
            //Parsing json
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(10)
                    .color(Color.RED)
                    .geodesic(true)
            );
        } catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    private class getDistanceGoogle extends AsyncTask<String, Void, Boolean> {
        private double SourceLat;
        private double SourceLog;
        private double DestLat;
        private double DesLog;
        private String result;
        Exception error;

        // constructor
        public getDistanceGoogle(double sourcelat, double sourcelog, double destlat, double destlog) {
            SourceLat = sourcelat;
            SourceLog = sourcelog;
            DestLat = destlat;
            DesLog = destlog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("LOADING");
                progressDialog.setMessage("Getting Estimation");
                progressDialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                // we use the OkHttp library from https://github.com/square/okhttp
                final String uri_target = distanceMakeURL(SourceLat, SourceLog, DestLat, DesLog);
                // get password salt first
                // uri request builder
                Uri buildUrSalt = Uri.parse(uri_target)
                        .buildUpon()
                        .build();
                this.result = generalHTTPQuest(buildUrSalt.toString());
                return true;
            } catch (Exception e) {
                error = e;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean AYSNCresult) {
            progressDialog.dismiss();
            // update preference based on the passed email address
            // inside fragment thus
            if (AYSNCresult) {
                //getEstimation(this.result);
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
            final String uri_target = new String(getString(R.string.ServerURI));
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
                    // make sure its updateUser in this field
                    // a new user join the ecosystem would not be in the database until the user is logged back in
                    // a bug that needs to be fixed
                    databaseHelper.updateUser(userLocal);
                }
                status_result = 1;
                databaseHelper.closeDB();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status_result == -1) {
                return "Cannot Connect To The Server";
            } else if (status_result == 0) {
                return "Query Error";
            } else {
                return "Database Synchronized";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Database Synchronized")) {
                setData();
            }
        }
    }

    private ProgressDialog progressDialog;

    // update the firebase on the database
    private class SendFireBaseID extends AsyncTask<String, Void, String> {

        final String COLUMN_USERID = "userID";
        final String COLUMN_FIREBASE_ID = "FireBase_ID";

        private String firebase_ID_instance;
        private String userID_instance;

        // constructor
        public SendFireBaseID(String userID, String firebase_ID) {
            this.firebase_ID_instance = firebase_ID;
            this.userID_instance = userID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("LOADING");
                progressDialog.setMessage("Synchronizing Firebase ID");
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            // we use the OkHttp library from https://github.com/square/okhttp
            final String uri_target = new String(getString(R.string.ServerURI));
            final String page_target_salt = new String("updateFirebaseID.php?");
            // get password salt first
            // uri request builder
            Uri buildUrSalt = Uri.parse(uri_target + page_target_salt)
                    .buildUpon()
                    .appendQueryParameter(COLUMN_FIREBASE_ID, this.firebase_ID_instance)
                    .appendQueryParameter(COLUMN_USERID, this.userID_instance)
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
                return "Invalid Credentials";
            } else if (status_result == 1) {
                return "Update Success";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            // update preference based on the passed email address
            // inside fragment thus
            if (result.equals("Update success")) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    // https://rosettacode.org/wiki/Haversine_formula#Java
    private Double CalculateDistance(String Lat1, String Lon1, String Lat2, String Lon2) {
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