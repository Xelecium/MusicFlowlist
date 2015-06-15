package xeleciumlabs.musicflowlist;

import android.app.Application;
import android.content.Context;

/**
 * Created by Xelecium on 6/14/2015.
 */
public class MusicFlowListApplication extends Application {

    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
    }
}
