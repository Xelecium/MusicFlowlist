package xeleciumlabs.musicflowlist.data;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Xelecium on 6/8/2015.
 */
public class Track {


    private long mId;
    private String mTitle;
    private String mArtist;

    private Uri mTrackUri;                      //URI of the track being referenced
    private ArrayList<Track> mFollowTracks;    //List of the tracks to follow the reference track

    public Track(long trackId, String trackTitle, String trackArtist) {
        mId = trackId;
        mTitle = trackTitle;
        mArtist = trackArtist;
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
}
