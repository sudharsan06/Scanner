package com.veetech.hiremee.hm_qr_scanner.AppUtilities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.veetech.hiremee.hm_qr_scanner.HallTicket.HallTicket_UserFindActivity;
import com.veetech.hiremee.hm_qr_scanner.HallTicket.MainActivity;
import com.veetech.hiremee.hm_qr_scanner.R;
import com.veetech.hiremee.hm_qr_scanner.SplashScreen;

public class InternetConnectivity extends AppCompatActivity {

    private String Internet_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_connectivity);
        setTitle("Network Problem");

        Intent userCredentialIntent = getIntent();
        if (userCredentialIntent.hasExtra("INTERNET_KEY")) {
            Internet_key = userCredentialIntent.getExtras().getString("INTERNET_KEY");

        }

        Button btn_reconnect = (Button) findViewById(R.id.btn_reconnect);
        btn_reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(InternetConnectivity.this)) {

                    if(Internet_key.equals("SPLASHSCREEN"))
                    {
                        Intent intent = new Intent(InternetConnectivity.this, SplashScreen.class);
                        startActivity(intent);
                        finish();
                    }else if(Internet_key.equals("MAINACTIVITY"))
                    {
                        Intent intent = new Intent(InternetConnectivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else if(Internet_key.equals("USERFINDACTIVITY"))
                    {
                        Intent intent = new Intent(InternetConnectivity.this, HallTicket_UserFindActivity.class);
                        startActivity(intent);
                        finish();
                    }


                }
            }
        });

    }
}
