package hack.goodnight.itsonme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AppEventsLogger;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class Login extends FragmentActivity {
    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    public void myEventFunction(View view) {
        ServerInterface service = Root.getInstance().getService();
        service.login("facebook_access_token="+ Root.getInstance().getAuth(), new retrofit.Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Log.d("ITSONMETAG","User info: "+user.first_name);
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("ITSONMETAG", "RetrofitError: "+ retrofitError.getKind());
                Log.e("ITSONMETAG", "RetrofitError details: " + retrofitError.getUrl() + ", repsonse = " + retrofitError.getResponse());
            }
        });
    }

    public void openLobby(View v)
    {
        Intent intent;
        intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }
}
