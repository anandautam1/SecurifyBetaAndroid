package org.farmate.securifybeta.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.android.gms.vision.text.Text;

import org.farmate.securifybeta.R;
import org.farmate.securifybeta.activity.LoginActivity;
import org.farmate.securifybeta.activity.StartActivity;
import org.farmate.securifybeta.app.Config;
import org.farmate.securifybeta.database.LatLngBean;
import org.farmate.securifybeta.database.securifyUserDatabaseHelper;
import org.farmate.securifybeta.database.usersLocal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.farmate.securifybeta.activity.LoginActivity.generalHTTPQuest;


// map fragment work on the dialog
// https://stackoverflow.com/questions/15853066/getting-a-mapfragment-to-work-in-an-alertdialog

// map fragment on polyline draw paths
// https://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/

public class dialogRequestFragment extends DialogFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // for the dialog
    private final String ARG_TITLE = "title";
    private final String ARG_MESSAGE = "message";
    private final String ARG_PUSHTYPE = "push_type";
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

    private String CLIENT_PARAM_NAME;
    private String TECHNI_PARAM_NAME;
    private String ETA;
    private String DISTANCE;
    private String PHONE;
    private String LATI1_CLIENT;
    private String LONG1_CLIENT;
    private String LATI2_TECHNI;
    private String LONG2_TECHNI;
    private String IS_CONFIRMED;
    private String CLIENT_FIREBASE_ID;
    private String CLIENT_USER_ID;
    private String TECHNI_FIREBASE_ID;
    private String TECHNI_USER_ID;


    private View inflatedView;

    private GoogleMap mMap;
    private SupportMapFragment mapFrag;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public dialogRequestFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static dialogRequestFragment newInstance(String param1, String param2) {
        dialogRequestFragment fragment = new dialogRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayList<LatLng> listLatLng = new ArrayList<LatLng>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mArgs = getArguments();
        if (mArgs != null) {
            CLIENT_PARAM_NAME = mArgs.getString(ARG_CLIENT_PARAM_NAME);
            TECHNI_PARAM_NAME = mArgs.getString(ARG_TECHNI_PARAM_NAME);
            ETA = mArgs.getString(ARG_ETA);
            DISTANCE = mArgs.getString(ARG_DISTANCE);
            PHONE = mArgs.getString(ARG_PHONE);
            LATI1_CLIENT = mArgs.getString(ARG_LATI1_CLIENT);
            LONG1_CLIENT = mArgs.getString(ARG_LONG1_CLIENT);
            LATI2_TECHNI = mArgs.getString(ARG_LATI2_TECHNI);
            LONG2_TECHNI = mArgs.getString(ARG_LONG2_TECHNI);
            IS_CONFIRMED = mArgs.getString(ARG_IS_CONFIRMED);
            CLIENT_FIREBASE_ID = mArgs.getString(ARG_CLIENT_FIREBASE_ID);
            CLIENT_USER_ID = mArgs.getString(ARG_CLIENT_USER_ID);
            TECHNI_FIREBASE_ID = mArgs.getString(ARG_TECHNI_FIREBASE_ID);
            TECHNI_USER_ID = mArgs.getString(ARG_TECHNI_USER_ID);
            // create a ArrayList<LatLng> listLatLng for the map to zoom into
            LatLng CLIENT_LATLNG = new LatLng(Double.parseDouble(LATI1_CLIENT),Double.parseDouble(LONG1_CLIENT));
            LatLng TECHNI_LATLNG = new LatLng(Double.parseDouble(LATI2_TECHNI),Double.parseDouble(LONG2_TECHNI));
            listLatLng.add(CLIENT_LATLNG);
            listLatLng.add(TECHNI_LATLNG);
        }
    }

    private SupportMapFragment supportMapFragment;
    private Button AcceptButton;
    private Button DeclineButton;

    private TextView FullNameTech_status;
    private TextView ETA_status;
    private TextView Distance_status;
    private TextView PhoneNumber_Status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_dialog_request, container, false);

        FullNameTech_status = (TextView) inflatedView.findViewById(R.id.FirstLastNameTechnician);
        // if request is zero and TECHNI firebase id does exist --> display the client
        if(IS_CONFIRMED.equals("0") && !TECHNI_FIREBASE_ID.isEmpty()) {
            FullNameTech_status.setText(CLIENT_PARAM_NAME);
        } // if request is zero and TECHNI firebase id does not exist --> display the tecnician
        else if(IS_CONFIRMED.equals("1") && !TECHNI_FIREBASE_ID.isEmpty()){
            FullNameTech_status.setText(TECHNI_PARAM_NAME);
        }
        else {
            FullNameTech_status.setText(TECHNI_PARAM_NAME);
        }
        // if request is zero and TECHNI firebase id does not exist --> display the tecnician

        ETA_status = (TextView) inflatedView.findViewById(R.id.Estimation);
        ETA_status.setText(ETA);

        Distance_status = (TextView) inflatedView.findViewById(R.id.Distance);
        Distance_status.setText(DISTANCE);

        PhoneNumber_Status = (TextView) inflatedView.findViewById(R.id.PhoneNumber);
        PhoneNumber_Status.setText(PHONE);

        AcceptButton = (Button) inflatedView.findViewById(R.id.buttonAcceptRequest);
        AcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send async to the server
                sendRequestDialog AsyncTask = new sendRequestDialog();
                AsyncTask.execute(new String[]{getString(R.string.ServerURI)});

            }
            });
        DeclineButton = (Button) inflatedView.findViewById(R.id.buttonDeclineRequest);
        DeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return inflatedView;
    }

    // TODO: FINISH THE ASYNC TO GET THE FIREBASE ID OF THE CLIENT
    // ASSSUME THE USER IS ONLINE IN THIS CASE
    // Async syntax AsyncTask <TypeOfVarArgParams, ProgressValue, ResultValue>
    private class sendRequestDialog extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("LOADING");
                progressDialog.setMessage("Sending Request");
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            // we use the OkHttp library from https://github.com/square/okhttp
            final String uri_target= new String(getString(R.string.ServerURI));
            final String page_directory = new String("jobActivity/");
            final String page_target_salt = new String("firebaseSendRequest.php?");
            // get password salt first
            if(!IS_CONFIRMED.equals("1"))
            {
                TECHNI_FIREBASE_ID = "XXXMLG";
            }
            // uri request builder
            Uri buildUrSalt = Uri.parse(uri_target + page_directory + page_target_salt)
                    .buildUpon()
                    .appendQueryParameter(ARG_TITLE, "Job Request")
                    .appendQueryParameter(ARG_MESSAGE, "Urgent Repair")
                    .appendQueryParameter(ARG_PUSHTYPE, "individual")
                    .appendQueryParameter(ARG_CLIENT_PARAM_NAME, CLIENT_PARAM_NAME)
                    .appendQueryParameter(ARG_TECHNI_PARAM_NAME, TECHNI_PARAM_NAME)
                    .appendQueryParameter(ARG_ETA, ETA)
                    .appendQueryParameter(ARG_DISTANCE,DISTANCE )
                    .appendQueryParameter(ARG_PHONE, PHONE)
                    .appendQueryParameter(ARG_LATI1_CLIENT, LATI1_CLIENT)
                    .appendQueryParameter(ARG_LONG1_CLIENT, LONG1_CLIENT)
                    .appendQueryParameter(ARG_LATI2_TECHNI, LATI2_TECHNI)
                    .appendQueryParameter(ARG_LONG2_TECHNI, LONG2_TECHNI)
                    .appendQueryParameter(ARG_IS_CONFIRMED, IS_CONFIRMED)
                    .appendQueryParameter(ARG_CLIENT_FIREBASE_ID, CLIENT_FIREBASE_ID)
                    .appendQueryParameter(ARG_CLIENT_USER_ID, CLIENT_USER_ID)
                    .appendQueryParameter(ARG_TECHNI_FIREBASE_ID, TECHNI_FIREBASE_ID)
                    .appendQueryParameter(ARG_TECHNI_USER_ID, TECHNI_USER_ID)
                    .build();
            String result = generalHTTPQuest(buildUrSalt.toString());
            String psaltResult = "";
            int status_result = -1;
            try {
                JSONObject reader = new JSONObject(result);
                status_result = reader.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status_result == -1) {
                return "Cannot Send Request";
            }
            else if(status_result == 1){
                return "Request Sent";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            // start new intent
            if(result.equals("Request Sent")) {
                dismiss();
            }
        }
    }
    // TODO: FINISH THE ASYNC TO SEND THE FIREBASE ID BUNDLE TO THE CLIENT
    // ASSUME THE USER IS ONLINE IN THIS CASE

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // by default the map has to be reinstated all the time to ensure up to date location
        initializeMap();
    }

    private void initializeMap() {
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.dialogMapFragment, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
    }

    // fix bug by using this
    // https://stackoverflow.com/questions/14928833/android-app-error-duplicate-id-0x7f04000f-tag-null-or-parent-id-0x0-with-ano
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.dialogMapFragment));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
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
        // async to draw the polyline
        LoadingLocations(listLatLng);
        getDirectionGoogle asyncTask = new getDirectionGoogle(
                listLatLng.get(0).latitude,
                listLatLng.get(0).longitude,
                listLatLng.get(1).latitude,
                listLatLng.get(1).longitude);
        asyncTask.execute(new String[] {getString(R.string.ServerURI)});
    }

    private void LoadingLocations(ArrayList<LatLng> arrayList) {
        if (arrayList.size() > 0) {
            try {
                listLatLng = new ArrayList<LatLng>();
                for (int i = 0; i < arrayList.size(); i++) {
                    LatLng bean = arrayList.get(i);
                        double lat = bean.latitude;
                        double lon = bean.longitude;
                        // create a new list of marker
                        // only create maker for those who are online by the local database
                        if(i == 0) {
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lon))
                                    .title("Client")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }
                        if(i == 1) {
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lon))
                                    .title("Technician")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        }
                            //Set Zoom Level of Map pin
                            LatLng object = new LatLng(lat, lon);
                            listLatLng.add(object);
                        }
                //SetZoomlevel(listLatLng);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Sorry! unable to load markers", Toast.LENGTH_SHORT).show();
        }
    }

    public void SetZoomlevel(ArrayList<LatLng> listLatLng) {
        final GoogleMap googleMap = mMap;
        if (listLatLng != null && listLatLng.size() == 1) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 13));
        } else if (listLatLng != null && listLatLng.size() > 1) {
            final LatLngBounds.Builder builder = LatLngBounds.builder();
            for (int i = 0; i < listLatLng.size(); i++) {
                builder.include(listLatLng.get(i));
            }
            if(getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.dialogMapFragment);
                // set camera to view all the marker within the map
                int width = getResources().getDisplayMetrics().widthPixels;
                int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), inflatedView.findViewById(R.id.dialogMapFragment)
                        .getWidth(), inflatedView.findViewById(R.id.dialogMapFragment).getHeight(), padding));
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // TODO: DRAW THE POLYLINE BASED ON THE GIVEN LONG LAT OF THE CURRENT LOCATION OF THE USER
    // ASSUME THE USER IS ONLINE

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

    public String DirectionMakeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=" + getString(R.string.googleAPICredetialServer));
        return urlString.toString();
    }

    // async task appropritate
    private ProgressDialog progressDialog;

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
                // must be called here to get all the zoom perspective of the map initialization
                SetZoomlevel(listLatLng);
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
            // need to point to the right mMap within the fragment
            if(getActivity() != null) {
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .addAll(list)
                        .width(10)
                        .color(Color.RED)
                        .geodesic(true));

            }
        } catch (JSONException e) {

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

}
