package com.veetech.hiremee.hm_qr_scanner;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.CheckNetwork;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.Config;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.Constant;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.CustomProgressDialog;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.InternetConnectivity;
import com.veetech.hiremee.hm_qr_scanner.HireMeeID.HM_ID_UserFindActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    private static String TAG = SplashScreen.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST = 29;
    String strDeviceMAC_Address;
    CustomProgressDialog customProgressDialog;
    SharedPreferences URL_Preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        URL_Preferences = getSharedPreferences("HireMee_Scan_URL_Preference", Context.MODE_PRIVATE);
        customProgressDialog = new CustomProgressDialog(this, R.drawable.loadingicon);
        if (CheckNetwork.isInternetAvailable(SplashScreen.this)) {
            checkIfAndroidM();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                //HighEndDevices
                Log.d("Splash Screen", "Android M MAC Address-->" + get_Above_M_WifiMacAddress());
                strDeviceMAC_Address = get_Above_M_WifiMacAddress();

           /*     if(strDeviceMAC_Address.equals(""))
                {
                   strDeviceMAC_Address = GetMacAdress(this);

                }*/

                CheckDeviceIDMatching(strDeviceMAC_Address);

                /*      *//***
                 * For Adding Temperory purpose. IF the backend team check the error. comment this intent line
                 *//*
                Intent intent = new Intent(SplashScreen.this, ScanSelectionActivity.class);
                startActivity(intent);
                finish();*/

            } else {

                //LowEndDevices
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                strDeviceMAC_Address = wInfo.getMacAddress();
                Log.d("Splash Screen", "Below Android M MAC Address-->" + strDeviceMAC_Address);
                CheckDeviceIDMatching(strDeviceMAC_Address);


                /***
                 * For Adding Temperory purpose. IF the backend team check the error. comment this intent line
                 /*   *//*
                Intent intent = new Intent(SplashScreen.this, ScanSelectionActivity.class);
                startActivity(intent);
                finish();*/
            }
        } else {
            Intent intent = new Intent(SplashScreen.this, InternetConnectivity.class);
            intent.putExtra("INTERNET_KEY", "SPLASHSCREEN");
            startActivity(intent);
            finish();
        }
    }

  /*  public String GetMacAdress(Context context)
    {
        String mac = GetMacAddressLegacy(context);

        if (mac == "02:00:00:00:00:00")
        {
            var interfaces = Java.Net.NetworkInterface.NetworkInterfaces;

            foreach (var nif in interfaces)
            {
                if (!nif.Name.ToLower().Contains("wlan")) continue;

                byte[] macBytes = nif.GetHardwareAddress();

                string macString = BitConverter.ToString(macBytes);
                if (!string.IsNullOrWhiteSpace(macString))
                    mac = macString.Trim().ToUpper().Replace("-", ":");
            }
        }

        return mac;
    }*/

    private void CheckDeviceIDMatching(String strDeviceMAC_Address) {

        final String userDeviceID = strDeviceMAC_Address;
        Log.d(TAG, "Mobile Device userDeviceID--->" + userDeviceID);
        customProgressDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Config.VerifyDeviceId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE--->" + response);
                // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                try {

                    JSONObject APIResponse = new JSONObject(response);
                    customProgressDialog.dismiss();

                    String code = APIResponse.getString("code");
                    String Message = APIResponse.getString("message");

                    switch (code) {
                        case "200":

                            String LiveURL = APIResponse.getString("response");
                            String scan_count = APIResponse.getString("scan_count");

                            String device_id = APIResponse.getString("device_id");
                            String token = APIResponse.getString("Token");
                            String expire = APIResponse.getString("Expire");
                            if (scan_count == null || scan_count.equals("")) {
                                scan_count = "0";
                            }


                            SharedPreferences.Editor editor = URL_Preferences.edit();
                            editor.putString("ScanningURL", LiveURL);
                            editor.putString("scan_count", scan_count);
                            editor.putString("device_id", device_id);
                            editor.putString("Token", token);
                            editor.putString("Expire", expire);
                            editor.apply();
                            editor.commit();

                            Intent intent = new Intent(SplashScreen.this, ScanSelectionActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case "203":

                            new android.support.v7.app.AlertDialog.Builder(SplashScreen.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Information")
                                    .setMessage("Device not yet mapped with HireMee. Contact Admin")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            finish();
                                        }
                                    })
                                    .show();
                            break;

                        default:
                            Constant.ToastBottomMessage(SplashScreen.this, APIResponse.getString("message"));
                            finish();
                            break;
                    }

                } catch (JSONException e) {
                    customProgressDialog.dismiss();
                    new android.support.v7.app.AlertDialog.Builder(SplashScreen.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Information")
                            .setMessage(e.getMessage())
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    finish();
                                }
                            })
                            .show();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customProgressDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            new android.support.v7.app.AlertDialog.Builder(SplashScreen.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Information")
                                    .setMessage("Connection failed. Contact Admin!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            finish();
                                        }
                                    })
                                    .show();
                            //Toast.makeText(SplashScreen.this, "Network Busy!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            new android.support.v7.app.AlertDialog.Builder(SplashScreen.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Information")
                                    .setMessage("AuthFailureError. Contact Admin!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            finish();
                                        }
                                    })
                                    .show();
                            //Toast.makeText(SplashScreen.this, "AuthFailureError  ", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            // Toast.makeText(SplashScreen.this, "ServerError  ", Toast.LENGTH_LONG).show();
                            new android.support.v7.app.AlertDialog.Builder(SplashScreen.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Information")
                                    .setMessage("Server is not responding. Contact Admin!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            finish();
                                        }
                                    })
                                    .show();
                        } else if (error instanceof NetworkError) {
                            new android.support.v7.app.AlertDialog.Builder(SplashScreen.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Information")
                                    .setMessage("Network not responding. try again later!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            finish();
                                        }
                                    })
                                    .show();
                            //  Toast.makeText(SplashScreen.this, "NetworkError  ", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(SplashScreen.this, "ParseError  ", Toast.LENGTH_LONG).show();
                        } else {
                            error.printStackTrace();
                            new android.support.v7.app.AlertDialog.Builder(SplashScreen.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Information")
                                    .setMessage("Unknown error. try again later!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // params.put("webtoken", Config.HM_WebToken);
                params.put("device_id", userDeviceID);
                params.put("SecretKey", Config.SECRETE_KEY);
                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(postRequest);
    }

    public void checkIfAndroidM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_NETWORK_STATE},
                        MY_PERMISSIONS_REQUEST);
            }
            else {
                Log.d("Home", "Already granted access");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Home", "Permission Granted");

                } else {
                    Log.d("Home", "Permission Failed");
                    Toast.makeText(getApplicationContext(), "You must allow to get network and device state functionalities from your mobile device.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public static String get_Above_M_WifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }


}
