package com.app.snapsearch.snapsearch;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Make it a try catch.
public class FlickrPicker {
    private static final String TAG = "FlickrFetcher";
    private static final String API_KEY = "3f4a24364038b034dcfe90f376b69b79";
    public byte[] getUrlByte(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            return outputStream.toByteArray();
        }finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec)throws IOException{
        return new String(getUrlByte(urlSpec));
    }
    public List<GalleryItem> fetchItems() {
        List<GalleryItem> items = new ArrayList<GalleryItem>();
        try{
            String url = Uri.parse("http://api.flickr.com/service/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }
    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)throws IOException, JSONException{
        Gson gson = new Gson();
        Type galleryItemType = new TypeToken<ArrayList<GalleryItem>>() {
        }.getType();

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");
        String jsonPhotosString = photosJsonArray.toString();

        List<GalleryItem> galleryItemList = gson.fromJson(jsonPhotosString, galleryItemType);
        items.addAll(galleryItemList);
    }
}
