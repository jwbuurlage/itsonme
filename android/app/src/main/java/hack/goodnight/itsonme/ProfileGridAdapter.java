package hack.goodnight.itsonme;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by jw on 8-11-14.
 */
class ProfileGridAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final List<String> web;

    public ProfileGridAdapter(Activity context,
                              List<String> web) {
        super(context, R.layout.profileimage, web);
        this.context = context;
        this.web = web;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.profileimage, null, true);

        ImageView imgView = (ImageView) rowView.findViewById(R.id.img);
        new ImageDownloader(imgView).execute(web.get(position));

        return rowView;
    }
}