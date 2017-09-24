package org.farmate.securifybeta.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.vision.text.Text;

import org.farmate.securifybeta.R;
import org.farmate.securifybeta.activity.LoginActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


// map fragment work on the dialog
// https://stackoverflow.com/questions/15853066/getting-a-mapfragment-to-work-in-an-alertdialog

// map fragment on polyline draw paths
// https://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/

public class dialogRequestFragment extends DialogFragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String ARG_PARAM_NAME = "Name_key";
    private final String ARG_ETA = "ETA_key";
    private final String ARG_PHONE = "Phone_Key";
    private final String ARG_LATI1 = "LATI_1_key";
    private final String ARG_LONG1 = "LONG_1_key";
    private final String ARG_LATI2 = "LATI_2_key";
    private final String ARG_LONG2 = "LONG_2_key";

    private String PARAM_NAME;
    private String ETA;
    private String PHONE;
    private String LATI1;
    private String LONG1;
    private String LATI2;
    private String LONG2;

    private View inflatedView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mArgs = getArguments();
        if (mArgs != null) {
            PARAM_NAME = mArgs.getString(ARG_PARAM_NAME);
            ETA = mArgs.getString(ARG_ETA);
            PHONE = mArgs.getString(ARG_PHONE);
            LATI1 = mArgs.getString(ARG_LATI1);
            LONG1 = mArgs.getString(ARG_LONG1);
            LATI2 = mArgs.getString(ARG_LATI2);
            LONG2 = mArgs.getString(ARG_LONG2);
        }
    }

    private SupportMapFragment supportMapFragment;
    private Button AcceptButton;
    private Button DeclineButton;
    private TextView FullNameTech_status;
    private TextView ETA_status;
    private TextView PhoneNumber_Status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_dialog_request, container, false);

        FullNameTech_status = (TextView) inflatedView.findViewById(R.id.FirstLastNameTechnician);
        FullNameTech_status.setText(PARAM_NAME);

        ETA_status = (TextView) inflatedView.findViewById(R.id.Estimation);
        ETA_status.setText(ETA);

        PhoneNumber_Status = (TextView) inflatedView.findViewById(R.id.PhoneNumber);
        PhoneNumber_Status.setText(PHONE);

        AcceptButton = (Button) inflatedView.findViewById(R.id.buttonAcceptRequest);
        AcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                //dismiss();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

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


}
