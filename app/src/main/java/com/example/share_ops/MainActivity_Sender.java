package com.example.share_ops;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jaiselrahman.filepicker.model.MediaFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


public class MainActivity_Sender extends AppCompatActivity{
    ServerSocket serverSocket;
    Socket sSocket;
    int SERVERPORT = 8080;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                //Toast.makeText(this,"permission WIFI_ACCESS",Toast.LENGTH_SHORT).show();
                IP = IPgen();
                Toast.makeText(this,IP,Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public void startServer(View view){
        ipGenerator();
        Thread serverThread = new Thread(new ServerThread());
        serverThread.start();
    }


    public void ipGenerator(){
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PERMISSION_REQUEST_CODE);
            } else {
                //Toast.makeText(this,"permission for WIFI already granted",Toast.LENGTH_SHORT).show();
                IP = IPgen();
                Toast.makeText(this,String.valueOf(IP),Toast.LENGTH_LONG).show();
            }
            System.out.println("File path is :"+filePath);
            String [] segments = filePath.split("/");

            System.out.println("File Size in Byte /"+(new File(filePath).length()));
            Toast.makeText(this,segments[(segments.length)-1],Toast.LENGTH_LONG).show();
            bitmap = TextToImageEncode(IP+"/"+segments[(segments.length)-1]+"/"+(new File(filePath).length()));
            img_QR.setImageBitmap(bitmap);

        } catch ( WriterException e) {
            e.printStackTrace();
        }
    }
    String IPgen(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        try {
            return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";

    }
    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    class ServerThread extends Thread {

        public void run(){
            try {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listenText.setText("Listening on port : "+IP+":"+SERVERPORT);
                    }
                });

                serverSocket = new ServerSocket(SERVERPORT);

                while (true) {
                    // LISTEN FOR INCOMING CLIENTS
                    Socket client = serverSocket.accept();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Connected...");
                        }
                    });

                    File myFile = new File (filePath);
                    byte [] mybytearray  = new byte [(int)myFile.length()];
                    FileInputStream fis = new FileInputStream(myFile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);
                    OutputStream os = client.getOutputStream();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(MainActivity_sender.this,"Sending....",Toast.LENGTH_LONG).show();
                            serverStatus.setText("sending File ...");
                        }
                    });
                    os.write(mybytearray,0,mybytearray.length);
                    os.flush();
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}