package xeleciumlabs.musicflowlist.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import xeleciumlabs.musicflowlist.R;
import xeleciumlabs.musicflowlist.adapters.TrackAdapter;
import xeleciumlabs.musicflowlist.data.Track;


public class MainActivity extends Activity {

    private ArrayList<Track> mTracks;
    private ListView mTrackList;

    //@InjectView(R.id.trackList)ListView mTrackList;

    //OnCreate is for setting up static elements of the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Attaches ListView for all audio tracks
//        ButterKnife.inject(this);
        mTrackList = (ListView)findViewById(R.id.trackList);
        //Instantiates the array to store the audio tracks
        mTracks = new ArrayList<Track>();
        //Helper method to add audio tracks to the mTracks list
        getTrackList();
        //Sorts the mTracks list alphabetically
        Collections.sort(mTracks, new Comparator<Track>() {
            public int compare (Track t1, Track t2) {
                return t1.getTitle().compareTo(t2.getTitle());
            }
        });

        //Adapter for the ListView
        TrackAdapter adapter = new TrackAdapter(this, mTracks);
        mTrackList.setAdapter(adapter);
    }
    //OnResume is for whenever the user loads up the activity
    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getTrackList() {

        //Searches through storage for audio files
        ContentResolver resolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, MediaStore.Audio.Media.IS_MUSIC, null, null);

        if(cursor != null) {
            //Files are available
            if (cursor.moveToFirst()) {
                //Get info about file being referenced
                int idColumn = cursor.getColumnIndex
                        (MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndex
                        (MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndex
                        (MediaStore.Audio.Media.ARTIST);
                //Add file to the mTracks list
                do {
                    long thisId = cursor.getLong(idColumn);
                    String thisTitle = cursor.getString(titleColumn);
                    String thisArtist = cursor.getString(artistColumn);
                    mTracks.add(new Track(thisId, thisTitle, thisArtist));
                }
                while (cursor.moveToNext());
            }
            //Files are not available
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
