package org.farmate.securifybeta.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.farmate.securifybeta.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String LOGIN_MESSAGE = "org.farmate.securify.LOGIN";
    public static final String REGISTER_MESSAGE = "org.farmate.securify.REGISTER";
    public static final String RESET_MESSAGE = "org.farmate.securify.RECOVER";

    private TextView emailField;
    private TextView passwordField;
    private Button loginButton;
    private Button registerButton;
    private Button resetButton;
    // http request trial
    private TextView httpResults;

    private String emailString;
    private String passwordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Securify");
        //getActionBar().setIcon(R.mipmap.ic_launcher);
        // define all objects by id
        emailField = (TextView) findViewById(R.id.inputCarNickName);
        // password field
        passwordField = (TextView) findViewById(R.id.inputPassword);
        // register button take to a next intent
        registerButton = (Button) findViewById(R.id.registerButton);
        // login button take to a next intent
        loginButton = (Button) findViewById(R.id.loginButton);
        // http result example
        // httpResults = (TextView) findViewById(R.id.httpResult);
        resetButton = (Button) findViewById(R.id.resetButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check whether the email and password has been filled
                // ERROR CHECK FOR UNFILLED FIELDS
                // get the most up to date from all inputs
                emailString = emailField.getText().toString();
                passwordString = passwordField.getText().toString();
                // perform async task
                LoginRequest asyncTask = new LoginRequest();
                asyncTask.execute(new String[] {getString(R.string.ServerURI)});
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                emailString = emailField.getText().toString();
                // email input will be passed on by default
                intent.putExtra(REGISTER_MESSAGE, emailString);
                startActivity(intent);
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this, ResetPassActivity.class);
                emailString = emailField.getText().toString();
                // email input will be passed on by default
                intent.putExtra(RESET_MESSAGE, emailString);
                startActivity(intent);
            }
        });
    }
        // Async syntax AsyncTask <TypeOfVarArgParams, ProgressValue, ResultValue>
        private class LoginRequest extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
                // we use the OkHttp library from https://github.com/square/okhttp
                final String uri_target= new String(getString(R.string.ServerURI));
                final String page_target_salt = new String("userLogin.php?");
                // get password salt first
                // uri request builder
                Uri buildUrSalt = Uri.parse(uri_target + page_target_salt)
                        .buildUpon()
                        .appendQueryParameter("email",emailString)
                        .build();
                String result = generalHTTPQuest(buildUrSalt.toString());
                String psaltResult = "";
                int status_result = -1;
                try {
                    JSONObject reader = new JSONObject(result);
                    status_result = reader.getInt("success");
                    psaltResult = reader.getString("pSalt");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status_result == -1) {
                    return "Cannot Connect To The Server";
                }
                else if(status_result == 0) {
                    return "Email Does Not Exist";
                }
                // debug only
                //return psaltResult;
                passwordString = sha1Encrypt(passwordString.concat(psaltResult));
                // debug only
                //return passwordString;
                Uri buildUrLogin = Uri.parse(uri_target + page_target_salt)
                        .buildUpon()
                        .appendQueryParameter("email",emailString)
                        .appendQueryParameter("password",passwordString)
                        .build();
                // uri request builder
                result = generalHTTPQuest(buildUrSalt.toString());
                psaltResult = "";
                status_result = -1;
                try {
                    JSONObject reader = new JSONObject(result);
                    status_result = reader.getInt("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status_result == -1) {
                    return "Cannot Connect To The Server";
                }
                else if(status_result == 0) {
                    return "Invalid Credentials";
                }
                else if(status_result == 1){
                    return "Login Success";
                }
                return "";
            }
            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(LoginActivity.this,result,Toast.LENGTH_LONG).show();
                // start new intent
                if(result.equals("Login Success")) {
                    Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                    emailString = emailField.getText().toString();
                    // email input will be passed on by default
                    intent.putExtra(LOGIN_MESSAGE, emailString);
                    startActivity(intent);
                    finish();
                }
            }
        }
        // sha 1 simple encryption process
        public static String sha1Encrypt(String passwordString)
        {
            try {
                // Create MD5 Hash
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.update(passwordString.getBytes());
                byte messageDigest[] = digest.digest();

                // Create Hex String
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < messageDigest.length; i++)
                    hexString.append(String.format("%02X", 0xFF & messageDigest[i]));
                return hexString.toString();

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        }

        public static String generalHTTPQuest(String uriFinal)
        {
            OkHttpClient client = new OkHttpClient();
            Request request =
                    new Request.Builder()
                            .url(uriFinal)
                            .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    try {
                        // get the psalt from request
                        // incoporated with json later maybe
                        return response.body().string();
                        // concatenate the stirng with the salt
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Not Connected To Internet";
        }
}

;



    // get the register button to assign to the next register activity

    // reset password activity too

    // get the login button to fetch something from the website which will take it
    // to the welcome activity.

