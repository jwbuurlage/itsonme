package hack.goodnight.itsonme;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class

public class LobbyActivity extends Activity {
    private TestData[] Data = {
            new TestData

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TableLayout prices = (TableLayout)findViewById(R.id.table_groups);
        prices.setStretchAllColumns(true);
        prices.bringToFront();
        for(int i = 0; i < drug.length; i++){
            TableRow tr =  new TableRow(this);
            TextView c1 = new TextView(this);
            c1.setText(drug[i].getName());
            TextView c2 = new TextView(this);
            c2.setText(String.valueOf(drug[i].getPrice()));
            TextView c3 = new TextView(this);
            c3.setText(String.valueOf(drug[i].getAmount()));
            tr.addView(c1);
            tr.addView(c2);
            tr.addView(c3);
            prices.addView(tr);
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
