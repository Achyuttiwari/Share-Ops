package com.example.share_ops;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity implements ActivityCompat {

    int PERMISSION_REQUEST_INTERNET = 0;
    View mLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_INTERNET) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Internet_permission_granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission request was denied.
                Toast.makeText(this, "Internet_permission_denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void onPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Internet_Permission_granted", Toast.LENGTH_SHORT).show();
        } else {
            // Permission is missing and must be requested.
            requestInternetPermission();
        }
    }

    private void requestInternetPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.INTERNET)) {
            Snackbar.make(mLayout, "Internet access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.INTERNET},
                            PERMISSION_REQUEST_INTERNET);
                }
            }).show();
        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_INTERNET);
        }
    }

    public void onSender(View view) {

        Intent i = new Intent(this, FileChooser.class);
        startActivity(i);
    }

    public void onReceiver(View view) {
        Intent i = new Intent(this, MainActivity_receiver.class);
        startActivity(i);
    }
}
