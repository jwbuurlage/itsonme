package hack.goodnight.itsonme;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by jw on 8-11-14.
 */
class ProfileGridAdapter extends ArrayAdapter<User> {
    private final Activity context;
    private final List<User> web;

    public ProfileGridAdapter(Activity context,
                              List<User> web) {
        super(context, R.layout.profileimage, web);
        this.context = context;
        this.web = web;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.profileimage, null, true);

        ImageView iv = (ImageView) rowView.findViewById(R.id.img);
        Picasso.with(context).load(web.get(position).facebook_avatar_url).into(iv);

        return rowView;
    }
}