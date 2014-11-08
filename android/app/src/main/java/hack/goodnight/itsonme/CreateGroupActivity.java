package hack.goodnight.itsonme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;


public class CreateGroupActivity extends Activity {
    private static final String TAG = "CreateGroupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        TextView groupNameTextView = (TextView)findViewById(R.id.groupNameText);
        if(groupNameTextView.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
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

    public void startGroup(View v)
    {
        TextView groupTextView = (TextView)findViewById(R.id.groupNameText);
        String groupName = groupTextView.getText().toString();

        final Intent intent = new Intent(this, EveningActivity.class);

        ServerInterface service = Root.getInstance().getService();
        service.createGroup(groupName, new retrofit.Callback<Group>() {
            @Override
            public void success(Group group, Response response) {
                Log.i(TAG, "Server gave group.");
                startActivity(intent);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "RetrofitError: " + retrofitError.getKind());
                Log.e(TAG, "RetrofitError details: " + retrofitError.getUrl() + ", repsonse = " + retrofitError.getResponse());
            }
        });
    }
}
