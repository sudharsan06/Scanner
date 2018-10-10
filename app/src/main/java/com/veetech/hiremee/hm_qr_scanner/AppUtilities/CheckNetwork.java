package com.veetech.hiremee.hm_qr_scanner.AppUtilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

//TODO ** To checking internet connectivity
public class CheckNetwork {

    private static final String TAG = CheckNetwork.class.getSimpleName();

    public static boolean isInternetAvailable(Context context)
    {

        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null)
        {
            Log.d(TAG, "no internet connection");
            return false;
        }
        else
        {
            if(info.isConnected())
            {
                Log.d(TAG, " internet connection available...");
                return true;
            }
            else
            {
                Log.d(TAG, " internet connection");
                return true;
            }
        }
    }
}