package com.example.share_ops;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;


import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class FileChooser extends AppCompatActivity {
    TextView filePath;
    int PERMISSION_REQUEST_CODE = 1;
    ArrayList<MediaFile> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        filePath = (TextView) findViewById(R.id.text_filePath);
        permissionFile();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission already granted", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                Log.e("size","size is"+String.valueOf(files.size())+"path"+files.get(0).getPath());

                String path = files.get(0).getPath();
                filePath.setText(path);
                Log.e("data", path);
            } else {
                Log.e("data", "cancel");
            }
        }
    }

    public void permissionFile() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "permission already granted", Toast.LENGTH_SHORT).show();
        }

    }
        public void chooseFile(View view){

            Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setSelectedMediaFiles(files)
                    .setShowFiles(true)
                    .setShowImages(true)
                    .setShowAudios(true)
                    .setShowVideos(false)
                    .setIgnoreNoMedia(false)
                    .enableVideoCapture(true)
                    .enableImageCapture(true)
                    .setIgnoreHiddenFile(false)
                    .setMaxSelection(1)
                    .build());
            startActivityForResult(intent, 1);
        }

        public void moveNext(View view){
            System.out.println(new File(filePath.getText().toString()).exists());
            if(new File(filePath.getText().toString()).exists()){
                Intent intent = new Intent(this, MainActivity_Sender.class);
                intent.putExtra("path",filePath.getText().toString());
                intent.putParcelableArrayListExtra("list",files);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please Select a File", Toast.LENGTH_SHORT).show();
            }
    }
}
