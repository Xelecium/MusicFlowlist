package xeleciumlabs.musicflowlist.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Xelecium on 6/10/2015.
 */
public class Track implements Parcelable {

    private long mId;
    private String mTitle;
    private String mArtist;
    private Uri mAlbumArt;

    private Uri mTrackUri;                      //URI of the track being referenced
    private ArrayList<Track> mFollowTracks;    //List of the tracks to follow the reference track

    public Track(long trackId, String trackTitle, String trackArtist, Uri trackAlbumArt) {
        mId = trackId;
        mTitle = trackTitle;
        mArtist = trackArtist;
        mAlbumArt = trackAlbumArt;
    }

    private Track(Parcel in) {
        //opening the Parcelable when it is passed between Activities
        mId = in.readLong();
        mTitle = in.readString();
        mArtist = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Saving data as a Parcelable so it can be passed between Activities
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
    }

    public Uri getAlbumArt() {
        return mAlbumArt;
    }
    //Add a track to the list of follow tracks
    public void addFollowTrack(Track followTrack) {
        mFollowTracks.add(followTrack);
    }

    //Remove a track from the list of follow tracks
    public void removeFollowTrack(Track followTrack) {
        mFollowTracks.remove(followTrack);
        if (mFollowTracks.isEmpty()) {
            //remove the track listing from the tracklist
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

}

