package hack.goodnight.itsonme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/* class ProfileImageGridView extends GridView
{

} */

class TestData extends Object {
    public int x;
    public int y;
    public String name;

    public TestData(int _x, int _y, String _name)
    {
        x = _x;
        y = _y;
        name = _name;
    }
}

public class LobbyActivity extends Activity {
    private TestData[] data = {
            new TestData(1, 2, "Zuipen op maandag."),
            new TestData(1, 2, "Zuipen op dinsdag."),
            new TestData(1, 2, "Zuipen op woensdag."),
            new TestData(1, 2, "Balansdag (zuipen)"),
            new TestData(1, 2, "Zuipen op vrijdag.")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        TableLayout groups = (TableLayout)findViewById(R.id.table_groups);
        groups.setStretchAllColumns(true);
        groups.bringToFront();
        for(int i = 0; i < data.length; i++){
            TableRow tr =  new TableRow(this);
            tr.setPadding(20, 20, 20, 20);

            TextView c1 = new TextView(this);
            c1.setText(data[i].name);
            c1.setTextSize(20);

            TextView c2 = new TextView(this);
            c2.setText(String.valueOf(data[i].x));

            TextView c3 = new TextView(this);
            c3.setText(String.valueOf(data[i].y));

            tr.addView(c1);
            tr.addView(c2);
            tr.addView(c3);

            if(i % 2 == 0) {
                tr.setBackgroundColor(0xFFEEEEEE);
            }
            groups.addView(tr);
        }


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
}
