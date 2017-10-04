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

public class dialogRequestFragmentOffline extends DialogFragment{
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

    public dialogRequestFragmentOffline() {
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
        this.inflatedView = inflater.inflate(R.layout.fragment_dialog_request_fragment_offline, container, false);

        FullNameTech_status = (TextView) inflatedView.findViewById(R.id.FirstLastNameTechnician);
        // if request is zero and TECHNI firebase id does exist --> display the client
            FullNameTech_status.setText(TECHNI_PARAM_NAME);
        // if request is zero and TECHNI firebase id does not exist --> display the tecnician

        ETA_status = (TextView) inflatedView.findViewById(R.id.Estimation);
        ETA_status.setText(ETA);

        Distance_status = (TextView) inflatedView.findViewById(R.id.Distance);
        Distance_status.setText(DISTANCE);

        PhoneNumber_Status = (TextView) inflatedView.findViewById(R.id.PhoneNumber);
        PhoneNumber_Status.setText(PHONE);

        DeclineButton = (Button) inflatedView.findViewById(R.id.buttonDeclineRequest);
        DeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // by default the map has to be reinstated all the time to ensure up to date location
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
}
