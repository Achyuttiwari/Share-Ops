package com.example.wifishareit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity_sender extends AppCompatActivity{
    ServerSocket serverSocket;
    Socket sSocket;
    int SERVERPORT = 2935;
    Handler handler;

    public final static int QRcodeWidth = 500 ;
    int PERMISSION_REQUEST_CODE = 1;
    Bitmap bitmap ;

    TextView listenText;
    TextView serverStatus;
    ImageView img_QR;

    String filePath;
    String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sender);

        Intent fileIntent = getIntent();
        filePath = fileIntent.getStringExtra("path");

        Toast.makeText(this,filePath,Toast.LENGTH_SHORT).show();

        img_QR = (ImageView)findViewById(R.id.imageView_QR);

        listenText = (TextView)findViewById(R.id.text_listen);
        listenText.setText("Not Listening");
        serverStatus = (TextView)findViewById(R.id.text_serverStatus);
        serverStatus.setText("Disconnected");

        handler = new Handler();
    }

}