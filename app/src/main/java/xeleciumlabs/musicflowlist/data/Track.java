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
    private String mAlbum;
    private Uri mAlbumArt;

    private Uri mTrackUri;                      //URI of the track being referenced

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    public Track(long trackId, String trackTitle, String trackArtist, String trackAlbum, Uri trackAlbumArt) {
        mId = trackId;
        mTitle = trackTitle;
        mArtist = trackArtist;
        mAlbum = trackAlbum;
        mAlbumArt = trackAlbumArt;
    }

    private Track(Parcel in) {
        //opening the Parcelable when it is passed between Activities
        mId = in.readLong();
        mTitle = in.readString();
        mArtist = in.readString();
        mAlbum = in.readString();
        mAlbumArt = Uri.parse(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Saving data as a Parcelable so it can be passed between Activities
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mArtist);
        dest.writeString(mAlbum);
        dest.writeString(mAlbumArt.toString());
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

    public String getAlbum() {
        return mAlbum;
    }

    public Uri getAlbumArt() {
        return mAlbumArt;
    }

    //Add a track to the list of follow tracks
    public void addFollowTrack(Track followTrack) {

        //search the database for an item matching the current track
        //if it doesn't exist
            //create one

        //add selected track as a followtrack for the current track
    }

    //Remove a track from the list of follow tracks
    public void removeFollowTrack(Track followTrack) {

        //remove the selected track as a followtrack for the current track
        //if the current track has no more followtracks
        //remove the current track from the database
    }

    @Override
    public int describeContents() {
        return 0;
    }


}

