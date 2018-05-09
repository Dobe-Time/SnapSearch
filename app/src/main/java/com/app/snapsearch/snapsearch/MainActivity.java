package com.app.snapsearch.snapsearch;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    //tobytest
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hooks up image button in activity_main to the java object.
        ImageButton cameraButton = (ImageButton) findViewById(R.id.CameraButton);
        //Creates the camera intent.
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Onclick for the camera button moves user to a camera intent to take a photo.
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(captureImage);
                //puts the date in a bundle stored in a map. value????? use bitmap
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, 0);

            }
        });

    }
}

