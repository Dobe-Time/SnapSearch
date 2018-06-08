package com.app.snapsearch.snapsearch;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.contract.Tag;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private  static final String TAG =  "ComputerVisionUtil";
    // Parmas for constructor are api key and region of api key for computer vision.
    private static final String KEY = "2ee6b523d54e4bf3af298d87e2314354";
    public  VisionServiceClient visionServiceClient = new VisionServiceRestClient(KEY, "https://westus2.api.cognitive.microsoft.com/vision/v2.0");
    public static final int CAMERA_REQUEST = 1;
    Bitmap image;
    ImageView imgPicture;
    public void onImageGalleryClicked(View v){
        //invoke image gallery using implicit intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        //where do find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        //use URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        photoPickerIntent.setDataAndType(data, "image/*");

        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == IMAGE_GALLERY_REQUEST || requestCode == 0) {
            Bitmap datifoto = null;
            Uri picUri = data.getData();//<- get Uri here from data intent
            if(picUri !=null){
                try {
                    datifoto = android.provider.MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            picUri);
                    image = datifoto;
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null){
            image = (Bitmap) savedInstanceState.get("image");
        }
        Button btnCamera = (Button)findViewById(R.id.TakePictureButton);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            }
        });
        final Button doneButton = (Button) findViewById(R.id.backButton);
        doneButton.setVisibility(View.GONE);
        final Intent flickr = new Intent(getApplicationContext(),FlickrActivityFragment.class);
        int pictureId = R.drawable.tree;
        // Hooks up image button in activity_main to the java object.
        ImageButton cameraButton = (ImageButton) findViewById(R.id.CameraButton);
        //This try catch loop for a json syntax exeption that will arise from the use of the Azure
        //async task.
        try {
            cameraButton.setOnClickListener(new View.OnClickListener() {
                ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak") AsyncTask<ByteArrayInputStream, String, String> visonTask = new AsyncTask<ByteArrayInputStream, String, String>() {
                        @Override
                        protected String doInBackground(ByteArrayInputStream... fileInputStreams) {
                            ByteArrayInputStream inputStream1;
                            inputStream1 = fileInputStreams[0];
                            try {
                                //This describes the loading message
                                publishProgress("Loading....");
                                // These lines specify which Json tags will be taken from visionServiceClient.analyzeImage.
                                String[] features = {"description"};
                                String[] details = {};
                                // The big one, the call to the Image recognition API.
                                AnalysisResult result = visionServiceClient.analyzeImage(inputStream1 , features, details);
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
                            //TextView textView = (TextView)findViewById(R.id.textView);
                            StringBuilder stringBuilder = new StringBuilder();
                            // adds all the"tags" from the json out put of computer vision and makes a string.
                            // This string will be displayed on screen and passed in as flikr's query.
                            for(Caption caption : result.description.captions){
                                stringBuilder.append(caption.text + " ");
                            }
                            //textView.setText(stringBuilder);
                            Toast.makeText(MainActivity.this, "Fully Loaded", Toast.LENGTH_SHORT).show();
                            // bundle to pass  in search query
                            Bundle bundle = new Bundle();
                            bundle.putString("query", stringBuilder.toString());
                            FlickrActivityFragment fragment = new FlickrActivityFragment();
                            fragment.setArguments(bundle);
                            //launches flikr fragment.
                            doneButton.setVisibility(View.VISIBLE);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.activity_main, fragment);
                            transaction.commit();
                        }
                    };
                    //CALL TO THE ASYNC TASK.
                    visonTask.execute(setImage(image));
                }
            });
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    //makes it so when leaving the choose picture intent the image displayed will become the chosen image.
    protected void onResume() {
        super.onResume();
        if(image != null){
            ImageView imgPicture = (ImageView) this.findViewById(R.id.ImgPicture);
            imgPicture.invalidate();
            imgPicture.setImageBitmap(image);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("image", image);
    }

    //method to correctly set image from bitmap or to use default timesquare as a place holder.
    public ByteArrayInputStream setImage(Bitmap image){
        Bitmap mBitmap;
        if (image == null){
            image = BitmapFactory.decodeResource(getResources(), R.drawable.timesquare);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return inputStream;
    }

    @Override
    //closes fragment goes back to home screen and hides done button.
    public void onBackPressed() {
        super.onBackPressed();
        Button doneButton = findViewById(R.id.backButton);
        doneButton.setVisibility(View.GONE);
        onStop();
    }
}