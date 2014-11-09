package hack.goodnight.itsonme;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
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

        isDrinking = true;
        isReady = false;

        setTitle(Root.getInstance().currentGroup.name);

        GridView gridView = (GridView)findViewById(R.id.gridView);

        List<String> a = new ArrayList<String>();
        for(Participation p : Root.getInstance().currentGroup.participations) {
            a.add(p.user.facebook_avatar_url);
            Log.i("Found user: ", a.get(a.size() - 1));
        }

        ProfileGridAdapter adapter = new ProfileGridAdapter(this, a);
        gridView.setAdapter(adapter);
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

    public void noMoreAlcoholClicked(View view) {
        //get toggle value from ui element
        isDrinking = true;
        updateGroup();
    }

    public void iAmReadyClicked(View view) {
        isReady = true;
        updateGroup();
    }

    public void updateGroup() {
        ServerInterface service = Root.getInstance().getService();

        int partId = -1;
        for(Participation part : Root.getInstance().currentGroup.participations){
            if( part.user.id == Root.getInstance().getUser().id ){
                partId = part.id;
                break;
            }
        }

        service.updateParticipation(partId, isDrinking, isReady, new retrofit.Callback<Group>() {
            @Override
            public void success(Group updatedGroup, Response response) {
                Log.i(TAG, "group updated.");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "RetrofitError: " + retrofitError.getKind());
                Log.e(TAG, "RetrofitError details: " + retrofitError.getUrl() + ", response = " + retrofitError.getResponse());
            }
        });
    }
}
