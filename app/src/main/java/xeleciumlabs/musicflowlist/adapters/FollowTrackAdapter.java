package xeleciumlabs.musicflowlist.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import xeleciumlabs.musicflowlist.R;
import xeleciumlabs.musicflowlist.data.Track;

/**
 * Created by Xelecium on 6/10/2015.
 */
public class FollowTrackAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Track> mTracks;
    private LayoutInflater mInflater;

    //Base Constructor
    public FollowTrackAdapter(Context context, ArrayList<Track> tracks) {
        mContext = context;
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

        ViewHolder holder;

        //if view is not yet populated
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.follow_track_item, parent, false);

            holder = new ViewHolder();
            holder.albumArt = (ImageView)convertView.findViewById(R.id.albumArt);
            holder.trackName = (TextView)convertView.findViewById(R.id.track_title);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        Track currentTrack = mTracks.get(position);

        //A little work is needed to get the album art for each file
        Bitmap bitmap = null;
        try {
            //Get the image associated with the album art identifier
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), currentTrack.getAlbumArt());
        } catch (FileNotFoundException exception) {
            //If a track doesn't have an associated album art, we'll provide a default one
            exception.printStackTrace();
            bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.empty_albumart);

        } catch (IOException e) {

            e.printStackTrace();
        }

        holder.albumArt.setImageBitmap(bitmap);
        holder.trackName.setText(currentTrack.getTitle());

        return convertView;
    }

    //Holds the Views containing the track items
    private static class ViewHolder {
        ImageView albumArt;
        TextView trackName;
        CheckBox checkBox;
    }


}
