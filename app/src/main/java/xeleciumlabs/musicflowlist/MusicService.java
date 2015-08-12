package xeleciumlabs.musicflowlist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import xeleciumlabs.musicflowlist.activities.MainActivity;
import xeleciumlabs.musicflowlist.data.Track;

/**
 * Created by Xelecium on 6/10/2015.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private static final String TAG = MusicService.class.getSimpleName();

    private String mTrackTitle = "";
    private static final int NOTIFICATION_ID = 816;

    public static final String UPDATE_TRACK = "xeleciumlabs.musicflowlist.updatetrack";
    private Intent mUpdateTrackIntent;

    private boolean mShuffle = false;
    private Random mRandom;

    //media player
    private MediaPlayer mPlayer;
    //song list
    private ArrayList<Track> mTracks;
    //current position
    private int mIndex;

    private final IBinder mMusicBinder = new MusicBinder();

    public void onCreate(){
        super.onCreate();

        //Initialize the Track Index
        mIndex = 0;
        //Create the MediaPlayer
        mPlayer = new MediaPlayer();

        initMusicPlayer();

        //Random used for shuffling, if enabled
        mRandom = new Random();
        //Send info to
        mUpdateTrackIntent = new Intent(UPDATE_TRACK);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        Log.d(TAG, "Playing track number: " + mIndex);

        //Notification info
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(mTrackTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(mTrackTitle);
        Notification not = builder.build();

        startForeground(NOTIFICATION_ID, not);

        mUpdateTrackIntent.putExtra("trackID", mIndex);
        mUpdateTrackIntent.putExtra("trackDuration", getDuration());
        sendBroadcast(mUpdateTrackIntent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mPlayer.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    public void initMusicPlayer(){
        //set player properties
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<Track> tracks){
        mTracks = tracks;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mPlayer.stop();
        mPlayer.release();
        return false;
    }

    public void setSong(int songIndex){
        mIndex = songIndex;
    }

    public void setShuffle() {
        mShuffle = !mShuffle;
    }

    //Play a track
    public void playSong() {
        //Clear the MediaPlayer
        mPlayer.reset();

        //Get the track from the array
        Track playSong = mTracks.get(mIndex);
        //Get the ID from the selected track
        long currSong = playSong.getId();
        String trackTitle = playSong.getTitle();
        //Set the URI based on the track's ID
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try{
            mPlayer.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        Log.d(TAG, "Playing track: " + trackTitle);
        mPlayer.prepareAsync();
    }

    public int getPosition(){
        return mPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    public void pausePlayer(){
        mPlayer.pause();
    }

    public void seek(int position){
        mPlayer.seekTo(position);
    }

    public void go(){
        mPlayer.start();
    }

    public void playPrev(){
        mIndex--;
        if (mIndex < 0) {
            mIndex = mTracks.size() - 1;
        }
        Log.d(TAG, "Playing previous track: " + mIndex);
        playSong();
    }

    //skip to next
    public void playNext(){

        if (mShuffle) {
            int newSong = mIndex;

            while (newSong == mIndex) {
                newSong = mRandom.nextInt(mTracks.size());
            }
            mIndex = newSong;
        }
        else {
            mIndex++;
            if (mIndex >= mTracks.size()) {
                mIndex = 0;
            }
        }
        Log.d(TAG, "Playing next track: " + mIndex);
        playSong();
    }



    //TODO: If other application using the music stream, pause media player

}
