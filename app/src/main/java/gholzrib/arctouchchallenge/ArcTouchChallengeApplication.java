package gholzrib.arctouchchallenge;

import android.app.Application;

import okhttp3.OkHttpClient;

/**
 * Created by Gunther Ribak on 30/11/2016.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class ArcTouchChallengeApplication extends Application {

    private static OkHttpClient mClient = null;

    public static OkHttpClient getClientInstance() {
        if (mClient == null) {
            mClient = new OkHttpClient();
        }
        return mClient;
    }

}
