package com.example.share_ops;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


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
        {

        }
}
}
