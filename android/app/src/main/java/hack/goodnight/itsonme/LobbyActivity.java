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

class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public ImageDownloader(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        mIcon = ImageHelper.getRoundedCornerBitmap(mIcon, mIcon.getHeight() / 2);
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}

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
    private Group currentGroupDummy; //only for debugging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        EventBus.getDefault().register(this);

        ServerInterface service = Root.getInstance().getService();
        service.getGroups(new retrofit.Callback<List<Group>>() {
            @Override
            public void success(List<Group> list, Response response) {
                EventBus.getDefault().post(new GroupListChanged(list));
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "RetrofitError: " + retrofitError.getKind());
                Log.e(TAG, "RetrofitError details: " + retrofitError.getUrl() + ", repsonse = " + retrofitError.getResponse());
            }
        });
        if(!Root.getInstance().gcmRegId.isEmpty()) {
            service.sendPushToken(Root.getInstance().gcmRegId, new retrofit.Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    Log.i(TAG, "Sent GoogleCloudMessaging regid to server (existing registration).");
                }
                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.e(TAG, "RetrofitError: " + retrofitError.getKind());
                    Log.e(TAG, "RetrofitError details: " + retrofitError.getUrl() + ", repsonse = " + retrofitError.getResponse());
                }
            });
        }
        service.getCurrentGroup(new retrofit.Callback<Group>() {
            @Override
            public void success(Group group, Response response) {
                EventBus.getDefault().post(new ReceivedCurrentGroup(group));
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "RetrofitError: " + retrofitError.getKind());
                Log.e(TAG, "RetrofitError details: " + retrofitError.getUrl() + ", repsonse = " + retrofitError.getResponse());
            }
        });

        ImageButton but = (ImageButton)findViewById(R.id.createGroupButton);
        but.bringToFront();
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void createGroup(View v)
    {
        Intent intent;
        intent = new Intent(this, CreateGroupActivity.class);
        startActivity(intent);
    }
    public void onEventMainThread(GroupListChanged event) {
        groupList = event.list;
        Root.getInstance().groupList = event.list;
        for(Group g : groupList)
            Log.i(TAG, "Grouplist entry: "+g.name);
        if(currentGroupDummy != null) groupList.add(currentGroupDummy);
        makeTable();
    }
    public void onEventMainThread(ReceivedCurrentGroup event) {
        Log.i(TAG, "Received currentgroup: "+event.g.name);
        currentGroupDummy = event.g;
        if(groupList != null) {
            groupList.add(event.g);
            makeTable();
            Log.i(TAG,"Table should be refreshed.");
        }
    }

    public void onEventMainThread(EveningJoined event) {
        Root.getInstance().currentGroup = event.g;
        Intent intent = new Intent(this, EveningActivity.class);
        startActivity(intent);
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
                    if(part.user.id == Root.getInstance().getUser().id){
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
                            Log.e(TAG, "RetrofitError: " + retrofitError.getKind());
                            Log.e(TAG, "RetrofitError details: " + retrofitError.getUrl() + ", repsonse = " + retrofitError.getResponse());
                        }
                    });
                }
            }

        });

    }
}
