package hack.goodnight.itsonme;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.util.Log;
import java.util.List;

import com.facebook.AppEventsLogger;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        //TODO Do this at a global point
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://private-anon-265b71629-itsonme.apiary-mock.com").build();
        ServerInterface service = restAdapter.create(ServerInterface.class);

        service.getGroup(new retrofit.Callback<Group>(){
            @Override
            public void success(Group group, Response response) {
                TextView mylabel = (TextView) findViewById(R.id.textViewMiddle);
                mylabel.setText("Sweet succes!...");
                Log.d("ITSONMETAG","Group info: "+group.name+" and "+group.id);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                TextView mylabel = (TextView) findViewById(R.id.textViewMiddle);
                mylabel.setText("ERROR :(");
                Log.d("ITSONMETAG", "for fucks sake. error = "+ retrofitError.getKind());
                Log.d("ITSONMETAG", "for fucks sake. other stuff = "+ retrofitError.getUrl() + ", repsonse = " + retrofitError.getResponse());
            }
        });
        TextView mylabel = (TextView) findViewById(R.id.textViewMiddle);
        mylabel.setText("Loading...");
    }
}
