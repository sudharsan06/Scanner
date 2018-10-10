package com.veetech.hiremee.hm_qr_scanner.HireMeeID;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.CheckNetwork;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.Config;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.CustomProgressDialog;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.InternetConnectivity;
import com.veetech.hiremee.hm_qr_scanner.POJO.ExamCenterPOJO;
import com.veetech.hiremee.hm_qr_scanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExamCenterSelectionActivity extends AppCompatActivity {

    private Spinner spnExamCenter;
    private ArrayList<String> lst_examCenters;
    private ArrayList<String> lst_examCenterID;

    private JSONArray JsonArraycenters;
    private String strCenter;
    private int intCenterID;
    private Button btnExamCenterSelection;
    String strSelectedCenter = "";
    private static final int MY_PERMISSIONS_REQUEST = 29;
    SharedPreferences ExamCenterPreferences;
    SharedPreferences URL_Preferences;
    CustomProgressDialog customProgressDialog;

    private String TAG = ExamCenterSelectionActivity.class.getSimpleName();


    @Override
    protected void onStart() {
        super.onStart();
        getExamCenterData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_center_selection);
        checkIfAndroidM();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_customized);
        TextView title = (TextView) findViewById(getResources().getIdentifier("title_text", "id", getPackageName()));
        title.setText("HireMee QR Scanner");

        ExamCenterPreferences = getSharedPreferences("HireMeeExamCenter", Context.MODE_PRIVATE);
        URL_Preferences = getSharedPreferences("HireMee_Scan_URL_Preference", Context.MODE_PRIVATE);

        customProgressDialog = new CustomProgressDialog(this, R.drawable.loadingicon);

        spnExamCenter = (Spinner) findViewById(R.id.spn_examCenter);
        btnExamCenterSelection = (Button) findViewById(R.id.btn_examCenter);
        lst_examCenters = new ArrayList<>();
        lst_examCenterID = new ArrayList<>();
        getExamCenterData();


        btnExamCenterSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckNetwork.isInternetAvailable(ExamCenterSelectionActivity.this)) {

                    Log.d(TAG, "strSelectedCenter-->" + strSelectedCenter);

                    try {
                        strSelectedCenter = spnExamCenter.getSelectedItem().toString();

                        // if (strSelectedCenter != null && !strSelectedCenter.equals("")) {

                        int positionvalue = spnExamCenter.getSelectedItemPosition();
                        intCenterID = Integer.valueOf(lst_examCenterID.get(positionvalue));

                        if (strSelectedCenter.equals("--Select--") || strSelectedCenter.equals("")) { //-- Pooled campus --
                            Toast.makeText(ExamCenterSelectionActivity.this, "Select valid pooled campus!", Toast.LENGTH_LONG).show();

                        } else {

                            Log.d("ExamCenterActivity", "Selected Exam Center-->" + strSelectedCenter);
                            Log.d("ExamCenterActivity", "Selected Exam Center ID -->" + intCenterID);

                            SharedPreferences.Editor editor = ExamCenterPreferences.edit();
                            editor.putString("ExamCenter", strSelectedCenter);
                            editor.putInt("CenterID", intCenterID);
                            editor.apply();
                            editor.commit();

                            Intent intentScanHiremeeID = new Intent(ExamCenterSelectionActivity.this, HM_ID_QRScanningActivity.class);
                            startActivity(intentScanHiremeeID);
                            finish();
                        }
//                        } else {
//                            Toast.makeText(ExamCenterSelectionActivity.this, "No Response from server, Contact HireMee Team.", Toast.LENGTH_LONG).show();
//                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                        Toast.makeText(ExamCenterSelectionActivity.this, "No Response from server, Contact HireMee Team.", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Intent intent = new Intent(ExamCenterSelectionActivity.this, InternetConnectivity.class);
                    intent.putExtra("INTERNET_KEY", "MAINACTIVITY");
                    startActivity(intent);
                    finish();
                }
            }
        });

      /*  spnExamCenter.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {



                        Toast.makeText(ExamCenterSelectionActivity.this, "Center Name : " + getCenterName(position), Toast.LENGTH_LONG).show();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(ExamCenterSelectionActivity.this, "Nothing selected", Toast.LENGTH_LONG).show();
                    }
                });*/
    }

    private void getExamCenterData() {
        customProgressDialog.show();

        if (CheckNetwork.isInternetAvailable(ExamCenterSelectionActivity.this)) {

            StringRequest ExamCenterRequest = new StringRequest(Request.Method.GET, Config.examCenters,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d(TAG, "exam center Respose-->" + response);

                            customProgressDialog.dismiss();
                            JSONObject jsonCenters = null;
                            try {
                                jsonCenters = new JSONObject(response);
                                JsonArraycenters = jsonCenters.getJSONArray("data");
                                lst_examCenters.clear();

                                for (int i = 0; i < JsonArraycenters.length(); i++) {

                                    JSONObject centerObj = JsonArraycenters.getJSONObject(i);
                                    ExamCenterPOJO pojo = new ExamCenterPOJO();


                                    strCenter = centerObj.getString("ExamCentreName");
                                    intCenterID = centerObj.getInt("ExamCentreID");

                                    pojo.setCenterID(intCenterID);
                                    pojo.setStrCenterName(strCenter);

                                    lst_examCenters.add(strCenter);
                                    lst_examCenterID.add(Integer.toString(intCenterID));

                                }

                                spnExamCenter.setAdapter(new ArrayAdapter<String>(ExamCenterSelectionActivity.this, android.R.layout.simple_spinner_dropdown_item, lst_examCenters));

                            } catch (JSONException e) {
                                Toast.makeText(ExamCenterSelectionActivity.this,"No response from server!, Contact HireMee Team",Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customProgressDialog.dismiss();

                    Log.d(TAG, "Error-->" + error.toString());

                    Toast.makeText(ExamCenterSelectionActivity.this, "Network connection error, contact your system team", Toast.LENGTH_LONG).show();

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    String token = URL_Preferences.getString("Token", "");


                    Log.d("exam","Token-->"+token);
                    //params.put("Content-Type", "application/json; charset=utf-8");
                    params.put("Authorization", "Bearer "+ token);
                    return params;
                }
            };

            ExamCenterRequest.setRetryPolicy(new DefaultRetryPolicy(0, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(ExamCenterSelectionActivity.this).add(ExamCenterRequest);
        } else {
            Intent intent = new Intent(ExamCenterSelectionActivity.this, InternetConnectivity.class);
            intent.putExtra("INTERNET_KEY", "MAINACTIVITY");
            startActivity(intent);
            finish();
        }

    }


    private String getCenterName(int position) {
        String name = "";
        try {

            JSONObject json = JsonArraycenters.getJSONObject(position);
            name = json.getString("ExamCentreName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return name;
    }

    public void checkIfAndroidM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST);
            } else {
                Log.d("Home", "Already granted access");
            }
        }
    }

    //TODO ** Getting Runtime permission on Android higher version phones

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Home", "Permission Granted");

                } else {
                    Log.d("Home", "Permission Failed");
                    Toast.makeText(getApplicationContext(), "You must allow to capture QR Scanning and camera functionality on your mobile device.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
