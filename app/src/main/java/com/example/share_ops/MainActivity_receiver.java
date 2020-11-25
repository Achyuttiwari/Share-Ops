package com.example.share_ops;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class MainActivity_receiver extends AppCompatActivity {

    Socket clientSocket;
    String IP = "";
    int SERVERPORT = 8080;
    boolean connected = false;
    boolean sending = false;
    Handler handler;
    IntentIntegrator qrScan;
    int PERMISSION_REQUEST_CODE = 1;
    int PERMISSION_REQUEST_EXTERNAL = 2;

    TextView clientStatus;
    EditText serverIP;
    private byte[] imageByte;

    int filesize = 900000; // filesize temporary hardcoded

    long start = System.currentTimeMillis();
    int bytesRead;
    File file;
    int current = 0;
    Button rec_connect;

    String segments[];
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_receiver);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL);
        }
        handler = new Handler();
        rec_connect = findViewById(R.id.button_connectClient);
        rec_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectServer(v);
            }
        });
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "ShareFIle");
        if (!file.isDirectory()) {
            file.mkdir();
        }
        qrScan = new IntentIntegrator(this);
        serverIP = (EditText) findViewById(R.id.edit_serverIP);
        clientStatus = (TextView) findViewById(R.id.text_clientStatus);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                Toast.makeText(this, "permission already granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PERMISSION_REQUEST_EXTERNAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                Toast.makeText(this, "permission already granted", Toast.LENGTH_LONG).show();

            }
        }
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {

                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    segments = result.getContents().split("/");
                    IP = segments[0];
                    fileName = segments[1];
                    filesize = Byte.parseByte(segments[2]);
                    serverIP.setText(IP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void scanIP(View view) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "permission already granted", Toast.LENGTH_LONG).show();
            qrScan.initiateScan();

        }

    }

    public void connectServer(View view) {
        //IP = serverIP.getText().toString();
        //clientSocket = new Socket(IP, SERVERPORT);
        Log.e("ip is this", "ip" + IP);
        if (IP.equals("0.0.0.0") || IP.equals("")) {
            Toast.makeText(this, "Invalid Sender", Toast.LENGTH_SHORT).show();
            return;
        }
        Thread clientThread = new Thread(new ClientThread(IP, SERVERPORT));
        clientThread.start();
    }

    private class ClientThread extends Thread {
        String dstAddress;
        int dstPort;

        ClientThread(String address, int port) {
            dstAddress = address;
            dstPort = port;
        }

        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(dstAddress, dstPort);

                FileOutputStream fos = new FileOutputStream(new File(file, MainActivity_receiver.this.fileName));
                byte[] mybytearray = new byte[filesize];
                InputStream is = socket.getInputStream();
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bytesRead = is.read(mybytearray, 0, mybytearray.length);
                current = bytesRead;
                do {
                    bytesRead =
                            is.read(mybytearray, current, (mybytearray.length - current));
                    if (bytesRead >= 0) current += bytesRead;
                } while (bytesRead > -1);

                bos.write(mybytearray, 0, current);
                bos.flush();
                long end = System.currentTimeMillis();
                Log.i(" end-start = ", String.valueOf(end - start));
                bos.close();
                socket.close();
                fos.close();


                MainActivity_receiver.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity_receiver.this,
                                "Finished",
                                Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();

                final String eMsg = "Something wrong: " + e.getMessage();
                MainActivity_receiver.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity_receiver.this,
                                eMsg,
                                Toast.LENGTH_LONG).show();
                    }
                });

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
