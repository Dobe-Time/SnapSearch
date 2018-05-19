package com.app.snapsearch.snapsearch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private  static final String TAG =  "ComputerVisionUtil";
    private static final String key = "e3c1a93f0128485186fff27b936a608f";
    public  VisionServiceClient visionServiceClient = new VisionServiceRestClient("e3c1a93f0128485186fff27b936a608f");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tree);

        // Hooks up image button in activity_main to the java object.
        ImageButton cameraButton = (ImageButton) findViewById(R.id.CameraButton);
        //Creates the camera intent.
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Onclick for the camera button moves user to a camera intent to take a photo.
        ByteArrayOutputStream compresStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, compresStream);
        final ByteArrayInputStream inSteam = new ByteArrayInputStream(compresStream.toByteArray());
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("StaticFieldLeak") final AsyncTask<InputStream, String, String> visonTask = new AsyncTask<InputStream, String, String>() {
                    @Override
                    protected String doInBackground(InputStream... inSteam) {
                        try{
                            publishProgress("loading...");
                            String[] fetures = {"description"};
                            String[] details = {};
                            AnalysisResult result = visionServiceClient.analyzeImage(inSteam[0],  fetures, details);
                            String gResults = new Gson().toJson(result);
                            return gResults;
                        }catch (Exception e){
                            return null;
                        }
                    }
                    @Override
                    protected void onPostExecute(String s) {
                     // this LINE.
                        AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
                        StringBuilder builder = new StringBuilder();
                        for(Caption caption : result.description.captions){
                            builder.append(caption.text);
                        }
                        TextView textView = (TextView) findViewById(R.id.textView);
                        textView.setText(builder);
                    }
                };
                visonTask.execute();
                }
                //startActivity(captureImage);
                //puts the date in a bundle stored in a map. value????? use bitmap
                //captureImage.putExtra(MediaStore.EXTRA_OUTPUT, 0);
        });

    }

}

