package xeleciumlabs.musicflowlist.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    //TODO: Getting Started
    //Set up a brief tutorial on how the app works, with a flag in the application
    //for if the user has gone through it or not


    //TODO: Random track
    //Choose a random track to start playing

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        return super.onOptionsItemSelected(item);
    }


}
