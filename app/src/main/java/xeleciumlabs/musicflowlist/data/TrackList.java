package xeleciumlabs.musicflowlist.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by Xelecium on 6/11/2015.
 */
public abstract class TrackList {

    public static ArrayList<Track> getTrackList(Context context, ArrayList<Track> tracks) {

        //Searches through storage for audio files
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //We only want audio files that are marked as music (not notifications, ringtones, or alarms)
        Cursor cursor = resolver.query(uri, null, MediaStore.Audio.Media.IS_MUSIC, null, null);

        if(cursor != null) {
            //Files are available
            if (cursor.moveToFirst()) {
                //Get info about file being referenced
                int idColumn = cursor.getColumnIndex
                        (MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndex
                        (MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndex
                        (MediaStore.Audio.Media.ARTIST);
                int albumArtColumn = cursor.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM_ID);



                //Add file to the mTracks list
                do {
                    long thisId = cursor.getLong(idColumn);
                    String thisTitle = cursor.getString(titleColumn);
                    String thisArtist = cursor.getString(artistColumn);
                    long thisAlbumArt = cursor.getLong(albumArtColumn);

                    Uri art = Uri.parse("content://media/external/audio/albumart");
                    art = ContentUris.withAppendedId(art, thisAlbumArt);


                    //Add the data to the list
                    tracks.add(new Track(thisId, thisTitle, thisArtist, art));
                }
                while (cursor.moveToNext());
            }
            //Release the cursor when done
            cursor.close();
        }

        return tracks;
    }
}
