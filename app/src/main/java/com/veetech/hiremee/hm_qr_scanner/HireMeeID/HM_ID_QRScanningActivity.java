package com.veetech.hiremee.hm_qr_scanner.HireMeeID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.veetech.hiremee.hm_qr_scanner.HallTicket.HallTicketQRScanningActivity;
import com.veetech.hiremee.hm_qr_scanner.HallTicket.HallTicket_UserFindActivity;
import com.veetech.hiremee.hm_qr_scanner.HallTicket.MainActivity;
import com.veetech.hiremee.hm_qr_scanner.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by SUDV2E08542 on 11/4/2017.
 */

public class HM_ID_QRScanningActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static String TAG = HM_ID_QRScanningActivity.class.getSimpleName();
    private ZXingScannerView mScannerView;

    private MediaPlayer mp;
    private String strResultValue;

    private SharedPreferences ExamCenterPreferences;
    private String selectedExamCenter;
    ImageView imgSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        ExamCenterPreferences = getSharedPreferences("HireMeeExamCenter", Context.MODE_PRIVATE);
        selectedExamCenter = ExamCenterPreferences.getString("ExamCenter", "");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_customize_menu);
        TextView title = (TextView) findViewById(getResources().getIdentifier("title_text", "id", getPackageName()));
        title.setText(selectedExamCenter);
        imgSettings = (ImageView) findViewById(getResources().getIdentifier("imgv_settings", "id", getPackageName()));
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(HM_ID_QRScanningActivity.this, imgSettings);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.menu_changeCenter:
                                Intent moveValue = new Intent(HM_ID_QRScanningActivity.this, ExamCenterSelectionActivity.class);
                                startActivity(moveValue);
                                finish();
                                return true;
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });

        mp = MediaPlayer.create(this, R.raw.beep);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {

        Log.d(TAG, "Scanned value-->" + result.getText());
        mp.start();
        mScannerView.stopCamera();
        strResultValue = result.getText();

        String firstTwoTrimmedValue = strResultValue.substring(0,2);


        if(!TextUtils.isEmpty(strResultValue))
        {
            if(!strResultValue.contains("@@@"))
            {
                if(firstTwoTrimmedValue.equals("HC"))
                {
                    Intent passValuesIntent = new Intent(HM_ID_QRScanningActivity.this, HM_ID_UserFindActivity.class);
                    passValuesIntent.putExtra("Scan_Status", "TRUE");
                    passValuesIntent.putExtra("ScannedID", strResultValue);
                    startActivity(passValuesIntent);
                    finish();

                }else{

                    Toast.makeText(HM_ID_QRScanningActivity.this, "Hiremee Hall ticket scanning not allowed here!", Toast.LENGTH_LONG).show();
                    Intent moveValue = new Intent(HM_ID_QRScanningActivity.this, HM_ID_UserFindActivity.class);
                    moveValue.putExtra("Scan_Status", "INVALID");
                    startActivity(moveValue);
                    finish();
                }


            }else{
                Toast.makeText(HM_ID_QRScanningActivity.this, "Hiremee Hall ticket scanning not allowed here!", Toast.LENGTH_LONG).show();
                Intent moveValue = new Intent(HM_ID_QRScanningActivity.this, HM_ID_UserFindActivity.class);
                moveValue.putExtra("Scan_Status", "HALLTICKET");
                startActivity(moveValue);
                finish();
            }


        }else{
            Toast.makeText(HM_ID_QRScanningActivity.this, "Scan the Valid QR Code!", Toast.LENGTH_LONG).show();
            Intent moveValue = new Intent(HM_ID_QRScanningActivity.this, HM_ID_UserFindActivity.class);
            moveValue.putExtra("Scan_Status", "EMPTY");
            startActivity(moveValue);
            finish();
        }


/*
        if (strResultValue.contains("@@@")) {

            Toast.makeText(HM_ID_QRScanningActivity.this, "Hiremee Hall ticket scanning not allowed here!", Toast.LENGTH_LONG).show();
            Intent moveValue = new Intent(HM_ID_QRScanningActivity.this, HM_ID_UserFindActivity.class);
            moveValue.putExtra("Scan_Status", "FALSE");
            startActivity(moveValue);
            finish();

        } else if (!TextUtils.isEmpty(strResultValue)) {


            Intent passValuesIntent = new Intent(HM_ID_QRScanningActivity.this, HM_ID_UserFindActivity.class);
            passValuesIntent.putExtra("Scan_Status", "TRUE");
            passValuesIntent.putExtra("ScannedID", strResultValue);
            startActivity(passValuesIntent);
            finish();


        }

        else {
            Toast.makeText(HM_ID_QRScanningActivity.this, "Scan Valid QR code", Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }
}
