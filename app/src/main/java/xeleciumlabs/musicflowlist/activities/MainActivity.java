package xeleciumlabs.musicflowlist.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import xeleciumlabs.musicflowlist.MusicController;
import xeleciumlabs.musicflowlist.MusicService;
import xeleciumlabs.musicflowlist.MusicService.MusicBinder;
import xeleciumlabs.musicflowlist.R;
import xeleciumlabs.musicflowlist.adapters.TrackAdapter;
import xeleciumlabs.musicflowlist.data.Track;
import xeleciumlabs.musicflowlist.data.TrackList;

import static android.widget.AdapterView.*;


public class MainActivity extends Activity {

    private ArrayList<Track> mTracks;       //List of tracks on the device
    private ListView mTrackListView;            //ListView containing the list of tracks

    private MusicService mMusicService;
    private Intent playIntent;
    private boolean musicBound = false;

    private boolean paused = false;
    private boolean playbackPaused = false;

    private MusicController controller;

    private LinearLayout mCurrentTrack;

    //Mediaplayer controls
    private ImageView mPlayPrevTrackButton;
    private ImageView mPlayRewindButton;
    private ImageView mPlayPauseButton;
    private ImageView mPlayForwardButton;
    private ImageView mPlayNextTrackButton;

    //Currently Playing Track
    private ImageView mCurrentTrackAlbumArt;
    private TextView mCurrentTrackTitle;
    private TextView mCurrentTrackDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentTrack = (LinearLayout)findViewById(R.id.currentSong);

        //Media Playback controller
        mPlayPrevTrackButton = (ImageView)findViewById(R.id.prevSong);
        mPlayPrevTrackButton.setOnClickListener(playPrev);
        mPlayRewindButton = (ImageView)findViewById(R.id.rewind);
        mPlayRewindButton.setOnClickListener(rewind);
        mPlayPauseButton = (ImageView)findViewById(R.id.play);
        mPlayPauseButton.setOnClickListener(playPause);
        mPlayForwardButton = (ImageView)findViewById(R.id.forward);
        mPlayForwardButton.setOnClickListener(forward);
        mPlayNextTrackButton = (ImageView)findViewById(R.id.nextSong);
        mPlayNextTrackButton.setOnClickListener(playNext);

        mCurrentTrackAlbumArt = (ImageView)findViewById(R.id.currentSongAlbumArt);
        mCurrentTrackTitle = (TextView)findViewById(R.id.song_title);
        mCurrentTrackDuration = (TextView)findViewById(R.id.song_duration);

        mCurrentTrack.setVisibility(GONE);

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

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            mMusicService = binder.getService();
            //pass list
            mMusicService.setList(mTracks);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }




    OnItemClickListener trackClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            //Intent to go to the Follow Selector Activity
//            Intent intent = new Intent(MainActivity.this, FollowSelectorActivity.class);
//            //Pass in the track that was tapped
//            Bundle bundle = new Bundle();
//            bundle.putInt("TrackIndex", position);
//            bundle.putParcelableArrayList("Tracks", mTracks);
//            intent.putExtras(bundle);
//            startActivity(intent);

            //mMusicService.setSong(Integer.parseInt(view.getTag().toString()));
            mMusicService.setSong(position);
            mMusicService.playSong();

            if (playbackPaused) {
                playbackPaused = false;
            }

            mCurrentTrack.setVisibility(VISIBLE);
        }
    };

    //Play previous track
    OnClickListener playPrev = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mMusicService.playPrev();
            if (playbackPaused) {
                playbackPaused = false;
            }
            mCurrentTrack.setVisibility(View.VISIBLE);
        }
    };

    //Rewind 10 seconds
    OnClickListener rewind = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int currentPosition = mMusicService.getPosn();
            int seekPosition = currentPosition - (10 * 1000);

            if (seekPosition < 0) {
                seekPosition = 0;
            }

            mMusicService.seek(seekPosition);
        }
    };

    //Toggle between play and pause
    OnClickListener playPause = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (playbackPaused) {
                mMusicService.go();
                mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause));
            }
            else {
                mMusicService.pausePlayer();
                mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.play));
            }
            playbackPaused = !playbackPaused;
        }
    };

    //Forward 10 seconds
    OnClickListener forward = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int currentPosition = mMusicService.getPosn();
            int seekPosition = currentPosition + (10 * 1000);
            if (seekPosition > mMusicService.getDur()) {
                seekPosition = mMusicService.getDur();
            }

            mMusicService.seek(seekPosition);
        }
    };

    //Play next track
    OnClickListener playNext = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mMusicService.playNext();
            if (playbackPaused) {
//                setController();
                playbackPaused = false;
            }
            mCurrentTrack.setVisibility(View.VISIBLE);
            //controller.show(0);
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
