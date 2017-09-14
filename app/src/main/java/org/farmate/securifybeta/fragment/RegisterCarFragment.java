package org.farmate.securifybeta.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import org.farmate.securifybeta.R;
import org.farmate.securifybeta.activity.CameraCaptureActivity;
import org.farmate.securifybeta.activity.LoginActivity;
import org.farmate.securifybeta.activity.RegisterActivity;
import org.farmate.securifybeta.activity.StartActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.data;
import static android.R.attr.start;
import static android.app.Activity.RESULT_OK;
import static org.farmate.securifybeta.activity.LoginActivity.generalHTTPQuest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterCarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterCarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class RegisterCarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int someVarA;
    private String someVarB;

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
    private static final int ACTIVITY_START_CAMERA_APP = 1;
    // custom permission vector
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_W_EXTERNAL_STORAGE = 2;
    private static final String FRAGMENT_TAG = "org.farmate.securifybeta";

    // Form Elements
    private EditText carNickNameInput;
    private EditText registrationNumberInput;
    private EditText lastServiceInput;
    private EditText descriptionInput;

    private String carNickNameString;
    private String registrationNumberString;
    private String lastServiceString;
    private String descriptionString;

    Calendar myCalendar = Calendar.getInstance();
    String dateFormat = "dd.MM.yyyy";
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);

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
        // disable keyboard input
        if (Build.VERSION.SDK_INT >= 11)
        {
            lastServiceInput.setRawInputType(InputType.TYPE_CLASS_TEXT);
            lastServiceInput.setTextIsSelectable(true);
        }
        else{
            lastServiceInput.setRawInputType(InputType.TYPE_NULL);
            lastServiceInput.setFocusable(true);
        }
        // init - set date to current date
        long currentdate = System.currentTimeMillis();
        String dateString = sdf.format(currentdate);
        lastServiceInput.setText(dateString);
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                lastServiceInput.setText(sdf.format(myCalendar.getTime()));
            }
        };


        lastServiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        descriptionInput = (EditText) inflatedView.findViewById((R.id.inputDescription));

        TakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check permission for CAMERA
                if(Build.VERSION.SDK_INT >= 23)
                {
                    if((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                        // Check Permissions Now
                        // Callback onRequestPermissionsResult interceptado na Activity MainActivity
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    // Check permission for Internal Storage
                    if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_W_EXTERNAL_STORAGE);
                    }
                    else
                    {
                        startCamera();
                    }
                }
                else
                {
                    startCamera();
                }
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

    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            // send to the intent on the picture to be broadcast on the gallery
            mediaScanIntent.setData(contentUri);
            getActivity().sendBroadcast(mediaScanIntent);
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentUri);
                CarPhoto.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_W_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    String mCurrentPhotoPath;
    File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    void setReducedImageSize() {
        int targetImageViewWidth = CarPhoto.getWidth();
        int targetImageViewHeight = CarPhoto.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap photoReducedSizeBitmp = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        CarPhoto.setImageBitmap(photoReducedSizeBitmp);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void startCamera()
    {
        // permission has been granted, continue as usual
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (callCameraApplicationIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
            String authorities = getActivity().getApplicationContext().getPackageName() + ".fileprovider";
            Uri imageUri = FileProvider.getUriForFile(getActivity(), authorities, photoFile);
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
        }
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

