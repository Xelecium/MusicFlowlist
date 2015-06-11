package xeleciumlabs.musicflowlist;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import xeleciumlabs.musicflowlist.data.Track;

/**
 * Created by Xelecium on 6/10/2015.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer mPlayer;
    //song list
    private ArrayList<Track> mTracks;
    //current position
    private int mIndex;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return musicBind;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }

    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        mIndex = 0;
        //create player
        mPlayer = new MediaPlayer();

        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        //mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<Track> theSongs){
        mTracks = theSongs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    private final IBinder musicBind = new MusicBinder();


    @Override
    public boolean onUnbind(Intent intent){
        mPlayer.stop();
        mPlayer.release();
        return false;
    }
    public void playSong(){
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
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

    public void setSong(int songIndex){
        mIndex = songIndex;
    }

}
