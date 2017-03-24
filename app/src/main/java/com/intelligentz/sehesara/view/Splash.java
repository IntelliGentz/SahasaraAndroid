package com.intelligentz.sehesara.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.intelligentz.sehesara.R;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Context context;
    boolean timeout;
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
            goToNextActivity();
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
            goToNextActivity();
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
}
