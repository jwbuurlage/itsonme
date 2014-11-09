package hack.goodnight.itsonme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;

class GroupListAdapter extends ArrayAdapter<Group>{
    private final Activity context;
    private final List<Group> web;

    public GroupListAdapter(Activity context,
                            List<Group> web) {
        super(context, R.layout.row_group_info, web);
        this.context = context;
        this.web = web;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_group_info, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        txtTitle.setText(web.get(position).name);

        GridView imageView = (GridView) rowView.findViewById(R.id.img);

        List<User> a = new ArrayList<User>();
        for(Participation p : web.get(position).participations) {
            a.add(p.user);
        }

        ProfileGridAdapter adapter = new ProfileGridAdapter((Activity)this.getContext(), a);
        imageView.setAdapter(adapter);

        /* if(position % 2 == 0) {
            rowView.setBackgroundColor(0xFFF0F0F0);
        } */

        return rowView;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }
}

class LoggedInServer
{
}

class GroupListChanged
{
    List<Group> list;
    GroupListChanged(List<Group> g) { list = g; }
}
class ReceivedCurrentGroup
{
    Group g;
    ReceivedCurrentGroup(Group _g){ g = _g; }
}
class EveningJoined
{
    Group g;
    EveningJoined(Group _g){ g = _g; }
}

public class LobbyActivity extends Activity {
    private static final String TAG = "ITSONME_LobbyActivity";

    private List<Group> groupList;
    //we request currentGroup and allGroups simultaneously. If we first receive currentGroup, and allGroups afterwards, then allGroups has to know that we already got a group
    boolean isInGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        EventBus.getDefault().register(this);
        ImageButton but = (ImageButton)findViewById(R.id.createGroupButton);
        but.bringToFront();
        but.setVisibility(View.GONE);
        findViewById(R.id.loadingBar).setVisibility(View.VISIBLE);
        onLaunch();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lobby, menu);
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
        }else if(id == R.id.action_logout){
            Intent intent;
            intent = new Intent(this, Login.class);
            intent.putExtra("logout", true);
            startActivity(intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createGroup(View v)
    {
        Intent intent;
        intent = new Intent(this, CreateGroupActivity.class);
        startActivity(intent);
    }

    public void onLaunch() {
        // - login at our server
        // - send google cloud message id
        // - request current group
        // - if in a group, go to evening activity
        // - if not, show group list (this activity)
        isInGroup = false;

        ServerInterface service = Root.getInstance().getService();
        service.login(Root.getInstance().auth_token, new retrofit.Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Root.getInstance().user = user;
                Log.i(TAG, "User info: " + user.first_name);
                EventBus.getDefault().post(new LoggedInServer());
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                findViewById(R.id.loadingBar).setVisibility(View.GONE);
                Log.e(TAG, "RetrofitError. TYPE:" + retrofitError.getKind() + " URL: " + retrofitError.getUrl());
            }
        });
    }

    public void onEventMainThread(LoggedInServer event) {
        ServerInterface service = Root.getInstance().getService();
        //Always send Google Cloud Messaging id to be sure
        if(!Root.getInstance().gcmRegId.isEmpty()) {
            service.sendPushToken(Root.getInstance().gcmRegId, new retrofit.Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    Log.i(TAG, "Sent GoogleCloudMessaging regid to server (existing registration).");
                }
                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.e(TAG, "RetrofitError. TYPE:" + retrofitError.getKind() + " URL: " + retrofitError.getUrl());
                }
            });
        }
        //Check if we are currently in a group
        service.getCurrentGroup(new retrofit.Callback<Group>() {
            @Override
            public void success(Group group, Response response) {
                EventBus.getDefault().post(new ReceivedCurrentGroup(group));
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "RetrofitError. TYPE:" + retrofitError.getKind() + " URL: " + retrofitError.getUrl());
            }
        });
        //Also get list of other groups
        service.getGroups(new retrofit.Callback<List<Group>>() {
            @Override
            public void success(List<Group> list, Response response) {
                EventBus.getDefault().post(new GroupListChanged(list));
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "RetrofitError. TYPE:" + retrofitError.getKind() + " URL: " + retrofitError.getUrl());
            }
        });
    }

    public void onEventMainThread(GroupListChanged event) {
        groupList = event.list;
        for(Group g : groupList)
            Log.i(TAG, "Grouplist entry: "+g.name);
        if(isInGroup == false) {
            makeTable();
        }
        findViewById(R.id.loadingBar).setVisibility(View.GONE);
        findViewById(R.id.createGroupButton).setVisibility(View.VISIBLE);
    }
    public void onEventMainThread(ReceivedCurrentGroup event) {
        if(event.g != null && event.g.name.isEmpty() == false){
            Log.i(TAG, "Received currentgroup: " + event.g.name);
            isInGroup = true;
            EventBus.getDefault().post(new EveningJoined(event.g));
            findViewById(R.id.loadingBar).setVisibility(View.GONE);
        }
    }

    public void onEventMainThread(EveningJoined event) {
        Root.getInstance().currentGroup = event.g;
        Intent intent = new Intent(this, EveningActivity.class);
        startActivity(intent);
        this.finish(); //when the user presses 'Back' at the evening screen, he should exit the app, not go to the lobby!!!
    }

    public void makeTable()
    {
        final ListView lv = (ListView)findViewById(R.id.list_view_groups);
        GroupListAdapter adapter = new GroupListAdapter(this, groupList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("onItemClick", "Reach test");

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                Group  group    = (Group) ((ListView)findViewById(R.id.list_view_groups)).getItemAtPosition(position);

                // Show Alert
                Log.i("LobbyClicked", "Position :" + itemPosition + "  ListItem : " + group.name);

                boolean alreadyInGroup = false;

                for(Participation part : group.participations)
                    if(part.user.id == Root.getInstance().user.id){
                        //already joined
                        EventBus.getDefault().post(new EveningJoined(group));
                        alreadyInGroup = true;
                    }

                if(!alreadyInGroup) {
                    ServerInterface service = Root.getInstance().getService();
                    service.joinGroup(group.id, new retrofit.Callback<Group>() {
                        @Override
                        public void success(Group group, Response response) {
                            Log.i(TAG, "Group joined. Server gave group.");
                            EventBus.getDefault().post(new EveningJoined(group));
                        }
                        @Override
                        public void failure(RetrofitError retrofitError) {
                            Log.e(TAG, "RetrofitError. TYPE:" + retrofitError.getKind() + " URL: " + retrofitError.getUrl());
                        }
                    });
                }
            }

        });

    }
}
