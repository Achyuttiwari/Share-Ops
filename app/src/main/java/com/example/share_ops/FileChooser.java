package com.example.share_ops;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FileChooser extends AppCompatActivity {
    TextView filePath;
    int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        filePath = (TextView) findViewById(R.id.text_filePath);
        permissionFile();
    }
}
