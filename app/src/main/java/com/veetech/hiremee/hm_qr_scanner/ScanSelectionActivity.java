package com.veetech.hiremee.hm_qr_scanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.veetech.hiremee.hm_qr_scanner.HallTicket.MainActivity;
import com.veetech.hiremee.hm_qr_scanner.HireMeeID.ExamCenterSelectionActivity;
import com.veetech.hiremee.hm_qr_scanner.HireMeeID.HM_ID_QRScanningActivity;

public class ScanSelectionActivity extends AppCompatActivity {

    private Spinner spn_scantypeSelection;
    SharedPreferences ExamCenterPreferences;
    String selectedExamCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_selection);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_customized);

        TextView title = (TextView) findViewById(getResources().getIdentifier("title_text", "id", getPackageName()));
        title.setText("HireMee QR Scanner");
        spn_scantypeSelection = (Spinner) findViewById(R.id.spn_scantype);

        spn_scantypeSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spn_scantypeSelection.setSelection(0);

                switch (position) {

                    case 1:
                        Intent intentScanHallTicket = new Intent(ScanSelectionActivity.this, MainActivity.class);
                        startActivity(intentScanHallTicket);

                        break;
                    case 2:

                        ExamCenterPreferences = getSharedPreferences("HireMeeExamCenter", Context.MODE_PRIVATE);
                        selectedExamCenter = ExamCenterPreferences.getString("ExamCenter", "");
                        Log.d("ScanSelectionActivity", "SelectedExamCenter--->" + selectedExamCenter);


                        if (selectedExamCenter != null && selectedExamCenter != "") {
                            Intent intentScanHiremeeID = new Intent(ScanSelectionActivity.this, HM_ID_QRScanningActivity.class);
                            startActivity(intentScanHiremeeID);
                            //finish();
                        } else {
                            Intent intentScanHiremeeID = new Intent(ScanSelectionActivity.this, ExamCenterSelectionActivity.class);
                            startActivity(intentScanHiremeeID);
                            // finish();
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        new AlertDialog.Builder(ScanSelectionActivity.this)

                .setTitle("Exit App")
                .setMessage("Are you sure you want to Exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ExamCenterPreferences = getSharedPreferences("HireMeeExamCenter", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = ExamCenterPreferences.edit();
                        editor.clear();
                        editor.apply();
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
