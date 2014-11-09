package hack.goodnight.itsonme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class Login extends FragmentActivity {
    private static final String TAG = "ITSONME_LoginActivity";
    private LoginFragment loginFragment;

    String SENDER_ID = "697538693334"; //ItsOnMe unique code obtained from google account

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent.getBooleanExtra("logout", false)){
            Log.i(TAG,"facebook logout intent");
            Session.getActiveSession().closeAndClearTokenInformation();
        }

        if (savedInstanceState == null) {
        // Add the fragment on initial activity setup
            loginFragment = new LoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, loginFragment)
                    .commit();
        } else {
        // Or set the fragment from restored state info
            loginFragment = (LoginFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
        //setContentView(R.layout.activity_login);

        lobbyOpened = false;
        Log.i(TAG, "onCreate!!!");

        context = getApplicationContext();
        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);
        Root.getInstance().gcmRegId = regid; //also on empty string
        if (regid.isEmpty()) registerInBackground();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume!!!");
        lobbyOpened = false;

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(TAG, "onPause!!!");

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    private boolean lobbyOpened = false;
    public void openLobby()
    {
        if(!lobbyOpened) {
            lobbyOpened = true;
            Intent intent = new Intent(this, LobbyActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    // Everything below is copied from
    // http://developer.android.com/google/gcm/client.html

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(Login.class.getSimpleName(), Context.MODE_PRIVATE);
    }
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    sendRegistrationIdToBackend();

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        Root.getInstance().gcmRegId = regid;

        if( Root.getInstance().loggedInFacebook ){
            ServerInterface service = Root.getInstance().getService();
            service.sendPushToken(regid, new retrofit.Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    Root.getInstance().user = user;
                    Log.i(TAG, "Sent GoogleCloudMessaging regid to server (new registration).");
                }
                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.e(TAG, "RetrofitError: " + retrofitError.getKind());
                    Log.e(TAG, "RetrofitError details: " + retrofitError.getUrl() + ", repsonse = " + retrofitError.getResponse());
                }
            });
        }
    }

}
