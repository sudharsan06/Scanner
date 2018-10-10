package com.veetech.hiremee.hm_qr_scanner.HallTicket;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.CustomProgressDialog;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.InternetConnectivity;
import com.squareup.picasso.Picasso;
import com.veetech.hiremee.hm_qr_scanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//TODO ** To show user status

public class HallTicket_UserFindActivity extends AppCompatActivity {

    Bundle extras;
    String strUserName, strUserID, successResult, strExam, strProfileImage;

    String TAG = HallTicket_UserFindActivity.class.getSimpleName();
    TextView seekerName, seekerID, seekerStatus, seekerExam, failureInfo, tv_scan_count;
    ImageView imgStatusImage, userImage;

    SharedPreferences URL_Preferences;
    String str_saved_URL;
    private CustomProgressDialog customProgressDialogTwo;
    private String strDeviceMAC_Address;

    String strScannedValue, strAuthStatus, strResult, scanCount;
    String UserHireMeeID, UserName, Examname, strScanStatus;
    private CoordinatorLayout coordinatorLayout;
    String newURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_find);
        setTitle("Authentication Status");

        URL_Preferences = getSharedPreferences("HireMee_Scan_URL_Preference", Context.MODE_PRIVATE);
        str_saved_URL = URL_Preferences.getString("ScanningURL", "");
        tv_scan_count = (TextView) findViewById(R.id.tv_scan_count);
        scanCount = URL_Preferences.getString("scan_count", "");

        if (scanCount == null || scanCount == "") {
            scanCount = "0";
        }
        //animateTextView(0, Integer.parseInt(scanCount), tv_scan_count);


        customProgressDialogTwo = new CustomProgressDialog(HallTicket_UserFindActivity.this, R.drawable.loadingicon);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);
        seekerName = (TextView) findViewById(R.id.tv_seekerName);
        seekerID = (TextView) findViewById(R.id.tv_userID);
        seekerStatus = (TextView) findViewById(R.id.tv_status);
        seekerExam = (TextView) findViewById(R.id.tv_userExam);

        failureInfo = (TextView) findViewById(R.id.tv_failure_info);

        imgStatusImage = (ImageView) findViewById(R.id.imageView);
        userImage = (ImageView) findViewById(R.id.userImage);


        Intent userScanResult = getIntent();

        if (userScanResult.hasExtra("Scan_Status")) {
            strScanStatus = userScanResult.getExtras().getString("Scan_Status");
            if (strScanStatus.equals("TRUE")) {
                strScannedValue = userScanResult.getExtras().getString("ScannedValue");
                strResult = userScanResult.getExtras().getString("ScannedID");
                callNetworkURL();

            } else {

                seekerName.setVisibility(View.GONE);
                seekerID.setVisibility(View.GONE);
                seekerExam.setVisibility(View.GONE);
                userImage.setVisibility(View.GONE);
                failureInfo.setVisibility(View.GONE);

                imgStatusImage.setVisibility(View.VISIBLE);
                seekerStatus.setVisibility(View.VISIBLE);

                imgStatusImage.setImageResource(R.drawable.cancel_two);
                seekerStatus.setText("Invalid QR Code, If it is valid scan again!");
                seekerName.setText("");
                seekerExam.setText("");
                seekerID.setText("");

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "do you want to scan again?", Snackbar.LENGTH_LONG)
                        .setAction("Scan Now!", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                startActivity(QRScanAgain);
                                finish();
                            }
                        });

                snackbar.setDuration(15000);
                snackbar.setActionTextColor(Color.WHITE);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.green));
                snackbar.show();

            }
        }
    }

    private void callNetworkURL() {


        if (CheckNetwork.isInternetAvailable(HallTicket_UserFindActivity.this)) {

            customProgressDialogTwo.show();

      /*      String LocalString;

            if (str_saved_URL.substring(str_saved_URL.length() - 1).equals("/")) {
                LocalString = str_saved_URL + strScannedValue + "/YtT76LUJovG6JDJuOCCs5w==";  //TODO ** Entered URL + Scanned Value + Encrypted Token.
                newURL = LocalString.replaceAll("\\s", ""); //TODO **Remove all white spaces in between the string
            } else {
                LocalString = str_saved_URL + "/" + strScannedValue + "/YtT76LUJovG6JDJuOCCs5w==";
                newURL = LocalString.replaceAll("\\s", "");  //TODO **Remove all white spaces in between the string
            }

            Log.d("UserFindActivity", "URL value is--->" + newURL);*/


            //TODO ** Make API Call
            StringRequest codeAvailableRequest = new StringRequest(Request.Method.POST, Config.validateHallTicket, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    customProgressDialogTwo.dismiss();

                    Log.d("MainActivity", "network Response VAlue-->" + response);
                    try {

                        if(response!=null)
                        {
                            JSONObject fullResponse = new JSONObject(response);
                            strAuthStatus = fullResponse.getString("Status");
                            UserHireMeeID = fullResponse.getString("HireMeeID");
                            UserName = fullResponse.getString("Name");
                            Examname = fullResponse.getString("ExamName");

                            if (strAuthStatus.equals("")) {
                                MakeProfilePictureCall(strResult);

                                seekerName.setVisibility(View.GONE);
                                seekerID.setVisibility(View.GONE);
                                seekerExam.setVisibility(View.GONE);
                                userImage.setVisibility(View.GONE);
                                failureInfo.setVisibility(View.GONE);

                                imgStatusImage.setVisibility(View.VISIBLE);
                                seekerStatus.setVisibility(View.VISIBLE);

                                imgStatusImage.setImageResource(R.drawable.cancel_two);
                                seekerStatus.setText("Invalid QR Code, If it is valid scan again!");

                                seekerName.setText("");
                                seekerExam.setText("");
                                seekerID.setText("");

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "do you want to scan again?", Snackbar.LENGTH_LONG)
                                        .setAction("Scan Now!", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                                startActivity(QRScanAgain);
                                                finish();
                                            }
                                        });

                                snackbar.setDuration(15000);
                                snackbar.setActionTextColor(Color.WHITE);

                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(getResources().getColor(R.color.green));
                                snackbar.show();

                                Log.d(TAG, "No value returned from MainActivity");


                            } else if (strAuthStatus.equals("Authenticated Successfully.")) {
                                MakeProfilePictureCall(strResult);

                                seekerName.setVisibility(View.VISIBLE);
                                seekerID.setVisibility(View.VISIBLE);
                                seekerExam.setVisibility(View.VISIBLE);
                                userImage.setVisibility(View.VISIBLE);
                                failureInfo.setVisibility(View.GONE);
                                imgStatusImage.setVisibility(View.VISIBLE);
                                seekerStatus.setVisibility(View.VISIBLE);

                                imgStatusImage.setImageResource(R.drawable.tick_one);
                                seekerName.setTextSize(30);
                                seekerID.setText(UserHireMeeID);
                                seekerExam.setText(Examname);
                                seekerName.setText(UserName);
                                seekerStatus.setText(strAuthStatus);

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "do you want to scan next?", Snackbar.LENGTH_LONG)
                                        .setAction("Scan Now!", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                                startActivity(QRScanAgain);
                                                finish();
                                            }
                                        });

                                snackbar.setDuration(15000);
                                snackbar.setActionTextColor(Color.WHITE);

                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(getResources().getColor(R.color.green));
                                snackbar.show();


                            } else if (strAuthStatus.equals("Authentication Failed.")) {


                                seekerID.setVisibility(View.GONE);
                                seekerExam.setVisibility(View.GONE);
                                userImage.setVisibility(View.GONE);
                                failureInfo.setVisibility(View.GONE);

                                imgStatusImage.setVisibility(View.VISIBLE);
                                seekerStatus.setVisibility(View.VISIBLE);

                                imgStatusImage.setImageResource(R.drawable.cancel_two);
                                seekerStatus.setText("Authenticated failed.");

                                seekerName.setText("");
                                seekerExam.setText("");
                                seekerID.setText("");


                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "do you want to scan again?", Snackbar.LENGTH_LONG)
                                        .setAction("Scan Now!", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                                startActivity(QRScanAgain);
                                                finish();
                                            }
                                        });

                                snackbar.setDuration(15000);
                                snackbar.setActionTextColor(Color.WHITE);

                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(getResources().getColor(R.color.green));
                                snackbar.show();

                            }else{
                                seekerName.setVisibility(View.VISIBLE);
                                imgStatusImage.setVisibility(View.VISIBLE);
                                imgStatusImage.setImageResource(R.drawable.cancel_two);

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "do you want to scan again?", Snackbar.LENGTH_LONG)
                                        .setAction("Scan Now!", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                                startActivity(QRScanAgain);
                                                finish();
                                            }
                                        });

                                snackbar.setDuration(15000);
                                snackbar.setActionTextColor(Color.WHITE);

                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(getResources().getColor(R.color.green));
                                snackbar.show();
                            }
                        }else{
                            Toast.makeText(HallTicket_UserFindActivity.this, "No response from server!, Contact HireMee Team", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {

                        seekerName.setVisibility(View.VISIBLE);
                        imgStatusImage.setVisibility(View.VISIBLE);
                        imgStatusImage.setImageResource(R.drawable.cancel_two);

                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "do you want to scan again?", Snackbar.LENGTH_LONG)
                                .setAction("Scan Now!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();
                                    }
                                });

                        snackbar.setDuration(15000);
                        snackbar.setActionTextColor(Color.WHITE);

                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(getResources().getColor(R.color.green));
                        snackbar.show();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                        seekerID.setVisibility(View.GONE);
                        seekerExam.setVisibility(View.GONE);
                        userImage.setVisibility(View.GONE);
                        failureInfo.setVisibility(View.GONE);

                        imgStatusImage.setVisibility(View.VISIBLE);
                        seekerStatus.setVisibility(View.VISIBLE);

                        imgStatusImage.setImageResource(R.drawable.cancel_two);
                        seekerStatus.setText("Authenticated failed.");

                        seekerName.setText("");
                        seekerExam.setText("");
                        seekerID.setText("");

                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "do you want to scan again?", Snackbar.LENGTH_LONG)
                                .setAction("Scan Now!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();
                                    }
                                });

                        snackbar.setDuration(15000);
                        snackbar.setActionTextColor(Color.WHITE);

                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(getResources().getColor(R.color.green));
                        snackbar.show();

                    } else if (error instanceof AuthFailureError)

                    {
                        new android.support.v7.app.AlertDialog.Builder(HallTicket_UserFindActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Alert")
                                .setMessage("Authentication failed. Click yes to scan QR code again!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //setContentView(R.layout.activity_main);
                                        Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();

                                    }

                                })

                                .show();
                    } else if (error instanceof ServerError)

                    {
                        new android.support.v7.app.AlertDialog.Builder(HallTicket_UserFindActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Alert")
                                .setMessage("Incorrect URL. Click yes to re-enter URL!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, MainActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();
                                    }

                                })

                                .show();
                    } else if (error instanceof NetworkError)

                    {
                        new android.support.v7.app.AlertDialog.Builder(HallTicket_UserFindActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Alert")
                                .setMessage("Network error. Click yes to scan QR code again!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();

                                    }

                                })

                                .show();
                    } else if (error instanceof ParseError)

                    {
                        new android.support.v7.app.AlertDialog.Builder(HallTicket_UserFindActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Alert")
                                .setMessage("Parse error. Click yes to scan QR code again!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();

                                    }

                                })

                                .show();
                    } else {

                        seekerID.setVisibility(View.GONE);
                        seekerExam.setVisibility(View.GONE);
                        userImage.setVisibility(View.GONE);
                        failureInfo.setVisibility(View.GONE);

                        imgStatusImage.setVisibility(View.VISIBLE);
                        seekerStatus.setVisibility(View.VISIBLE);
                        imgStatusImage.setImageResource(R.drawable.cancel_two);
                        seekerStatus.setText("Authenticated failed.");

                        seekerName.setText("");
                        seekerExam.setText("");
                        seekerID.setText("");


                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "do you want to scan again?", Snackbar.LENGTH_LONG)
                                .setAction("Scan Now!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent QRScanAgain = new Intent(HallTicket_UserFindActivity.this, HallTicketQRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();
                                    }
                                });

                        snackbar.setDuration(15000);
                        snackbar.setActionTextColor(Color.WHITE);

                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(getResources().getColor(R.color.green));
                        snackbar.show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("HireMeeHallTicketInput", strScannedValue);
                    params.put("SSKey", "YtT76LUJovG6JDJuOCCs5w==");
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    String token = URL_Preferences.getString("Token", "");
                   // params.put("Content-Type", "application/json");
                    params.put("Authorization", "Bearer "+ token);
                    return params;
                }


            };
            codeAvailableRequest.setRetryPolicy(new DefaultRetryPolicy(0, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(getApplicationContext()).add(codeAvailableRequest);


        } else {
            Intent intent = new Intent(HallTicket_UserFindActivity.this, InternetConnectivity.class);
            intent.putExtra("INTERNET_KEY", "MAINACTIVITY");
            startActivity(intent);
            finish();
        }
    }


    public void MakeProfilePictureCall(String scannedCode) {
        final String scannedValue = scannedCode;
        if (CheckNetwork.isInternetAvailable(HallTicket_UserFindActivity.this)) {
            //customProgressDialogTwo.show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                //HighEndDevices
                strDeviceMAC_Address = get_Above_M_WifiMacAddress();

            } else {

                //LowEndDevices
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                strDeviceMAC_Address = wInfo.getMacAddress();

            }

            try {

                StringRequest postRequest = new StringRequest(Request.Method.POST, Config.image, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  customProgressDialogTwo.dismiss();
                        try {
                            Log.d("getFamilyMembersList", response);

                            JSONObject response1 = new JSONObject(response);
                            String success = response1.getString("code");
                            Log.d("MainActivity", "Success code-->" + success);

                            if (success.equals("200")) {

                                String ImageResult = response1.getString("URL");
                                Log.d("MainActivity", "userImage-->" + ImageResult);

                                if (!ImageResult.equals("")) {
                                    Picasso.with(HallTicket_UserFindActivity.this)
                                            .load(ImageResult)
                                            .placeholder(R.drawable.my_progress)    // optional
                                            .error(R.drawable.candidate)               // optional
                                            .resize(300, 300)                       // optional
                                            .into(userImage);
                                } else {
                                    userImage.setBackgroundResource(R.drawable.candidate);
                                }

                            } else if (success.equals("203")) {

                                Toast.makeText(HallTicket_UserFindActivity.this, response1.getString("message"), Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(HallTicket_UserFindActivity.this, response1.getString("message"), Toast.LENGTH_LONG).show();
                            }

                            CallScanCount("success");

                        } catch (JSONException e) {
                            userImage.setBackgroundResource(R.drawable.candidate);
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                customProgressDialogTwo.dismiss();
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(HallTicket_UserFindActivity.this, "Network Busy!", Toast.LENGTH_LONG).show();

                                } else if (error instanceof AuthFailureError) {
                                    Toast.makeText(HallTicket_UserFindActivity.this, "AuthFailureError  ", Toast.LENGTH_LONG).show();

                                } else if (error instanceof ServerError) {
                                    Toast.makeText(HallTicket_UserFindActivity.this, "ServerError  ", Toast.LENGTH_LONG).show();

                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(HallTicket_UserFindActivity.this, "NetworkError  ", Toast.LENGTH_LONG).show();

                                } else if (error instanceof ParseError) {
                                    Toast.makeText(HallTicket_UserFindActivity.this, "ParseError  ", Toast.LENGTH_LONG).show();

                                } else {
                                    error.printStackTrace();
                                }
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();

                        //params.put("webtoken", Config.HM_WebToken);

                        Log.d("HallTicket","ScannedHIremeeID-->"+scannedValue);

                        params.put("hiremee_id", scannedValue);
                        params.put("device_id", strDeviceMAC_Address);

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        String token = URL_Preferences.getString("Token", "");
                      //  params.put("Content-Type", "application/x-www-form-urlencoded");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };

                postRequest.setRetryPolicy(new DefaultRetryPolicy(0, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(this).add(postRequest);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HallTicket_UserFindActivity.this, "No response from server!, Contact HireMee Team", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(HallTicket_UserFindActivity.this, "No Internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void CallScanCount(final String ScanFlag) {

        if (CheckNetwork.isInternetAvailable(HallTicket_UserFindActivity.this)) {
            StringRequest codeAvailableRequest = new StringRequest(Request.Method.POST, Config.ScannerCount, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("HM_Scan_UserFindAc", "Response VAlue scan count-->" + response);
                    try {

                        JSONObject fullResponse = new JSONObject(response);

                        String scan_count = fullResponse.getString("scan_count");
                        if (scan_count == null || scan_count.equals("")) {
                            scan_count = "0";
                        }
                        SharedPreferences.Editor editor = URL_Preferences.edit();
                        editor.putString("scan_count", scan_count);
                        editor.apply();
                        editor.commit();

                        scanCount = URL_Preferences.getString("scan_count", "0");
                        if (scanCount == null || scanCount.equals("")) {
                            scanCount = "0";
                        }

                        animateTextView(0, Integer.parseInt(scanCount), tv_scan_count);

/*
                        if (strMapStatus.equals("")) {


                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "do you want to scan again?", Snackbar.LENGTH_LONG)
                                    .setAction("Scan Now!", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent QRScanAgain = new Intent(HM_ID_UserFindActivity.this, HM_ID_QRScanningActivity.class);
                                            startActivity(QRScanAgain);
                                            finish();
                                        }
                                    });

                            snackbar.setDuration(15000);
                            snackbar.setActionTextColor(Color.WHITE);

                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(getResources().getColor(R.color.green));
                            snackbar.show();


                            Log.d(TAG, "No value returned from MainActivity");


                        }  else {


                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "do you want to scan again?", Snackbar.LENGTH_LONG)
                                    .setAction("Scan Now!", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent QRScanAgain = new Intent(HM_ID_UserFindActivity.this, HM_ID_QRScanningActivity.class);
                                            startActivity(QRScanAgain);
                                            finish();
                                        }
                                    });

                            snackbar.setDuration(15000);
                            snackbar.setActionTextColor(Color.WHITE);

                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(getResources().getColor(R.color.green));
                            snackbar.show();
                        }*/


                    } catch (JSONException e) {
                        Toast.makeText(HallTicket_UserFindActivity.this, "No response from server!, Contact HireMee Team", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(HallTicket_UserFindActivity.this, "Network Busy!", Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(HallTicket_UserFindActivity.this, "AuthFailureError  ", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(HallTicket_UserFindActivity.this, "ServerError  ", Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(HallTicket_UserFindActivity.this, "NetworkError  ", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(HallTicket_UserFindActivity.this, "ParseError  ", Toast.LENGTH_LONG).show();
                    } else {
                        error.printStackTrace();
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    Log.d("hm_ID", "This_hiremeeID-->" + strResult);

                   // params.put("webtoken", Config.HM_WebToken);
                    params.put("hiremee_id", strResult);
                    params.put("device_id", strDeviceMAC_Address);
                    params.put("mapping_status", ScanFlag);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    String token = URL_Preferences.getString("Token", "");
                    //  params.put("Content-Type", "application/x-www-form-urlencoded");
                    params.put("Authorization", "Bearer " + token);
                    return params;
                }
            };
            codeAvailableRequest.setRetryPolicy(new DefaultRetryPolicy(0, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(getApplicationContext()).add(codeAvailableRequest);


        } else {

            Intent intent = new Intent(HallTicket_UserFindActivity.this, InternetConnectivity.class);
            intent.putExtra("INTERNET_KEY", "MAINACTIVITY");
            startActivity(intent);
            finish();
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
            ex.printStackTrace();
        }
        return "";
    }

    public void animateTextView(int initialValue, int finalValue, final TextView textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(800);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText("You have scanned : " + valueAnimator.getAnimatedValue().toString() + " candidates");

            }
        });
        valueAnimator.start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
