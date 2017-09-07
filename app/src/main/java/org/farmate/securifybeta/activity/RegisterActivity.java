package org.farmate.securifybeta.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.farmate.securifybeta.R;
import org.json.JSONException;
import org.json.JSONObject;

import static org.farmate.securifybeta.activity.LoginActivity.generalHTTPQuest;

public class RegisterActivity extends AppCompatActivity {
    public static final String REGISTER_MESSAGE = "org.farmate.securify.REGISTER";

    private TextView emailField;
    private TextView firstNameField;
    private TextView lastNameField;
    private Spinner roleSpinner;
    private TextView phoneNumberField;
    private TextView passMainField;
    private TextView passConfField;
    private Button registerButton;

    private String emailString;
    private String firstNameString;
    private String lastNameString;
    private String roleString;
    private String phoneNumberString;
    private String passwordString;
    private String confPasswordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.REGISTER_MESSAGE);
        // get each pointer to each part of the page
        emailField = (TextView) findViewById(R.id.inputemail);
        emailField.setText(message);

        emailField = (TextView) findViewById(R.id.inputemail);
        firstNameField = (TextView) findViewById(R.id.inputFirstName);
        lastNameField = (TextView) findViewById(R.id.inputLastName);

        // setup spinner
        roleSpinner = (Spinner) findViewById(R.id.spinnerRole);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.RoleDropdown, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        roleSpinner.setAdapter(adapter);

        phoneNumberField = (TextView) findViewById(R.id.inputPhone);
        passMainField = (TextView) findViewById(R.id.inputPassword);
        passConfField = (TextView) findViewById(R.id.inputConfirmPass);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emailString = emailField.getText().toString();
                firstNameString = firstNameField.getText().toString();
                lastNameString = lastNameField.getText().toString();
                roleString = roleSpinner.getSelectedItem().toString();
                phoneNumberString = phoneNumberField.getText().toString();
                passwordString = passMainField.getText().toString();
                confPasswordString = passConfField.getText().toString();
                // check if any of the field are empty
                if (!isValidEmail(emailString)) {
                    emailField.setError("Invalid Email Address");
                    Toast.makeText(RegisterActivity.this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                }
                else if(emailString.matches("") || firstNameString.matches("") || lastNameString.matches("") || roleString.matches("") || phoneNumberString.matches("") || passwordString.matches("") || confPasswordString.matches(""))
                {
                    Toast.makeText(RegisterActivity.this, "All Fields Must Be Field", Toast.LENGTH_LONG).show();
                }
                else if (!(passwordString.equals(confPasswordString))) {
                    Toast.makeText(RegisterActivity.this, "Password Fields Must Match", Toast.LENGTH_LONG).show();
                }
                else {
                    // execute async http request to register the client
                    RegisterActivity.RegisterRequest asyncTask = new RegisterActivity.RegisterRequest();
                    // get all the fields for the form
                    asyncTask.execute(new String[] {getString(R.string.ServerURI)});
                }

            }
        });

    }

    // Async syntax AsyncTask <TypeOfVarArgParams, ProgressValue, ResultValue>
    private class RegisterRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // we use the OkHttp library from https://github.com/square/okhttp
            final String uri_target = new String(getString(R.string.ServerURI));
            final String page_target_salt = new String("register.php?");
            // get password salt first generate 7 long random char

            // uri request builder
            Uri buildUrSalt = Uri.parse(uri_target + page_target_salt)
                    .buildUpon()
                    .appendQueryParameter("email", emailString)
                    .appendQueryParameter("firstName", firstNameString)
                    .appendQueryParameter("lastName", lastNameString)
                    .appendQueryParameter("role", roleString)
                    .appendQueryParameter("phoneNumber", phoneNumberString)
                    .appendQueryParameter("password", passwordString)
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

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_LONG).show();
            // start new intent
            if (result.equals("Registration Success")) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                emailString = emailField.getText().toString();
                // email input will be passed on by default
                intent.putExtra(REGISTER_MESSAGE, emailString);
                startActivity(intent);
                finish();
            }
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}