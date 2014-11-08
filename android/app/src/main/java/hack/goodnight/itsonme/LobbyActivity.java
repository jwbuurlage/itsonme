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
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;



class GroupListAdapter extends ArrayAdapter<Group>{
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
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private final Activity context;
    private final List<Group> web;

    public GroupListAdapter(Activity context,
                            List<Group> web) {
        super(context, R.layout.row_group_info, web);
        this.context = context;
        this.web = web;
    }

    private static Drawable LoadImageFromWeb(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_group_info, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web.get(position).name);

        new ImageDownloader(imageView).execute(web.get(position).participations.get(0).user.facebook_avatar_url);

        /* if(position % 2 == 0) {
            rowView.setBackgroundColor(0xFFF0F0F0);
        } */

        return rowView;
    }
}

public class LobbyActivity extends Activity {
    private static final String TAG = "LobbyActivity";


    private List<Group> groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        ServerInterface service = Root.getInstance().getService();
        service.getGroups(new retrofit.Callback<List<Group>>() {
            @Override
            public void success(List<Group> list, Response response) {
                Log.i(TAG, "Server gave grouplist.");
                groupList = list;
                for(Group group : groupList) {
                    Log.i(TAG, "Group received: "+group.name);
                }
                makeTable();
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
                Group  itemValue    = (Group) ((ListView)findViewById(R.id.list_view_groups)).getItemAtPosition(position);

                // Show Alert
                Log.i("LobbyClicked", "Position :"+itemPosition+"  ListItem : " +itemValue.name);

            }

        });

        // make grid with profile pictures as test
    }
}
