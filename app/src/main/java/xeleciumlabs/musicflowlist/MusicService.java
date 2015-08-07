package xeleciumlabs.musicflowlist;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

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

    private String mTrackTitle = "";
    private static final int NOTIFICATION_ID = 816;

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
        //create the service
        super.onCreate();
        //initialize position
        mIndex = 0;
        //create player
        mPlayer = new MediaPlayer();

        initMusicPlayer();

        mRandom = new Random();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();

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

    public void playSong() {
        //play a song
        mPlayer.reset();

        //get song
        Track playSong = mTracks.get(mIndex);
        //get id
        long currSong = playSong.getId();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try{
            mPlayer.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mPlayer.prepareAsync();
    }

    public int getPosn(){
        return mPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mPlayer.getDuration();
    }

    public boolean isPng(){
        return mPlayer.isPlaying();
    }

    public void pausePlayer(){
        mPlayer.pause();
    }

    public void seek(int posn){
        mPlayer.seekTo(posn);
    }

    public void go(){
        mPlayer.start();
    }

    public void playPrev(){
        mIndex--;
        if (mIndex < 0) {
            mIndex = mTracks.size() - 1;
        }
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
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }


}
