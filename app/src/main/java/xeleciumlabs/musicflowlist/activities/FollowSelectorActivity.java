package xeleciumlabs.musicflowlist.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import xeleciumlabs.musicflowlist.MusicService;
import xeleciumlabs.musicflowlist.R;
import xeleciumlabs.musicflowlist.adapters.FollowTrackAdapter;
import xeleciumlabs.musicflowlist.data.Track;

public class FollowSelectorActivity extends Activity implements MediaPlayerControl {

    private ImageView mCurrentTrackAlbumArt;
    private TextView mCurrentTrackTitle;

    private MusicService mService;
    private Intent mPlayIntent;
    private boolean musicBound = false;
    private int mSelectedTrack;
    private ArrayList<Track> mTracks;
    private ListView mTrackListView;
    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_selector);

        //Get the passed data
        //Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        mSelectedTrack = bundle.getInt("TrackIndex", 0);
        mTracks = bundle.getParcelableArrayList("Tracks");

        //Set up the Music service
        connectService();

        if(mPlayIntent == null){
            mPlayIntent = new Intent(this, MusicService.class);
            bindService(mPlayIntent, mConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);
        }

//        mService.setContext(this);
        mService.setList(mTracks);

        //Setting up the ListView
        mTrackListView = (ListView)findViewById(R.id.followTrackList);
        FollowTrackAdapter adapter = new FollowTrackAdapter(this, mTracks);
        mTrackListView.setAdapter(adapter);

        //Setting up the View for the currently selected track
        mCurrentTrackAlbumArt = (ImageView) findViewById(R.id.albumArt);
        mCurrentTrackTitle = (TextView)findViewById(R.id.track_title);

        //Same code as for the adapter, but just for the current track,
        // which is not part of the ListView
        Track currentTrack = mTracks.get(mSelectedTrack);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), currentTrack.getAlbumArt());
        } catch (FileNotFoundException exception) {
            //If a track doesn't have an associated album art, we'll provide a default one
            exception.printStackTrace();
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.empty_albumart);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCurrentTrackAlbumArt.setImageBitmap(bitmap);
        mCurrentTrackTitle.setText(currentTrack.getTitle());
        //TODO: Play/Pause button & Next Track button
        //TODO: Set up the SeekBar divider to show current progress

        //TODO: Future feature
        //Tapping on the current track will change to a more traditional looking
        //music player, prominently displaying the album art with playback controls
        //at the bottom


        //Not setting an empty view because we can't get to this activity with an empty list,
        // as there's nothing to click

        mService.setSong(mSelectedTrack);
        mService.playSong();
    }

    //When the activity starts, we'll start the MusicService
    @Override
    protected void onStart() {
        super.onStart();
        if(mPlayIntent == null){
            mPlayIntent = new Intent(this, MusicService.class);
            bindService(mPlayIntent, mConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);
        }
    }

    private void connectService() {
        mService = new MusicService();
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
                //get service
                mService = binder.getService();
                //pass list
                mService.setList(mTracks);
                musicBound = true;
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_follow_selector, menu);
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

    @Override
    //When the activity is closed, release resources
    protected void onDestroy() {
        stopService(mPlayIntent);
        mService = null;
        super.onDestroy();
    }

    //Mediaplayer methods
    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
