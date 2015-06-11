package xeleciumlabs.musicflowlist.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import xeleciumlabs.musicflowlist.MusicService;
import xeleciumlabs.musicflowlist.R;
import xeleciumlabs.musicflowlist.adapters.TrackAdapter;
import xeleciumlabs.musicflowlist.data.Track;
import xeleciumlabs.musicflowlist.data.TrackList;

import static android.widget.AdapterView.*;


public class MainActivity extends Activity {

    private ArrayList<Track> mTracks;       //List of tracks on the device
    private ListView mTrackListView;            //ListView containing the list of tracks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Associate the ListView
        mTrackListView = (ListView)findViewById(R.id.trackList);
        //Initialize and populate the list of tracks on the device
        mTracks = new ArrayList<>();
//        getTrackList();
        TrackList.getTrackList(this, mTracks);

        //Sort the list of tracks alphabetically by title
        Collections.sort(mTracks, new Comparator<Track>() {
            public int compare(Track t1, Track t2) {
                return t1.getTitle().compareTo(t2.getTitle());
            }
        });

        //Adapter for the ListView
        TrackAdapter adapter = new TrackAdapter(this, mTracks);
        mTrackListView.setAdapter(adapter);
        mTrackListView.setOnItemClickListener(trackClickListener);

        //Set empty view
        TextView noMusic = (TextView)findViewById(android.R.id.empty);
        mTrackListView.setEmptyView(noMusic);
    }

//    //Helper method to populate the list, which will be used by the ListView
//    public void getTrackList() {
//
//        //Searches through storage for audio files
//        ContentResolver resolver = getContentResolver();
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        //We only want audio files that are marked as music (not notifications, ringtones, or alarms)
//        Cursor cursor = resolver.query(uri, null, MediaStore.Audio.Media.IS_MUSIC, null, null);
//
//        if(cursor != null) {
//            //Files are available
//            if (cursor.moveToFirst()) {
//                //Get info about file being referenced
//                int idColumn = cursor.getColumnIndex
//                        (MediaStore.Audio.Media._ID);
//                int titleColumn = cursor.getColumnIndex
//                        (MediaStore.Audio.Media.TITLE);
//                int artistColumn = cursor.getColumnIndex
//                        (MediaStore.Audio.Media.ARTIST);
//                int albumArtColumn = cursor.getColumnIndex
//                        (MediaStore.Audio.Media.ALBUM_ID);
//
//
//
//                //Add file to the mTracks list
//                do {
//                    long thisId = cursor.getLong(idColumn);
//                    String thisTitle = cursor.getString(titleColumn);
//                    String thisArtist = cursor.getString(artistColumn);
//                    long thisAlbumArt = cursor.getLong(albumArtColumn);
//
//                    Uri art = Uri.parse("content://media/external/audio/albumart");
//                    art = ContentUris.withAppendedId(art, thisAlbumArt);
//
//
//                    //Add the data to the list
//                    mTracks.add(new Track(thisId, thisTitle, thisArtist, art));
//                }
//                while (cursor.moveToNext());
//            }
//            //Release the cursor when done
//            cursor.close();
//        }
//    }

    OnItemClickListener trackClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Intent to go to the Follow Selector Activity
            Intent intent = new Intent(MainActivity.this, FollowSelectorActivity.class);
            //Pass in the track that was tapped
            Bundle bundle = new Bundle();
            bundle.putInt("TrackIndex", position);
            bundle.putParcelableArrayList("Tracks", mTracks);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        return super.onOptionsItemSelected(item);
    }




}
