package org.farmate.securifybeta.service;

/**
 * Created by Ananda on 23/09/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.farmate.securifybeta.activity.StartActivity;
import org.json.JSONException;
import org.json.JSONObject;

import org.farmate.securifybeta.activity.MainActivity;
import org.farmate.securifybeta.app.Config;
import org.farmate.securifybeta.util.NotificationUtils;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private final String ARG_PARAM_NAME = "Name_key";
    private final String ARG_ETA = "ETA_key";
    private final String ARG_PHONE = "Phone_Key";
    private final String ARG_LATI1 = "LATI_1_key";
    private final String ARG_LONG1 = "LONG_1_key";
    private final String ARG_LATI2 = "LATI_2_key";
    private final String ARG_LONG2 = "LONG_2_key";
    private final String IS_CONFIRMED = "CONFIRMATION_STATUS";
    private final String CLIENT_FIREBASE_ID = "CLIENT_FIREBASE";
    private final String CLIENT_USER_ID = "CLIENT_USERID";
    private final String TECHNI_FIREBASE_ID = "TECHNI_FIREBASE";
    private final String TECHNI_USER_ID = "TECHNI_USERID";

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            // get the payload parameter from the json.
            String Name_key = payload.getString(ARG_PARAM_NAME);
            String ETA_key = payload.getString(ARG_ETA);
            String Arg_phone = payload.getString(ARG_PHONE);

            // get the string payload
            String Client_Firebase_ID = payload.getString(CLIENT_FIREBASE_ID);
            String Client_User_ID = payload.getString(CLIENT_USER_ID);
            String Arg_lati1 = payload.getString(ARG_LATI1);
            String Arg_long1 = payload.getString(ARG_LONG1);
            // based on the confirmation
            String Is_Confirmed = payload.getString(IS_CONFIRMED);
            String Techni_Firebase_ID = "";
            String Techni_User_ID = "";
            String Arg_lati2 = "";
            String Arg_long2 = "";
            if(IS_CONFIRMED.equals("1")) {
                Techni_Firebase_ID = payload.getString(TECHNI_FIREBASE_ID);
                Techni_User_ID = payload.getString(TECHNI_USER_ID);
                Arg_lati2 = payload.getString(ARG_LATI2);
                Arg_long2 = payload.getString(ARG_LONG2);
            }

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);
            // payload specific

            Log.e(TAG, ARG_PARAM_NAME + Name_key);
            Log.e(TAG, ARG_ETA + ETA_key);
            Log.e(TAG, ARG_PHONE + Arg_phone);
            Log.e(TAG, ARG_LATI1 + Arg_lati1);
            Log.e(TAG, ARG_LONG1 + Arg_long1);
            Log.e(TAG, ARG_LATI2 + Arg_lati2);
            Log.e(TAG, ARG_LONG2 + Arg_long2);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);

                pushNotification.putExtra("message", message);
                pushNotification.putExtra("image", imageUrl);

                pushNotification.putExtra(ARG_PARAM_NAME , Name_key);
                pushNotification.putExtra(ARG_ETA , ETA_key);
                pushNotification.putExtra(ARG_PHONE , Arg_phone);

                // by default if is_confimed is null that means that the tecnifirebasae_ID will be zero
                pushNotification.putExtra(IS_CONFIRMED, Is_Confirmed);
                pushNotification.putExtra(CLIENT_FIREBASE_ID, Client_Firebase_ID);
                pushNotification.putExtra(CLIENT_USER_ID, Client_User_ID);
                pushNotification.putExtra(ARG_LATI1 , Arg_lati1);
                pushNotification.putExtra(ARG_LONG1 , Arg_long1);
                // argument on 2
                pushNotification.putExtra(TECHNI_FIREBASE_ID, Techni_Firebase_ID);
                pushNotification.putExtra(CLIENT_USER_ID, Techni_User_ID);
                pushNotification.putExtra(ARG_LATI2 , Arg_lati2);
                pushNotification.putExtra(ARG_LONG2 , Arg_long2);

                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), StartActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("image", imageUrl);

                resultIntent.putExtra(ARG_PARAM_NAME , Name_key);
                resultIntent.putExtra(ARG_ETA , ETA_key);
                resultIntent.putExtra(ARG_PHONE , Arg_phone);
                resultIntent.putExtra(ARG_LATI1 , Arg_lati1);
                resultIntent.putExtra(ARG_LONG1 , Arg_long1);
                resultIntent.putExtra(ARG_LATI2 , Arg_lati2);
                resultIntent.putExtra(ARG_LONG2 , Arg_long2);
                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}