package xeleciumlabs.musicflowlist.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

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

    private boolean playbackPaused = false;
    private boolean seeking = false;

    private Handler mHandler = new Handler();

    private LinearLayout mPlayBackContainer;

    //Mediaplayer controls
    private ImageView mPlayPrevTrackButton;
    private ImageView mPlayRewindButton;
    private ImageView mPlayPauseButton;
    private ImageView mPlayForwardButton;
    private ImageView mPlayNextTrackButton;

    private SeekBar mPlaySeekBar;

    //Currently Playing Track
    private ImageView mCurrentTrackAlbumArt;
    private TextView mCurrentTrackTitle;
    private TextView mCurrentTrackTime;
    private TextView mCurrentTrackLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayBackContainer = (LinearLayout)findViewById(R.id.currentSong);

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

        mPlaySeekBar = (SeekBar)findViewById(R.id.songSeekBar);
        mPlaySeekBar.setOnSeekBarChangeListener(seekBar);

        mCurrentTrackAlbumArt = (ImageView)findViewById(R.id.currentTrackAlbumArt);
        mCurrentTrackTitle = (TextView)findViewById(R.id.currentTrackTitle);
        mCurrentTrackTime = (TextView)findViewById(R.id.currentTrackTime);
        mCurrentTrackLength = (TextView)findViewById(R.id.currentTrackDuration);

        mPlayBackContainer.setVisibility(GONE);

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
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
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

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(updateTrackReceiver, new IntentFilter(MusicService.UPDATE_TRACK));
    }

    BroadcastReceiver updateTrackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int trackID = intent.getIntExtra("trackID", -1);
            int trackDuration = intent.getIntExtra("trackDuration", 0);
            Track currentTrack = mTracks.get(trackID);
            String trackTitle = currentTrack.getTitle();
            Uri trackAlbumArt = currentTrack.getAlbumArt();

            Bitmap bitmap = null;
            try {
                //Get the image associated with the album art identifier
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), trackAlbumArt);
            } catch (FileNotFoundException exception) {
                //If a track doesn't have an associated album art, we'll provide a default one
                exception.printStackTrace();
                bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.empty_albumart);

            } catch (IOException e) {

                e.printStackTrace();
            }

            mCurrentTrackTitle.setText(trackTitle);
            mCurrentTrackAlbumArt.setImageBitmap(bitmap);
            mCurrentTrackLength.setText(timeParse(trackDuration));
            mPlaySeekBar.setMax(trackDuration);
        }
    };

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

            mMusicService.setSong(position);
            mMusicService.playSong();

            if (playbackPaused) {
                playbackPaused = false;
            }

            mPlayBackContainer.setVisibility(VISIBLE);

            updateTrackTime();
        }
    };

    //Play previous track
    OnClickListener playPrev = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mMusicService.playPrev();
            if (playbackPaused) {
                playbackPaused = false;
                mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause));
            }
        }
    };

    //Rewind 10 seconds
    OnClickListener rewind = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int currentPosition = mMusicService.getPosition();
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
            int currentPosition = mMusicService.getPosition();
            int seekPosition = currentPosition + (10 * 1000);
            if (seekPosition > mMusicService.getDuration()) {
                seekPosition = mMusicService.getDuration();
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
                playbackPaused = false;
                mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause));
            }
        }
    };

    OnSeekBarChangeListener seekBar = new OnSeekBarChangeListener() {
        int newPosition;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                newPosition = progress;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //set flag that user is seeking
            seeking = true;
//            mHandler.removeCallbacks(updateTrackTime);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //unset flag that user is seeking
            seeking = false;
            //Seek to the corresponding part of the track
            mMusicService.seek(newPosition);

            updateTrackTime();
        }
    };

    public void updateTrackTime() {
        mHandler.postDelayed(updateTrackTime, 100);
    }


    private Runnable updateTrackTime = new Runnable() {
        @Override
        public void run() {
            //Behavior will be slightly different depending on if the user is seeking
            if (seeking) {
                //Take the position based on where the SeekBar thumb is
                int trackPosition = mPlaySeekBar.getProgress();
                //Update the TextView with the time based on the SeekBar Thumb's position
                String trackTime = timeParse(trackPosition);
                mCurrentTrackTime.setText(trackTime);
                //Decrease the delay between updates to be smoother
                mHandler.postDelayed(this, 50);
            }
            else {
                //Update position of the SeekBar
                int trackPosition = mMusicService.getPosition();
                mPlaySeekBar.setProgress(trackPosition);
                //Update the TextView with the current time
                String trackTime = timeParse(trackPosition);
                mCurrentTrackTime.setText(trackTime);
                //Repeat the runnable
                mHandler.postDelayed(this, 100);
            }
        }
    };

    public String timeParse(int milliTime) {
        //if time provided is less than 10 minutes, format as m:ss
        if (milliTime < (10 * 60 * 1000)) {
            return String.format("%01d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(milliTime),
                    TimeUnit.MILLISECONDS.toSeconds(milliTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliTime)));
        }

        //if time provided is less than 1 hour, format as mm:ss
        if (milliTime < (60 * 60 * 1000)) {
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(milliTime) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliTime)),
                    TimeUnit.MILLISECONDS.toSeconds(milliTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliTime)));
        }

        //if time provided is over an hour, format as h:mm:ss (h can be any number
        return String.format("%d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliTime),
                TimeUnit.MILLISECONDS.toMinutes(milliTime) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliTime)),
                TimeUnit.MILLISECONDS.toSeconds(milliTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliTime)));
    }

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
