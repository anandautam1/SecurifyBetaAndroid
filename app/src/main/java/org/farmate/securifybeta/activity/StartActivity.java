package org.farmate.securifybeta.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;

import org.farmate.securifybeta.R;
import org.farmate.securifybeta.app.Config;
import org.farmate.securifybeta.fragment.HomeFragment;
import org.farmate.securifybeta.fragment.MonitorCarFragment;
import org.farmate.securifybeta.fragment.MonitorTechFragment;
import org.farmate.securifybeta.fragment.RequestTechFragment;
import org.farmate.securifybeta.fragment.SettingsFragment;

// Final Fragment
import org.farmate.securifybeta.fragment.RegisterCarFragment;

import org.farmate.securifybeta.fragment.dialogRequestFragment;
import org.farmate.securifybeta.other.CircleTransform;
import org.farmate.securifybeta.database.usersLocal;
import org.farmate.securifybeta.database.securifyUserDatabaseHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.farmate.securifybeta.activity.LoginActivity.generalHTTPQuest;

// sliding menu using navigation drawer
// https://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/

public class StartActivity extends AppCompatActivity implements
        HomeFragment.OnFragmentInteractionListener,
        MonitorTechFragment.OnFragmentInteractionListener,
        MonitorCarFragment.OnFragmentInteractionListener,
        RegisterCarFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        dialogRequestFragment.OnFragmentInteractionListener
{
        // new fragment for the next activity
        // get preferences based on the logged in database

        public static final String LOGIN_MESSAGE = "org.farmate.securify.LOGIN";

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;

    private ProgressDialog progressDialog;

    // urls to load navigation header background image
    private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
    // image of the USER
    private static final String urlProfileImg = "https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAA2GAAAAJDNkODVhMDI2LTYyYzYtNGFiZC1hYjJlLTNkMmE4MDI0NDk1MQ.jpg";
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
        private static final String TAG_MONITOR_TECH = "monitor tech";
        private static final String TAG_MONITOR_CAR = "monitor car";
        private static final String TAG_REGISTER_CAR = "register";
        private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

        private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION =2 ;
        private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;

        private securifyUserDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // new instance of the database and the ONLY INSTANCE
        db = new securifyUserDatabaseHelper(getApplicationContext());
        // synchronize with the server
        StoreJSonDataInToSQLiteClass asyncTask = new StoreJSonDataInToSQLiteClass(getApplicationContext());
        asyncTask.execute(new String[] {getString(R.string.ServerURI)});

        setContentView(R.layout.activity_start);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // check for permission for any compalation above android N.
        if (Build.VERSION.SDK_INT >=23) {
            // permission for location
            if ((ActivityCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                // Check Permissions Now
                // Callback onRequestPermissionsResult interceptado na Activity MainActivity
                ActivityCompat.requestPermissions(StartActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            if ((ActivityCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                // Check Permissions Now
                // Callback onRequestPermissionsResult interceptado na Activity MainActivity
                ActivityCompat.requestPermissions(StartActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            }
            if ((ActivityCompat.checkSelfPermission(StartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                // Check Permissions Now
                // Callback onRequestPermissionsResult interceptado na Activity MainActivity
                ActivityCompat.requestPermissions(StartActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        // load nav menu header data
        loadNavHeader();
        // initializing navigation menu
        setUpNavigationView();
        if (savedInstanceState == null) {
            // store the value of the user email
            // store all the user unique id
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
            }
        }
        // handle all the permissions on the access
        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Toast.makeText(getApplicationContext(), "Location Permission granted", Toast.LENGTH_SHORT).show();
                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(getApplicationContext(), "Location Permission denied", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                case MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Toast.makeText(getApplicationContext(), "Location Permission granted", Toast.LENGTH_SHORT).show();
                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(getApplicationContext(), "Location Permission denied", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Toast.makeText(getApplicationContext(), "Ext Storage Permission granted", Toast.LENGTH_SHORT).show();
                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(getApplicationContext(), "Ext Storag Permission denied", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request
            }
        }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    // USER NAME DETAIL
    private void loadNavHeader() {
        // name, website
        txtName.setText("Ananda Utama");
        txtWebsite.setText("Manager");
        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);
        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        // set toolbar title
        setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // monitor technician
                MonitorTechFragment monitorTechFragment = new MonitorTechFragment();
                return monitorTechFragment;
            case 2:
                // monitor car
                MonitorCarFragment monitorCarFragment = new MonitorCarFragment();
                return monitorCarFragment;
            case 3:
                RegisterCarFragment registerCarFragment = new RegisterCarFragment();
                return registerCarFragment;
            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_monitor_tech:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_MONITOR_TECH;
                        break;
                    case R.id.nav_monitor_car:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MONITOR_CAR;
                        break;
                    case R.id.nav_register_car:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_REGISTER_CAR;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_logout:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(StartActivity.this, LoginActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }
        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }
        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

        private class StoreJSonDataInToSQLiteClass extends AsyncTask<String, Void, String> {

            public Context context;

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
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(StartActivity.this);
                progressDialog.setTitle("LOADING");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
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

                    Bundle bundleFromPrev = getIntent().getExtras();

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
                            db.addUser(userLocal);
                        }
                        status_result = 1;
                        db.closeDB();
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
                progressDialog.dismiss();
                // update preference based on the passed email address
                // inside fragment thus
                if (result.equals("Database Synchronized")) {

                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    //SharedPreferences sharedPref = getSharedPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    // get the email string passed to the start activity
                    Bundle bundleFromPrev = getIntent().getExtras();
                    // app wouldnt crash if the
                    String emailFromApp = bundleFromPrev.getString(LOGIN_MESSAGE, "");
                    // get USER ID based on the input email
                    if (!emailFromApp.equals("")) {
                        List<usersLocal> userList = new ArrayList<usersLocal>();
                        userList = db.getUserOnEmail(emailFromApp);
                        for (int i = 0; i < userList.size(); i++) {

                            usersLocal user = userList.get(i);
                            String userIdFromDB = String.valueOf(user.getUserID());
                            String fNameFromDB = user.getFname();
                            String lNameFromDB = user.getLname();
                            String phoneFromDB = user.getPhone();
                            String passHashedFromDB = user.getPass_hashed();
                            String roleFromDB = user.getRole();

                            editor.putString(getString(R.string.userEmailLoggedIn), emailFromApp);
                            //editor.commit();
                            editor.putString(getString(R.string.userIdLoggedIn), userIdFromDB);
                            //editor.commit();
                            editor.putString(getString(R.string.userFnameLoggedIn), fNameFromDB);
                            //editor.commit();
                            editor.putString(getString(R.string.userLnameLoggedIn), lNameFromDB);
                            //editor.commit();
                            editor.putString(getString(R.string.phoneLoggedIn), phoneFromDB);
                            //editor.commit();
                            editor.putString(getString(R.string.passHashedLoggedIn), passHashedFromDB);
                            //editor.commit();
                            editor.putString(getString(R.string.passRoleLoggedIn), roleFromDB);
                            editor.apply();
                        }
                    }
                    Toast.makeText(StartActivity.this, result, Toast.LENGTH_LONG).show();
                    loadHomeFragment();
                }
            }
        }

    // initialize the local sqlite database

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=");
        return urlString.toString();
    }
}

