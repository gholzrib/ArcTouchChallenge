package gholzrib.arctouchchallenge.core.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.widget.Toast;


public class CheckConnection {

    private static final String CONNECTION_ERROR_MESSAGE = "No internet connection, please try again later";

    /**
     * Checks if there is connection to the internet
     *
     * @param context
     * @param shouldShowToast Flag that informs if, in case the isn't connection, the Toast will
     *                        be shown
     * @return                If there is connection to the internet
     */
    public static boolean hasInternetConnection(Context context, boolean shouldShowToast) {

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isAvailable()
                    && cm.getActiveNetworkInfo().isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (shouldShowToast) {
            Toast.makeText(context, CONNECTION_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
        }

        return false;
    }

}
