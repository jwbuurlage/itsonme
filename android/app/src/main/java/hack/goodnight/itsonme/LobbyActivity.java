package hack.goodnight.itsonme;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

        TableLayout groups = (TableLayout)findViewById(R.id.table_groups);
        groups.setStretchAllColumns(true);
        groups.bringToFront();
        for(int i = 0; i < data.length; i++){
            TableRow tr =  new TableRow(this);

            TextView c1 = new TextView(this);
            c1.setText(data[i].name);

            TextView c2 = new TextView(this);
            c2.setText(String.valueOf(data[i].x));

            TextView c3 = new TextView(this);
            c3.setText(String.valueOf(data[i].y));

            tr.addView(c1);
            tr.addView(c2);
            tr.addView(c3);

            groups.addView(tr);
        }

        setContentView(R.layout.activity_lobby);
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
}
