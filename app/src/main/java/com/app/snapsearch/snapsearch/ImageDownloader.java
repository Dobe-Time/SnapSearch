package com.app.snapsearch.snapsearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ImageDownloader<T> extends HandlerThread {
    private static final String TAG = "ImageDownloader";
    private boolean mHasStopped = false;
    private static final int MESSAGE_DOWNLOADED = 0;
    private Handler mReqest;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ImageDownloaderListener<T> mImageDownloaderListener;

    public ImageDownloader(Handler responseHandler){
        super(TAG);
        mResponseHandler = responseHandler;
    }
    public interface ImageDownloaderListener<T>{
        void onImageDownloaded(T target, Bitmap thumbnail);
    }
    public void setmImageDownloaderListener(ImageDownloaderListener listener){
        mImageDownloaderListener = listener;
    }
    @Override
    public boolean quit(){
        mHasStopped = true;
        return super.quit();
    }
    public void queueImage(T target, String url) {
        Log.i(TAG, "Got a URL " + url);
        if (url == null){
            mRequestMap.remove(target);
        }else{
            mRequestMap.put(target, url);
            mReqest.obtainMessage(MESSAGE_DOWNLOADED, target).sendToTarget();
        }
    }
    public void clearQueue(){
        mResponseHandler.removeMessages(MESSAGE_DOWNLOADED);
        mRequestMap.clear();
    }
    private void handleRequest(final T target){
        try{
            final String url = mRequestMap.get(target);
            if(url == null){
                return;
            }
            byte[] bitmapBytes = new FlickrPicker().getUrlByte(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap made");
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mRequestMap.get(target) != url || mHasStopped){
                        return;
                    }
                    mRequestMap.remove(target);
                    mImageDownloaderListener.onImageDownloaded(target, bitmap);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onLooperPrepared(){
        mReqest = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == MESSAGE_DOWNLOADED){
                    T target = (T) msg.obj;
                    Log.i(TAG,"Got message for url:" + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }
}
