package org.farmate.securifybeta.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import org.farmate.securifybeta.R;
import org.farmate.securifybeta.activity.CameraCaptureActivity;
import org.farmate.securifybeta.activity.LoginActivity;
import org.farmate.securifybeta.activity.RegisterActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.farmate.securifybeta.activity.LoginActivity.generalHTTPQuest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterCarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterCarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterCarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View inflatedView;

    public RegisterCarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterCarFragment.
     *
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterCarFragment newInstance(String param1, String param2) {
        RegisterCarFragment fragment = new RegisterCarFragment();
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
        FragmentManager mgr = getActivity().getSupportFragmentManager();/// getChildFragmentManager();
    }

    static int TAKE_PIC = 1;
    Uri outPutfileUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;

    public static final String CAPTUREPICTURE_MESSAGE = "org.farmate.securify.CapturePIC";
    private FloatingActionButton TakePhoto;
    private FloatingActionButton UploadPhoto;
    private ImageView CarPhoto;
    private Button RegisterCarButton;

    // Form Elements
    private EditText carNickNameInput;
    private EditText registrationNumberInput;
    private EditText lastServiceInput;
    private EditText descriptionInput;

    private String carNickNameString;
    private String registrationNumberString;
    private String lastServiceString;
    private String descriptionString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflatedView = inflater.inflate(R.layout.fragment_register_car, container, false);
        TakePhoto = (FloatingActionButton) inflatedView.findViewById(R.id.floatingTakePhoto);
        UploadPhoto = (FloatingActionButton) inflatedView.findViewById(R.id.floatingUploadPhoto);
        CarPhoto = (ImageView) inflatedView.findViewById(R.id.ImageTakens);
        RegisterCarButton = (Button) inflatedView.findViewById((R.id.registerCarButton));

        // form elements
        carNickNameInput = (EditText) inflatedView.findViewById((R.id.inputCarNickName));
        registrationNumberInput = (EditText) inflatedView.findViewById((R.id.inputRegistrationNumber));
        lastServiceInput = (EditText) inflatedView.findViewById((R.id.inputLastService));
        descriptionInput = (EditText) inflatedView.findViewById((R.id.inputDescription));

        TakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CameraCaptureActivity.class);
                // email input will be passed on by default
                carNickNameString = carNickNameInput.getText().toString();
                intent.putExtra(CAPTUREPICTURE_MESSAGE, carNickNameString);
                startActivity(intent);
            }
        });

        RegisterCarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // check validity of an input
                    // data sanitation on the rego
                    // execute async http request to register the client
                    carNickNameString = carNickNameInput.getText().toString();
                    registrationNumberString = registrationNumberInput.getText().toString();
                    lastServiceString = lastServiceInput.getText().toString();
                    descriptionString = descriptionInput.getText().toString();

                    RegisterCarFragment.RegisterCarRequest asyncTask = new RegisterCarFragment.RegisterCarRequest();
                    //get all the fields for the form
                    asyncTask.execute(new String[] {getString(R.string.ServerURI)});
                }
        });

        return inflatedView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);
                CarPhoto.setImageBitmap(bitmap);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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


    // Async syntax AsyncTask <TypeOfVarArgParams, ProgressValue, ResultValue>
    private class RegisterCarRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // we use the OkHttp library from https://github.com/square/okhttp
            final String uri_target = new String(getString(R.string.ServerURI));
            final String page_target_salt = new String("carRegister.php?");
            // get password salt first generate 7 long random char

            // uri request builder
            Uri buildUrSalt = Uri.parse(uri_target + page_target_salt)
                    .buildUpon()
                    .appendQueryParameter("carNickName", carNickNameString)
                    .appendQueryParameter("registrationNumber", registrationNumberString)
                    .appendQueryParameter("lastService", lastServiceString)
                    .appendQueryParameter("description", descriptionString)
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
                return "Cannot Register";
            } else if (status_result == 1) {
                return "Registration Success";
            }
            return "";
        }

    }

}

