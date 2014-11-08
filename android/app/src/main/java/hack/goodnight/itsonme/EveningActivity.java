package hack.goodnight.itsonme;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;


public class EveningActivity extends Activity {
    private static final String TAG = "EveningActivity";

    private boolean isDrinking;
    private boolean isReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evening);

        isDrinking = getrekt();
        isReady = false;
    
        ListView lv =  new ListView(this);

        RelativeLayout myLayout = (RelativeLayout)findViewById(R.id.rel_layout_evening);
        //ArrayAdapter and shit, get profile pictures
        myLayout.addView(lv);

        TextView c1 = new TextView(this);
        c1.setText("John's round");
        c1.setTextSize(20);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evening, menu);
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

    public boolean getrekt() {
        return true;
    }

    public void noMoreAlcoholClicked(View view) {
        //get toggle value from ui element
        isDrinking = getrekt();
        updateGroup();
    }

    public void iAmReadyClicked(View view) {
        isReady = true;
        updateGroup();
    }

    public void updateGroup() {
        ServerInterface service = Root.getInstance().getService();
        service.updateParticipation(Root.getInstance().currentGroup.id, isDrinking, isReady, new retrofit.Callback<Group>() {
            @Override
            public void success(Group updatedGroup, Response response) {
                Log.i(TAG, "group updated.");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "RetrofitError: " + retrofitError.getKind());
                Log.e(TAG, "RetrofitError details: " + retrofitError.getUrl() + ", repsonse = " + retrofitError.getResponse());
            }
        });
    }
}
