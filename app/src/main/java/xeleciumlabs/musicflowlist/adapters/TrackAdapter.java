package xeleciumlabs.musicflowlist.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xeleciumlabs.musicflowlist.R;
import xeleciumlabs.musicflowlist.data.Track;

/**
 * Created by Xelecium on 6/8/2015.
 */
public class TrackAdapter extends BaseAdapter {

    private ArrayList<Track> mTracks;
    private LayoutInflater mInflater;

    //Base Constructor
    public TrackAdapter(Context context, ArrayList<Track> tracks) {

        mTracks = tracks;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mTracks.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.track_item, parent, false);
        TextView trackTitle = (TextView)layout.findViewById(R.id.track_title);
        Track currentTrack = mTracks.get(position);

        trackTitle.setText(currentTrack.getTitle());

        layout.setTag(position);
        return layout;
    }

    //Holds the Views containing the track items
    private static class ViewHolder {
        ImageView albumArt;
        TextView trackName;
        ImageView playButton;
    }


}
