package com.veetech.hiremee.hm_qr_scanner.HireMeeID;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
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
import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.picasso.Picasso;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.CheckNetwork;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.Config;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.CustomProgressDialog;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.InternetConnectivity;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.ProgressGenerator;
import com.veetech.hiremee.hm_qr_scanner.HallTicket.MainActivity;
import com.veetech.hiremee.hm_qr_scanner.R;
import com.veetech.hiremee.hm_qr_scanner.ScanSelectionActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HM_ID_UserFindActivity extends AppCompatActivity implements ProgressGenerator.OnCompleteListener {
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences ExamCenterPreferences, URL_Preferences;
    private String selectedExamCenter, scanCount;
    private int selectedCenterID;
    private ImageView imgBack;
    private String strDeviceMAC_Address;
    private String strScanStatus, strScannedID;
    private CustomProgressDialog customProgressDialogTwo;
    //private Button btnMapExam;

    String TAG = HM_ID_UserFindActivity.class.getSimpleName();
    TextView seekerID, seekerStatus, failureInfo, seekerName, tv_scan_count;
    ImageView imgStatusImage;
    String UserHireMeeID, strUserCode, strMapStatus, UserName, Examname;
    String strStatus, strHireeMeID, strHireMeeName, strHireMeeURL;
    ActionProcessButton btnMap;
    ProgressGenerator progressGenerator;
    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";
    private ImageView userImage;


   /* @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            tv_scan_count.startAnimation(AnimationUtils.loadAnimation(HM_ID_UserFindActivity.this,
                    android.R.anim.slide_in_left|android.R.anim.fade_in));
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hm__id__user_find);

        URL_Preferences = getSharedPreferences("HireMee_Scan_URL_Preference", Context.MODE_PRIVATE);
        scanCount = URL_Preferences.getString("scan_count", "");

        ExamCenterPreferences = getSharedPreferences("HireMeeExamCenter", Context.MODE_PRIVATE);
        selectedExamCenter = ExamCenterPreferences.getString("ExamCenter", "");
        selectedCenterID = ExamCenterPreferences.getInt("CenterID", -1);

        Log.d(TAG, "Exam Center--->" + selectedExamCenter);
        Log.d(TAG, "selected center id-->" + selectedCenterID);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_customize_back);

        TextView title = (TextView) findViewById(getResources().getIdentifier("title_text", "id", getPackageName()));
        title.setText(selectedExamCenter);
        imgBack = (ImageView) findViewById(getResources().getIdentifier("imgv_settings", "id", getPackageName()));
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent moveValue = new Intent(HM_ID_UserFindActivity.this, ScanSelectionActivity.class);
                moveValue.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(moveValue);
                finish();
            }

        });
        progressGenerator = new ProgressGenerator(this);
        btnMap = (ActionProcessButton) findViewById(R.id.btnSignIn);
        seekerName = (TextView) findViewById(R.id.tv_seekerName);
        seekerID = (TextView) findViewById(R.id.tv_userID);
        seekerStatus = (TextView) findViewById(R.id.tv_status);
        //  seekerExam = (TextView) findViewById(R.id.tv_userExam);
        failureInfo = (TextView) findViewById(R.id.tv_failure_info);
        tv_scan_count = (TextView) findViewById(R.id.tv_scan_count);
        imgStatusImage = (ImageView) findViewById(R.id.imageView);
        userImage = (ImageView) findViewById(R.id.userImage);

//sudha
        if (scanCount == null || scanCount.equals("")) {
            scanCount = "0";
        }
        animateTextView(0, Integer.parseInt(scanCount), tv_scan_count);

        customProgressDialogTwo = new CustomProgressDialog(HM_ID_UserFindActivity.this, R.drawable.loadingicon);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);
        //btnMapExam = (Button) findViewById(R.id.btn_MapExam);
        Intent userScanResult = getIntent();

        if (userScanResult.hasExtra("Scan_Status")) {
            strScanStatus = userScanResult.getExtras().getString("Scan_Status");
            if (strScanStatus.equals("TRUE")) {

                strScannedID = userScanResult.getExtras().getString("ScannedID");
                callToValidateUser();

            } else if (strScanStatus.equals("INVALID")) {

                seekerID.setVisibility(View.GONE);
                failureInfo.setVisibility(View.GONE);

                imgStatusImage.setVisibility(View.VISIBLE);
                seekerStatus.setVisibility(View.VISIBLE);

                imgStatusImage.setImageResource(R.drawable.cancel_two);
                seekerStatus.setText("Invalid QR Code, If it is valid scan again!");
                seekerID.setText("");

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

            } else if (strScanStatus.equals("HALLTICKET")) {
                seekerID.setVisibility(View.GONE);
                failureInfo.setVisibility(View.GONE);

                imgStatusImage.setVisibility(View.VISIBLE);
                seekerStatus.setVisibility(View.VISIBLE);

                imgStatusImage.setImageResource(R.drawable.cancel_two);
                seekerStatus.setText("You have scanned college campus hallticket, Scan with mobile app QR code");
                seekerStatus.setTextSize(18);
                seekerID.setText("");

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
            } else {
                //  seekerName.setVisibility(View.GONE);
                seekerID.setVisibility(View.GONE);
                //  seekerExam.setVisibility(View.GONE);
                //  userImage.setVisibility(View.GONE);
                failureInfo.setVisibility(View.GONE);
                imgStatusImage.setVisibility(View.VISIBLE);
                seekerStatus.setVisibility(View.VISIBLE);
                imgStatusImage.setImageResource(R.drawable.cancel_two);
                seekerStatus.setText("Improper Scanning, If it is valid scan again!");

                //   seekerName.setText("");
                //   seekerExam.setText("");
                seekerID.setText("");

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

            }
        }

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // progressGenerator.start(btnMap);
                btnMap.setProgress(0);
                callMappingURL();

                btnMap.setEnabled(false);

            }
        });

   /*     btnMapExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //callMappingURL();
                btnMapExam.setVisibility(View.GONE);
            }
        });*/


    }

    private void callToValidateUser() {

        if (CheckNetwork.isInternetAvailable(HM_ID_UserFindActivity.this)) {

            customProgressDialogTwo.show();

            StringRequest codeAvailableRequest = new StringRequest(Request.Method.POST, Config.UserAuthUsingID, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    MakeProfilePictureCall();
                    customProgressDialogTwo.dismiss();

                    Log.d("MainActivity", "Auth VAlue-->" + response);
                    try {
                        JSONObject fullResponse = new JSONObject(response);

                        //strStatus,strHireeMeID,strHireMeeName,strHireMeeURL;


                        strStatus = fullResponse.getString("code");

                        //animateTextView(0, Integer.parseInt(scanCount),tv_scan_count);

                        if (strStatus.equals("200")) {

                            JSONObject jsonValue = fullResponse.getJSONObject("response");
                            for (int i = 0; i < jsonValue.length(); i++) {

                                //    JSONObject arrayValues = jsonValue.getJSONObject(i);
                                strHireeMeID = jsonValue.getString("hiremee_id");
                                strHireMeeName = jsonValue.getString("Name");
                                strHireMeeURL = jsonValue.getString("URL");


                                seekerID.setVisibility(View.VISIBLE);
                                seekerName.setVisibility(View.VISIBLE);
                                failureInfo.setVisibility(View.GONE);
                                imgStatusImage.setVisibility(View.VISIBLE);
                                seekerStatus.setVisibility(View.VISIBLE);
                                // btnMapExam.setVisibility(View.VISIBLE);
                                btnMap.setVisibility(View.VISIBLE);

                                imgStatusImage.setImageResource(R.drawable.tick_one);
                                seekerID.setText(strHireeMeID);
                                seekerName.setText(strHireMeeName);
                                seekerStatus.setText(fullResponse.getString("message"));

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "do you want to scan next?", Snackbar.LENGTH_LONG)
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

                            }



                           /* JSONArray jsonValue = fullResponse.getJSONArray("response");

                            for (int i = 0; i < jsonValue.length(); i++) {
                                JSONObject arrayValues = jsonValue.getJSONObject(i);
                                strHireeMeID = arrayValues.getString("hiremee_id");
                                strHireMeeName = arrayValues.getString("Name");
                                strHireMeeURL = arrayValues.getString("URL");


                                seekerID.setVisibility(View.VISIBLE);
                                failureInfo.setVisibility(View.GONE);
                                imgStatusImage.setVisibility(View.VISIBLE);
                                seekerStatus.setVisibility(View.VISIBLE);
                                btnMapExam.setVisibility(View.VISIBLE);


                                imgStatusImage.setImageResource(R.drawable.tick_one);
                                seekerID.setText(strHireeMeID);
                                seekerStatus.setText("Authenticated User");

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "do you want to scan next?", Snackbar.LENGTH_LONG)
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


                        } else if (strStatus.equals("401")) {

                            seekerID.setVisibility(View.GONE);
                            failureInfo.setVisibility(View.GONE);
                            imgStatusImage.setVisibility(View.VISIBLE);
                            seekerStatus.setVisibility(View.VISIBLE);
                            // btnMapExam.setVisibility(View.GONE);
                            imgStatusImage.setImageResource(R.drawable.cancel_two);
                            seekerStatus.setText(fullResponse.getString("message"));
                            btnMap.setVisibility(View.GONE);

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
                        } else if (strStatus.equals("203")) {
                            seekerID.setVisibility(View.GONE);
                            failureInfo.setVisibility(View.GONE);
                            imgStatusImage.setVisibility(View.VISIBLE);
                            seekerStatus.setVisibility(View.VISIBLE);
                            // btnMapExam.setVisibility(View.GONE);

                            imgStatusImage.setImageResource(R.drawable.cancel_two);
                            seekerStatus.setText(fullResponse.getString("message"));

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
                        } else {
                            seekerID.setVisibility(View.GONE);
                            failureInfo.setVisibility(View.GONE);
                            imgStatusImage.setVisibility(View.VISIBLE);
                            seekerStatus.setVisibility(View.VISIBLE);
                            //     btnMapExam.setVisibility(View.GONE);

                            imgStatusImage.setImageResource(R.drawable.cancel_two);
                            seekerStatus.setText(fullResponse.getString("message"));

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
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HM_ID_UserFindActivity.this, "No response from server!, Contact HireMee Team", Toast.LENGTH_LONG).show();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "Network Busy!", Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "AuthFailureError  ", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "ServerError  ", Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "NetworkError  ", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "ParseError  ", Toast.LENGTH_LONG).show();
                    } else {
                        error.printStackTrace();
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("hiremee_id", strScannedID);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    String token = URL_Preferences.getString("Token", "");
                    params.put("Authorization", "Bearer " + token);
                    return params;
                }
            };
            codeAvailableRequest.setRetryPolicy(new DefaultRetryPolicy(0, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(getApplicationContext()).add(codeAvailableRequest);


        } else {
            Intent intent = new Intent(HM_ID_UserFindActivity.this, InternetConnectivity.class);
            intent.putExtra("INTERNET_KEY", "MAINACTIVITY");
            startActivity(intent);
            finish();
        }

    }


    public void MakeProfilePictureCall() {

        if (CheckNetwork.isInternetAvailable(HM_ID_UserFindActivity.this)) {
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
                               /* String scan_count = response1.getString("scan_count");
                                if (scan_count == null || scan_count == "") {
                                    scan_count = "0";
                                }
                                SharedPreferences.Editor editor = URL_Preferences.edit();
                                editor.putString("scan_count", scan_count);
                                editor.apply();
                                editor.commit();

                                scanCount = URL_Preferences.getString("scan_count", "0");
                                if (scanCount == null || scanCount == "") {
                                    scanCount = "0";
                                }

                                animateTextView(0, Integer.parseInt(scanCount),tv_scan_count);*/
                                //tv_scan_count.setText("Scan Count: " + scanCount.toString());

                                Log.d("MainActivity", "userImage-->" + ImageResult);

                                if (!ImageResult.equals("")) {
                                    Picasso.with(HM_ID_UserFindActivity.this)
                                            .load(ImageResult)
                                            .placeholder(R.drawable.my_progress)   // optional
                                            .error(R.drawable.candidate)               // optional
                                            .resize(400, 400)                       // optional
                                            .into(userImage);
                                }


                            } else if (success.equals("203")) {

                              /*  String scan_count = response1.getString("scan_count");
                                SharedPreferences.Editor editor = URL_Preferences.edit();
                                editor.putString("scan_count", scan_count);
                                editor.apply();
                                editor.commit();

                                scanCount = URL_Preferences.getString("scan_count", "0");
                                if (scanCount == null || scanCount == "") {
                                    scanCount = "0";
                                }
                                animateTextView(0, Integer.parseInt(scanCount),tv_scan_count);*/
                                //tv_scan_count.setText("Scan Count: " + scanCount.toString());
                                Toast.makeText(HM_ID_UserFindActivity.this, response1.getString("message"), Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(HM_ID_UserFindActivity.this, response1.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(HM_ID_UserFindActivity.this, "No Image loaded from server", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                customProgressDialogTwo.dismiss();
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(HM_ID_UserFindActivity.this, "Network Busy!", Toast.LENGTH_LONG).show();
                                } else if (error instanceof AuthFailureError) {
                                    Toast.makeText(HM_ID_UserFindActivity.this, "AuthFailureError  ", Toast.LENGTH_LONG).show();
                                } else if (error instanceof ServerError) {
                                    Toast.makeText(HM_ID_UserFindActivity.this, "ServerError  ", Toast.LENGTH_LONG).show();
                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(HM_ID_UserFindActivity.this, "NetworkError  ", Toast.LENGTH_LONG).show();
                                } else if (error instanceof ParseError) {
                                    Toast.makeText(HM_ID_UserFindActivity.this, "ParseError  ", Toast.LENGTH_LONG).show();
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

                        params.put("hiremee_id", strScannedID);
                        params.put("device_id", strDeviceMAC_Address);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        String token = URL_Preferences.getString("Token", "");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }

                };

                postRequest.setRetryPolicy(new DefaultRetryPolicy(0, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(this).add(postRequest);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HM_ID_UserFindActivity.this, "No response from server!, Contact HireMee Team", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(HM_ID_UserFindActivity.this, "No Internet connection", Toast.LENGTH_LONG).show();
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


    private void callMappingURL() {
        btnMap.setProgress(20);
        if (CheckNetwork.isInternetAvailable(HM_ID_UserFindActivity.this)) {
            btnMap.setProgress(30);
            //customProgressDialogTwo.show();

            StringRequest codeAvailableRequest = new StringRequest(Request.Method.POST, Config.ExamMapping, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //customProgressDialogTwo.dismiss();
                    btnMap.setProgress(50);
                    Log.d("MainActivity", "Mapping Response VAlue-->" + response);
                    try {


                        JSONObject fullResponse = new JSONObject(response);
                        strMapStatus = fullResponse.getString("Status");
                        UserHireMeeID = fullResponse.getString("HireMeeID");
                        UserName = fullResponse.getString("Name");
                        strUserCode = fullResponse.getString("code");
                        Examname = fullResponse.getString("ExamName");
                        btnMap.setProgress(80);


                        if (strUserCode.equals("203")) {

                            CallScanCount("fail");
                            btnMap.setProgress(100);
                            seekerID.setVisibility(View.GONE);
                            failureInfo.setVisibility(View.GONE);
                            imgStatusImage.setVisibility(View.VISIBLE);
                            seekerStatus.setVisibility(View.VISIBLE);

                            imgStatusImage.setImageResource(R.drawable.cancel_two);
                            seekerStatus.setText("Mapping failed, check the user again!");
                            btnMap.setBackgroundColor(getResources().getColor(R.color.red));
                            btnMap.setText("Mapping Failed.");

                            Toast.makeText(HM_ID_UserFindActivity.this, strMapStatus, Toast.LENGTH_SHORT).show();

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


                        } else if (strUserCode.equals("200")) {
                            CallScanCount("success");
                            btnMap.setProgress(100);
                            seekerID.setVisibility(View.VISIBLE);
                            failureInfo.setVisibility(View.GONE);
                            imgStatusImage.setVisibility(View.VISIBLE);
                            seekerStatus.setVisibility(View.VISIBLE);

                            imgStatusImage.setImageResource(R.drawable.tick_one);
                            seekerID.setText(UserHireMeeID);
                            seekerStatus.setText(strMapStatus);
                            btnMap.setBackgroundColor(getResources().getColor(R.color.green));
                            btnMap.setText("Mapped successfully.");


                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "do you want to scan next?", Snackbar.LENGTH_LONG)
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


                        } else if (strUserCode.equals("201")) {
                            // CallScanCount("fail");
                            btnMap.setProgress(100);
                            seekerID.setVisibility(View.GONE);
                            failureInfo.setVisibility(View.GONE);

                            imgStatusImage.setVisibility(View.VISIBLE);
                            seekerStatus.setVisibility(View.VISIBLE);

                            imgStatusImage.setImageResource(R.drawable.cancel_two);
                            seekerStatus.setText(strMapStatus);
                            btnMap.setBackgroundColor(getResources().getColor(R.color.red));
                            btnMap.setText("Mapping Failed.");
                            Toast.makeText(HM_ID_UserFindActivity.this, strMapStatus, Toast.LENGTH_SHORT).show();
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

                        } else if (strUserCode.equals("202")) {
                            //   CallScanCount("fail");
                            btnMap.setProgress(100);
                            seekerID.setVisibility(View.VISIBLE);
                            failureInfo.setVisibility(View.GONE);
                            imgStatusImage.setVisibility(View.VISIBLE);
                            seekerStatus.setVisibility(View.VISIBLE);

                            imgStatusImage.setImageResource(R.drawable.cancel_two);
                            seekerStatus.setText(strMapStatus);
                            btnMap.setBackgroundColor(getResources().getColor(R.color.button));
                            btnMap.setText("Exam Already Mapped");

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

                        } else {
                            // CallScanCount("fail");
                            btnMap.setProgress(100);
                            seekerID.setVisibility(View.GONE);
                            failureInfo.setVisibility(View.GONE);

                            imgStatusImage.setVisibility(View.VISIBLE);
                            seekerStatus.setVisibility(View.VISIBLE);

                            imgStatusImage.setImageResource(R.drawable.cancel_two);
                            seekerStatus.setText(strMapStatus);
                            btnMap.setBackgroundColor(getResources().getColor(R.color.red));
                            btnMap.setText("Mapping Failed. Invalid data!");

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
                            Toast.makeText(HM_ID_UserFindActivity.this, strMapStatus, Toast.LENGTH_SHORT).show();
                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(getResources().getColor(R.color.green));
                            snackbar.show();
                        }


                    } catch (JSONException e) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "No response from server!, Contact HireMee Team", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        new android.support.v7.app.AlertDialog.Builder(HM_ID_UserFindActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Alert")
                                .setMessage("Server Timeout! Click yes to scan QR code again!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent QRScanAgain = new Intent(HM_ID_UserFindActivity.this, HM_ID_QRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();

                                    }

                                })

                                .show();
                    } else if (error instanceof AuthFailureError)

                    {
                        new android.support.v7.app.AlertDialog.Builder(HM_ID_UserFindActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Alert")
                                .setMessage("Mapping failed. Click yes to scan QR code again!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent QRScanAgain = new Intent(HM_ID_UserFindActivity.this, HM_ID_QRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();

                                    }

                                })

                                .show();
                    } else if (error instanceof ServerError)

                    {
                        new android.support.v7.app.AlertDialog.Builder(HM_ID_UserFindActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Alert")
                                .setMessage("Incorrect QR Code. Click yes to Scan again!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent QRScanAgain = new Intent(HM_ID_UserFindActivity.this, MainActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();
                                    }

                                })

                                .show();
                    } else if (error instanceof NetworkError)

                    {
                        new android.support.v7.app.AlertDialog.Builder(HM_ID_UserFindActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Alert")
                                .setMessage("Network error. Click yes to scan QR code again!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent QRScanAgain = new Intent(HM_ID_UserFindActivity.this, HM_ID_QRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();

                                    }

                                })

                                .show();
                    } else if (error instanceof ParseError)

                    {
                        new android.support.v7.app.AlertDialog.Builder(HM_ID_UserFindActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Alert")
                                .setMessage("Parse error. Click yes to scan QR code again!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent QRScanAgain = new Intent(HM_ID_UserFindActivity.this, HM_ID_QRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();

                                    }

                                })

                                .show();
                    } else {

                        new android.support.v7.app.AlertDialog.Builder(HM_ID_UserFindActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Alert")
                                .setMessage("Server slow down! Click yes to scan QR code again!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent QRScanAgain = new Intent(HM_ID_UserFindActivity.this, HM_ID_QRScanningActivity.class);
                                        startActivity(QRScanAgain);
                                        finish();

                                    }

                                })

                                .show();
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    Log.d("hm_ID", "This_hiremID-->" + strHireeMeID);
                    Log.d("hm_ID", "This_ExamCentreID-->" + Integer.toString(selectedCenterID));

                    params.put("HireMeeID", strHireeMeID);
                    params.put("ExamCentreID", Integer.toString(selectedCenterID));
                    params.put("SSKey", "YtT76LUJovG6JDJuOCCs5w==");

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
            btnMap.setTextColor(getResources().getColor(R.color.icons));
            btnMap.setProgress(100);
            btnMap.setBackgroundColor(getResources().getColor(R.color.red));

            btnMap.setText("No Internet connectivity");
            Intent intent = new Intent(HM_ID_UserFindActivity.this, InternetConnectivity.class);
            intent.putExtra("INTERNET_KEY", "MAINACTIVITY");
            startActivity(intent);
            finish();
        }
    }

    private void CallScanCount(final String ScanFlag) {

        if (CheckNetwork.isInternetAvailable(HM_ID_UserFindActivity.this)) {
            StringRequest codeAvailableRequest = new StringRequest(Request.Method.POST, Config.ScannerCount, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("HM_Scan_UserFindAc", "Response VAlue-->" + response);
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
                        Toast.makeText(HM_ID_UserFindActivity.this, "No response from server!, Contact HireMee Team", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "Network Busy!", Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "AuthFailureError  ", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "ServerError  ", Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "NetworkError  ", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(HM_ID_UserFindActivity.this, "ParseError  ", Toast.LENGTH_LONG).show();
                    } else {
                        error.printStackTrace();
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    Log.d("hm_ID", "This_hiremID-->" + strHireeMeID);
                    Log.d("hm_ID", "This_ExamCentreID-->" + Integer.toString(selectedCenterID));

                    //params.put("webtoken", Config.HM_WebToken);
                    params.put("hiremee_id", strHireeMeID);
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

            Intent intent = new Intent(HM_ID_UserFindActivity.this, InternetConnectivity.class);
            intent.putExtra("INTERNET_KEY", "MAINACTIVITY");
            startActivity(intent);
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onComplete() {

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


}
