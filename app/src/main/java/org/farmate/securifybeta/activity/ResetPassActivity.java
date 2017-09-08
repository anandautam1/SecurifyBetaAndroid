package org.farmate.securifybeta.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.farmate.securifybeta.R;
import org.json.JSONException;
import org.json.JSONObject;

import static org.farmate.securifybeta.activity.LoginActivity.generalHTTPQuest;

public class ResetPassActivity extends AppCompatActivity {
    public static final String RESET_MESSAGE = "org.farmate.securify.RECOVER";

    private TextView emailField;
    private TextView passMainField;
    private TextView passConfField;
    private Button resetButton;

    private String emailString;
    private String passwordString;
    private String confPasswordString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.REGISTER_MESSAGE);

        emailField = (TextView) findViewById(R.id.inputCarNickName);
        emailField.setText(message);

        passMainField = (TextView) findViewById(R.id.inputPassword);

        passConfField = (TextView) findViewById(R.id.inputConfirmPass);

        resetButton = (Button) findViewById(R.id.resetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emailString = emailField.getText().toString();
                passwordString = passMainField.getText().toString();
                confPasswordString = passConfField.getText().toString();
                // check if any of the field are empty
                if (!ResetPassActivity.isValidEmail(emailString)) {
                    emailField.setError("Invalid Email Address");
                    Toast.makeText(ResetPassActivity.this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                }
                else if(emailString.matches("") || passwordString.matches("") || confPasswordString.matches(""))
                {
                    Toast.makeText(ResetPassActivity.this, "All Fields Must Be Field", Toast.LENGTH_LONG).show();
                }
                else if (!(passwordString.equals(confPasswordString))) {
                    Toast.makeText(ResetPassActivity.this, "Password Fields Must Match", Toast.LENGTH_LONG).show();
                }
                else {
                    // execute async http request to register the client
                    ResetPassActivity.ResetRequest asyncTask = new ResetPassActivity.ResetRequest();
                    // get all the fields for the form
                    asyncTask.execute(new String[] {getString(R.string.ServerURI)});
                }
            }
        });
    }

    // Async syntax AsyncTask <TypeOfVarArgParams, ProgressValue, ResultValue>
    private class ResetRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // we use the OkHttp library from https://github.com/square/okhttp
            final String uri_target = new String(getString(R.string.ServerURI));
            final String page_target_salt = new String("userReset.php?");
            // get password salt first generate 7 long random char

            // uri request builder
            Uri buildUrSalt = Uri.parse(uri_target + page_target_salt)
                    .buildUpon()
                    .appendQueryParameter("email", emailString)
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
                return "Cannot Reset";
            } else if (status_result == 1) {
                return "Password Reset Success";
            }

            return "";
            }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(ResetPassActivity.this, result, Toast.LENGTH_LONG).show();
            // start new intent
            if (result.equals("Password Reset Success")) {
                Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
                emailString = emailField.getText().toString();
                // email input will be passed on by default
                intent.putExtra(RESET_MESSAGE, emailString);
                startActivity(intent);
                finish();
            }
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
