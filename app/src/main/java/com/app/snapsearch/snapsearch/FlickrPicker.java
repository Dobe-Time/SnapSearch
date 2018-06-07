package com.app.snapsearch.snapsearch;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final  Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("method", SEARCH_METHOD)
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

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
    private List<GalleryItem> downloadGalleryItems(String url) {
        List<GalleryItem> items = new ArrayList<GalleryItem>();
        try{
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }
    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)throws IOException, JSONException{
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");
        for(int i = 0; i < photosJsonArray.length(); i++){
            JSONObject photoJsonObject =photosJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if(!photoJsonObject.has("url_s")){
                continue;
            }
            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
    public List<GalleryItem> searchPhotots(String query){
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }
    private String buildUrl(String method, String query){
        Uri.Builder uriBuilder = ENDPOINT.buildUpon();
        if (method.equals(SEARCH_METHOD)){
             uriBuilder.appendQueryParameter("text", query);
        }
        return uriBuilder.build().toString();

    }
}
