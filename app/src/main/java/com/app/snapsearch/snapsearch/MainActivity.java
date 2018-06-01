package com.app.snapsearch.snapsearch;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Tag;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private  static final String TAG =  "ComputerVisionUtil";
    //REMEMBER TO GET KEY OUT OF HERE
    // Parmas for constructor are api key and region of api key.
    private static final String KEY = "2ee6b523d54e4bf3af298d87e2314354";
    public  VisionServiceClient visionServiceClient = new VisionServiceRestClient(KEY, "https://westus2.api.cognitive.microsoft.com/vision/v2.0");
    public static final int CAMERA_REQUEST = 1;
    Image image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final Intent flickr = new Intent(getApplicationContext(),FlickrActivityFragment.class);
        int pictureId = R.drawable.timesquare;
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), pictureId);
        // Hooks up image button in activity_main to the java object.
        ImageButton cameraButton = (ImageButton) findViewById(R.id.CameraButton);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        try {
            cameraButton.setOnClickListener(new View.OnClickListener() {
                ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                @Override
                public void onClick(View v) {

                    @SuppressLint("StaticFieldLeak") AsyncTask<InputStream, String, String> visonTask = new AsyncTask<InputStream, String, String>() {
                        @Override
                        protected String doInBackground(InputStream... fileInputStreams) {
                            try {
                                //This describes the loading message
                                publishProgress("Loading....");
                                // These lines specify which Json tags will be taken from visionServiceClient.analyzeImage.
                                String[] features = {"tags"};
                                String[] details = {};
                                // The big one, the call to the Image recognition API.
                                AnalysisResult result = visionServiceClient.analyzeImage(inputStream, features, details);

                                String strResult = new Gson().toJson(result);
                                return strResult;
                                // catching exceptions.
                            } catch (VisionServiceException e) {
                                e.printStackTrace();
                                return null;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        //Loading message. Both onPreExecute and onProgressUpdate.
                        protected void onPreExecute() {
                            mDialog.show();
                        }

                        @Override
                        protected void onProgressUpdate(String... values) {
                            mDialog.setMessage("Analyzing Image");
                        }

                        @Override
                        //Formats string from tags and sets the textView to the String
                        protected void onPostExecute(String s) {
                            mDialog.dismiss();
                            AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
                            TextView textView = (TextView)findViewById(R.id.textView);
                            StringBuilder stringBuilder = new StringBuilder();

//                            for(Caption caption : result.description.captions){
//                                stringBuilder.append(caption.text);
//                            }
                            for(Tag tag : result.tags){
                                stringBuilder.append(tag.name + " ");
                            }

                            textView.setText(stringBuilder);
                            Toast.makeText(MainActivity.this, "Fully Loaded", Toast.LENGTH_SHORT).show();
                            FlickrActivityFragment fragment = new FlickrActivityFragment();
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.activity_main, fragment);
                            transaction.commit();

                        }
                    };
                    //CALL TO THE ASYNC TASK.
                    visonTask.execute(inputStream);
                }
                // startActivity(captureImage);
                //puts the date in a bundle stored in a map. value????? use bitmap
                //captureImage.putExtra(MediaStore.EXTRA_OUTPUT, 0);
            });
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }
}
//    public void onImageGallerySelected(View v){
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//
//        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
//        String pictureDirectoryPath = pictureDirectory.getPath();
//
//        Uri data = Uri.parse(pictureDirectoryPath);
//
//        photoPickerIntent.setDataAndType(data, "image/*");
//
//        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode,resultCode,data);
//
//    }


