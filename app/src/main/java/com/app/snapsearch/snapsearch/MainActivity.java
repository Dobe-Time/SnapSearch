package com.app.snapsearch.snapsearch;

import java.net.HttpURLConnection;
import java.net.URI;

import java.net.URI;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
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
import com.google.gson.JsonObject;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;


public class MainActivity extends AppCompatActivity {
    private  static final String TAG =  "ComputerVisionUtil";
    private static final String key = "21381d6e485e4fc6bc5836904ff344c3";
    public  VisionServiceClient visionServiceClient = new VisionServiceRestClient("2ee6b523d54e4bf3af298d87e2314354", "https://westus2.api.cognitive.microsoft.com/vision/v2.0");

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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // You must use the same region in your REST call as you used to get your
        // subscription keys. For example, if you got your subscription keys from
        // westus, replace "westcentralus" in the URI below with "westus".
        //
        // Free trial subscription keys are generated in the westcentralus region. If you
        // use a free trial subscription key, you shouldn't need to change this region.
        final String uriBase =
                "https://westcentralus.api.cognitive.microsoft.com/vision/v2.0/analyze";

        final String imageToAnalyze =
                "https://upload.wikimedia.org/wikipedia/commons/" +
                        "1/12/Broadway_and_Times_Square_by_night.jpg";
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                @SuppressLint("StaticFieldLeak") AsyncTask<InputStream, String, String> visonTask = new AsyncTask<InputStream, String, String>() {
                    @Override
                    protected String doInBackground(InputStream... inputStreams) {
                        try {
                            publishProgress("Loading....");
                            String[] features = {"description"};
                            String[] details = {};

                            AnalysisResult result = visionServiceClient.analyzeImage(imageToAnalyze, features, details);

                            String strResult = new Gson().toJson(result);
                            return strResult;
                        } catch (VisionServiceException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
                        TextView textView = (TextView)findViewById(R.id.textView);
                        StringBuilder stringBuilder = new StringBuilder();
                        for(Caption caption : result.description.captions){
                            stringBuilder.append(caption.text);
                        }
                        textView.setText(stringBuilder);
                    }
                };
                visonTask.execute(inputStream);
                }
               // startActivity(captureImage);
                //puts the date in a bundle stored in a map. value????? use bitmap
                //captureImage.putExtra(MediaStore.EXTRA_OUTPUT, 0);
        });

    }

}

