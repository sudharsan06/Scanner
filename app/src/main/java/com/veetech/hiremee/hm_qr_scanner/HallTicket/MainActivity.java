package com.veetech.hiremee.hm_qr_scanner.HallTicket;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.veetech.hiremee.hm_qr_scanner.AppUtilities.CheckNetwork;
import com.veetech.hiremee.hm_qr_scanner.AppUtilities.InternetConnectivity;
import com.veetech.hiremee.hm_qr_scanner.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity  {


    public static String TAG = MainActivity.class.getSimpleName();

    private static final int MY_PERMISSIONS_REQUEST = 29;

    String  str_saved_URL;
    private EditText Edt_URL;

    boolean soundValue;

    SharedPreferences URL_Preferences;


    //TODO Future Use
    //ClipboardManager clipboard;
    ClipData clip;
    private boolean soundEnable = true;
    SharedPreferences SoundPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_customized);
        TextView title = (TextView) findViewById(getResources().getIdentifier("title_text", "id", getPackageName()));
        title.setText("HireMee QR Scanner");

        checkIfAndroidM();


        Edt_URL = (EditText) findViewById(R.id.edt_URL);

        //clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        URL_Preferences = getSharedPreferences("HireMee_Scan_URL_Preference", Context.MODE_PRIVATE);
        SoundPreferences = getSharedPreferences("HireMee_Scan_Sound", Context.MODE_PRIVATE);
        soundValue = SoundPreferences.getBoolean("Sound_Mode", false);
        Log.d(TAG, "Sound Enable value in Sound MOde==>" + soundValue);


        //SoundMode();
        //mp = MediaPlayer.create(this, R.raw.beep);

        str_saved_URL = URL_Preferences.getString("ScanningURL", "");
        if (str_saved_URL != null && !str_saved_URL.equals("")) {
            Edt_URL.setText(str_saved_URL);
            //Edt_URL.setText("http://52.76.103.21:8083/api/hmscanner/validatehiremeeid");
        } else {
            Edt_URL.setText("");
        }


        //TODO ** for disable copy and paste option
        //TODO ** Temporarily removed as per testing team suggestion
   /*     Edt_URL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                clip= ClipData.newPlainText("", "");
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });*/
    }


    //TODO ** For future use.
    //TODO ** Using preference to enable and disable sound mode

   /* private void SoundMode() {
        //Log.d(TAG,"Sound Enable value in Sound MOde==>"+URL_Preferences.getBoolean("Sound_Mode",false));
        if (soundValue) {
            mp = MediaPlayer.create(this, R.raw.beep);
        } else {
            Toast.makeText(MainActivity.this, "No Sound", Toast.LENGTH_SHORT).show();
        }

        *//*}else{
            SharedPreferences.Editor editor = URL_Preferences.edit();
            editor.putString("Sound_Mode", "true");
            editor.apply();
            editor.commit();
        }*//*

    }*/


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


    //TODO ** Initiate QR Scanning
    public void QrScanner(View view) {
       // hideKeyboard();
        if (CheckNetwork.isInternetAvailable(MainActivity.this)) {
            //TODO ** Validate proper URL entered by the user
            String enteredURL = Edt_URL.getText().toString();
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(enteredURL);
            boolean b = m.find();

            if ((enteredURL.equals("")) || (!b) || (enteredURL.length() < 10)) {
                Edt_URL.setError("Incorrect URL, Try Again");
            } else {

                SharedPreferences.Editor editor = URL_Preferences.edit();
                editor.putString("ScanningURL", Edt_URL.getText().toString());
                editor.apply();
                editor.commit();

                Intent intent = new Intent(MainActivity.this, HallTicketQRScanningActivity.class);
                intent.putExtra("INTERNET_KEY", "MAINACTIVITY");
                startActivity(intent);


            }
        } else {
            Intent intent = new Intent(MainActivity.this, InternetConnectivity.class);
            intent.putExtra("INTERNET_KEY", "MAINACTIVITY");
            startActivity(intent);
            finish();
        }

    }

    //TODO ** For future usuage
    //TODO ** For maintain menu option to enable and disable sound and copy/paste option

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        if(soundValue==true)
        {
            menu.findItem(R.id.sound_control).setIcon(R.drawable.cancel_two);
            menu.findItem(R.id.sound_control).setTitle("Disable Sound");
        }else if(soundValue==false ){
            menu.findItem(R.id.sound_control).setIcon(R.drawable.tick_one);
            menu.findItem(R.id.sound_control).setTitle("Enable Sound");
        }
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        *//*       if(soundValue=true)
        {
            item.setIcon(R.drawable.cancel_two);
            item.setTitle("Disable");
        }else{
            item.setIcon(R.drawable.tick_one);
            item.setTitle("Enable");
        }*//*

       switch (item.getItemId()) {
            case R.id.myswitch:
                break;
            case R.id.sound_control:
                Log.d(TAG, "Sound Enable value in Item click ==>" + soundValue);
                if (soundValue) {

                    item.setTitle("Disable Sound");
                    item.setIcon(R.drawable.cancel_two);

                    SoundPreferences.edit().putBoolean("Sound_Mode", false).apply();
                    soundValue = SoundPreferences.getBoolean("Sound_Mode", false);
                    Log.d(TAG, "Sound Enable value in Item switch ==>" + soundValue);
                } else {

                    item.setTitle("Enable Sound");
                    item.setIcon(R.drawable.tick_one);

                    SoundPreferences.edit().putBoolean("Sound_Mode", true).apply();
                    soundValue = SoundPreferences.getBoolean("Sound_Mode", false);
                    Log.d(TAG, "Sound Enable value in Item switch ==>" + soundValue);
                }

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/


/*    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        new AlertDialog.Builder(MainActivity.this)

                .setTitle("Exit App")
                .setMessage("Are you sure you want to Exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }*/

}
