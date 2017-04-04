package com.intelligentz.sehesara.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.intelligentz.sehesara.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Splash extends AppCompatActivity {
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Context context;
    boolean timeout;
    boolean onCreateRan = false;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    LocationManager lm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        timeout = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;
        PlayGifView pGif = (PlayGifView) findViewById(R.id.viewGif);
        pGif.setImageResource(R.drawable.loader_2);
        ViewGroup.LayoutParams params = pGif.getLayoutParams();
        params.height = params.height/2;
        params.width = params.width/2;
        pGif.setLayoutParams(params);
        lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                requestPermission();

            }
        }, 3000);

    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        }else {
//            while (System.currentTimeMillis() - startTime < 3000) {
//
//            }
            checkNetworkAndLocation();

        }
    }

    private void goToNextActivity() {
        Intent intent = new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            for (int permisson : grantResults) {
                if (permisson != PackageManager.PERMISSION_GRANTED) {
                    showMessageOKCancel("You need to provide permisson to access GPS location to continue.", null);
                    return;
                }
            }
//            while (System.currentTimeMillis() - startTime < 3000) {
//
//            }
            checkNetworkAndLocation();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Splash.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void checkNetworkAndLocation(){
        lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled || !network_enabled) {
            // notify user

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    onCreateRan = true;
                    //get gps
                    //checkNetworkAndLocation();
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });
            dialog.show();
        }else {
            new CheckInternet().execute();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (onCreateRan) {
                checkNetworkAndLocation();
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onCreateRan) {
            checkNetworkAndLocation();
        }
    }

    class CheckInternet extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            if (network_enabled) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0 ) ? "True" : "False";
                } catch (IOException e) {
                }
            }
            return "False";
        }



        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            if (file_url.equals("True")) {
                goToNextActivity();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage(context.getResources().getString(R.string.mobile_network_not_enabled));
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        checkNetworkAndLocation();
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        finish();
                    }
                });
                dialog.show();
            }
        }
    }
}
