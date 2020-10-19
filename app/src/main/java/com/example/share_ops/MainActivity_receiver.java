package com.example.share_ops;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.net.Socket;

public class MainActivity_receiver extends AppCompatActivity {

    Socket clientSocket;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_receiver);
    }
}