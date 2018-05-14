//package com.app.snapsearch.snapsearch;
//
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.support.annotation.Nullable;
//
//import com.google.gson.Gson;
//import com.microsoft.projectoxford.vision.VisionServiceClient;
//import com.microsoft.projectoxford.vision.VisionServiceRestClient;
//import com.microsoft.projectoxford.vision.contract.AnalysisResult;
//import com.microsoft.projectoxford.vision.rest.VisionServiceException;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class ComputerVisionUtil {
//    private Bitmap image;
//    private static final String key = "INSERT KEY";
//    private VisionServiceClient visonClient;
//    public void onCreate(Bitmap image){
//        visonClient = new VisionServiceRestClient(":429565f34195454a8b3f48e4031a1bb4");
//        this.image = image;
//    }
//    public String analyseImage(){
//        ByteArrayOutputStream compresStream = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, compresStream);
//        ByteArrayInputStream inSteam = new ByteArrayInputStream(compresStream.toByteArray());
//        final AsyncTask<InputStream, String, String> visionTask = new AsyncTask<InputStream, String, String>() {
//            @Nullable
//            @Override
//            protected String doInBackground(InputStream... params) {
//                try{
//                    String[] fetures = {"description"};
//                    String[] details = {};
//                    AnalysisResult result = visonClient.analyzeImage(params[0],  fetures, details);
//                    String gResults = new Gson().toJson(result);
//                    return gResults;
//                }catch (Exception e){
//                    return null;
//                }
//            }
//
//        }
//    }
//}
