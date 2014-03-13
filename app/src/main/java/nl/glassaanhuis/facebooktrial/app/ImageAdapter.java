package nl.glassaanhuis.facebooktrial.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by cicci on 13-3-14.
 */
public class ImageAdapter extends BaseAdapter {

    private JSONArray plaatjes;
    private Context context;

    public ImageAdapter(JSONArray plaatjes, Context context) {

        this.plaatjes = plaatjes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return plaatjes.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ProfilePictureView profile = new ProfilePictureView(context);
        try {
            profile.setProfileId(plaatjes.getString(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return profile;
    }
}
