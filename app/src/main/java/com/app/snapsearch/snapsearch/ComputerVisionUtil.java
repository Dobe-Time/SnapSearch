package com.app.snapsearch.snapsearch;
import android.app.Activity;
import android.content.pm.FeatureInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.view.View;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

public class ComputerVisionUtil{
    private Bitmap image;
    private  static final String TAG =  "ComputerVisionUtil";
    private static final String key = "429565f34195454a8b3f48e4031a1bb4";
    private static VisionServiceClient visonClient;
    public ComputerVisionUtil(Bitmap image){
        this.image = image;
    }
    public void analyseImage(){
        ByteArrayOutputStream compresStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, compresStream);
        ByteArrayInputStream inSteam = new ByteArrayInputStream(compresStream.toByteArray());
        FindDescriptionTask.execute((Runnable) inSteam);
    }
    private class FindDescriptionTask extends AsyncTask<InputStream, String, String>{

        @Override
        protected String doInBackground(InputStream... params) {
            Log.d(TAG, "FindDescriptionTask Top of background");
            try{
                String[] fetures = {"description"};
                String[] details = {};
                AnalysisResult result = visonClient.analyzeImage(params[0],  fetures, details);
                String gResults = new Gson().toJson(result);
                return gResults;
            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
            StringBuilder builder = new StringBuilder();
            for(Caption caption : result.description.captions){
                builder.append(caption.text);
            }
        }
    }
}
