package hack.goodnight.itsonme;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;

class LeftGroup{
}
class GroupUpdate
{
    Group g;
    GroupUpdate(Group _g){ g = _g; }
}

public class EveningActivity extends Activity {
    private static final String TAG = "ITSONME_EveningActivity";

    private boolean isDrinking;
    private boolean isReady;

    Handler timerHandler = new Handler();

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if( Root.getInstance().currentGroup != null && !Root.getInstance().currentGroup.rounds.isEmpty() ) {
                List<Round> rounds = Root.getInstance().currentGroup.rounds;
                setLabels(rounds.get(rounds.size()-1));
            }
            timerHandler.postDelayed(this, 30000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evening);

        Log.i(TAG, "onCreate!!!");

        Intent intent = getIntent();
        if(intent.getBooleanExtra("notification", false)){
            Log.e(TAG, "Received notification at onCreate!!");
            NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
            if( Root.getInstance().currentGroup == null || Root.getInstance().currentGroup.id == 0){
                Intent intent2 = new Intent(this, Login.class);
                startActivity(intent2);
                this.finish();
                return;
            }
        }

        findViewById(R.id.statusPanel).setVisibility(View.GONE);
        isDrinking = true;
        ToggleButton btn = (ToggleButton)findViewById(R.id.toggleButton);
        btn.setChecked(isDrinking);

        EventBus.getDefault().register(this);

        setTitle(Root.getInstance().currentGroup.name);

        GridView gridView = (GridView)findViewById(R.id.gridView);
        List<User> a = new ArrayList<User>();
        for(Participation p : Root.getInstance().currentGroup.participations) {
            a.add(p.user);
        }
        ProfileGridAdapter adapter = new ProfileGridAdapter(this, a);
        gridView.setAdapter(adapter);

        timerHandler.postDelayed(timerRunnable, 0);

        EventBus.getDefault().post(new GroupUpdate(Root.getInstance().currentGroup));
        updateGroup();
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        if(intent.getBooleanExtra("notification", false)){
            Log.e(TAG, "Received notification at onNewIntent!!");
            NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
            if( Root.getInstance().currentGroup == null || Root.getInstance().currentGroup.id == 0){
                Intent intent2 = new Intent(this, Login.class);
                startActivity(intent2);
                this.finish();
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        if(id == R.id.action_leave) {
            leaveGroup();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void noMoreAlcoholClicked(View view) {
        ToggleButton btn = (ToggleButton)findViewById(R.id.toggleButton);
        isDrinking = btn.isChecked();
        updateGroup();
    }


    public void iAmReadyClicked(View view) {
        if(!isReady) {
            Log.i(TAG, "I Am Ready Clicked");
            isReady = true;
            updateGroup();
            findViewById(R.id.readyButton).setAlpha(1.0f);
        }
    }

    public void updateGroup() {
        int partId = -1;
        for(Participation part : Root.getInstance().currentGroup.participations){
            if( part.user.id == Root.getInstance().user.id ){
                partId = part.id;
                break;
            }
        }
        Log.i(TAG, "Updating participation "+partId);

        ServerInterface service = Root.getInstance().getService();
        service.updateParticipation(partId, isDrinking, isReady, new retrofit.Callback<Group>() {
            @Override
            public void success(Group updatedGroup, Response response) {
                Root.getInstance().currentGroup = updatedGroup;
                Log.i(TAG, "readystatus update sent succesfully. updated group received.");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "RetrofitError: " + retrofitError.getKind());
                Log.e(TAG, "RetrofitError details: " + retrofitError.getUrl() + ", response = " + retrofitError.getResponse());
            }
        });
    }

    public void onEventMainThread(LeftGroup event) {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onEventMainThread(PushMessage event) {
        //request update from server
        //update UI on response
        ServerInterface service = Root.getInstance().getService();
        service.getCurrentGroup(new retrofit.Callback<Group>() {
            @Override
            public void success(Group group, Response response) {
                EventBus.getDefault().post(new GroupUpdate(group));
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "RetrofitError. TYPE:" + retrofitError.getKind() + " URL: " + retrofitError.getUrl());
            }
        });
    }

    public void setLabels(Round round){
        findViewById(R.id.statusPanel).setVisibility(View.VISIBLE);

        TextView buyerText = (TextView) findViewById(R.id.roundTextView);
        buyerText.setText(round.buyer.first_name+"'s Round");

        long diff = (new Date()).getTime()- round.created_at.date.getTime();
        TextView timerText = (TextView) findViewById(R.id.timeTextView);

        int minutes=(int)Math.floor(diff/(60000));
        String moment;
        if(minutes == 0) {
            moment = "just now";
        } else if(minutes == 1) {
            moment = "one minute ago";
        } else if(minutes < 14) {
            moment = minutes+" minutes ago";
        } else if(minutes < 23) {
            moment = "a quarter of an hour ago";
        } else if(minutes < 50) {
            moment = "half an hour ago";
        } else {
            int hours=(int)Math.floor((minutes+15)/60);
            if(hours == 1)
                moment = "one hour ago";
            else
                moment = hours+" hour ago";
            if(hours > 24*365*1000000000*5)
                moment = "You are from the future!";
        }

        timerText.setText(moment);

        TextView AclText = (TextView) findViewById(R.id.nAlcTextView);
        TextView NAclText = (TextView) findViewById(R.id.nNAlcTextView);
        AclText.setText(""+round.n_alcoholic);
        NAclText.setText(""+round.n_non_alcoholic);
    }

    public void onEventMainThread(GroupUpdate event) {
        Group g = event.g;
        Root.getInstance().currentGroup = g;

        isReady = g.my_participation.is_ready;
        if(!isReady) findViewById(R.id.readyButton).setAlpha(0.5f);

        GridView gridView = (GridView)findViewById(R.id.gridView);
        List<User> a = new ArrayList<User>();
        for(Participation p : Root.getInstance().currentGroup.participations) {
            a.add(p.user);
        }
        ProfileGridAdapter adapter = new ProfileGridAdapter(this, a);
        gridView.setAdapter(adapter);

        //set round information
        if(!g.rounds.isEmpty()) {
            Round round = g.rounds.get(g.rounds.size()-1);
            setLabels(round);
        }else
            findViewById(R.id.statusPanel).setVisibility(View.GONE);
    }

    public void leaveGroup() {
        int partId = -1;
        for(Participation part : Root.getInstance().currentGroup.participations){
            if( part.user.id == Root.getInstance().user.id ){
                partId = part.id;
                break;
            }
        }
        Log.i(TAG, "Leaving group. Participation id "+partId);

        ServerInterface service = Root.getInstance().getService();
        service.leaveGroup(partId, new retrofit.Callback<Group>() {
            @Override
            public void success(Group updatedGroup, Response response) {
                Root.getInstance().currentGroup = null;
                EventBus.getDefault().post(new LeftGroup());
                Log.i(TAG, "Successfully left group.");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "RetrofitError. TYPE:" + retrofitError.getKind() + " URL: " + retrofitError.getUrl());
            }
        });
    }
}
