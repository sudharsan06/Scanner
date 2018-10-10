package com.veetech.hiremee.hm_qr_scanner.HallTicket;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.zxing.Result;
import com.veetech.hiremee.hm_qr_scanner.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class HallTicketQRScanningActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static String TAG = HallTicketQRScanningActivity.class.getSimpleName();
    private ZXingScannerView mScannerView;
    private MediaPlayer mp;
    private String strResultValue;
    private String ScannedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_customized);
        TextView title = (TextView) findViewById(getResources().getIdentifier("title_text", "id", getPackageName()));
        title.setText("HireMee QR Scanner");

        mp = MediaPlayer.create(this, R.raw.beep);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }


    @Override
    public void handleResult(Result result) {

        Log.d("HallTicketActivity", "Scanned value-->" + result.getText());
        mp.start();
        mScannerView.stopCamera();
        strResultValue = result.getText();
        String[] output = strResultValue.split("@@@");
        ScannedID = output[0];
        Log.d("Main Activity", "splited value-->" + ScannedID);
        // ScannedID_value = StringUtils.s(strResultValue, ".");
        Log.d("MainActivity", "Scanned Result---->" + strResultValue);
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(strResultValue);
        boolean b = m.find();

        if (b) {
            if (strResultValue.contains("@@@")) {
                CheckHireMeeID(strResultValue, ScannedID);
            } else {
                Intent moveValue = new Intent(HallTicketQRScanningActivity.this, HallTicket_UserFindActivity.class);
                moveValue.putExtra("Scan_Status", "FALSE");
                startActivity(moveValue);
                finish();
            }
        } else {
            Intent moveValue = new Intent(HallTicketQRScanningActivity.this, HallTicket_UserFindActivity.class);
            moveValue.putExtra("Scan_Status", "FALSE");
            startActivity(moveValue);
            finish();
        }
    }


    public void CheckHireMeeID(final String resultCode, String ScannedID) {
        mp.start();
        // Toast.makeText(HallTicketQRScanningActivity.this, "ScannedValue   " + resultCode + "\nScannedID" + ScannedID, Toast.LENGTH_SHORT).show();
        Intent passValuesIntent = new Intent(HallTicketQRScanningActivity.this, HallTicket_UserFindActivity.class);
        passValuesIntent.putExtra("Scan_Status", "TRUE");
        passValuesIntent.putExtra("ScannedValue", resultCode);
        passValuesIntent.putExtra("ScannedID", ScannedID);
        startActivity(passValuesIntent);
        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
