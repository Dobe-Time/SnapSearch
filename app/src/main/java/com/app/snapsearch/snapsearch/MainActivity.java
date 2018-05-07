package com.app.snapsearch.snapsearch;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    //tobytest
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton cameraButton = (ImageButton) findViewById(R.id.CameraButton);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(captureImage);
            }
        });

    }
}

