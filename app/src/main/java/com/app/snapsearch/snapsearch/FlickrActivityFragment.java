package com.app.snapsearch.snapsearch;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FlickrActivityFragment extends Fragment {
    private RecyclerView mPictureView;
    private static final String TAG = "FlickrActivityFragment";
    private List<GalleryItem> mItems = new ArrayList<>();
    private ImageDownloader<PhotoHolder> mImageDownloader;
    public FlickrActivityFragment newInstance() {
        return  new FlickrActivityFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        FetchPictures fetcher = new FetchPictures();
        fetcher.execute();

        Handler responseHandler = new Handler();
        mImageDownloader = new ImageDownloader<>(responseHandler);
        mImageDownloader.setmImageDownloaderListener(new ImageDownloader.ImageDownloaderListener<PhotoHolder>() {
            @Override
            public void onImageDownloaded(PhotoHolder photoHolder, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                photoHolder.bindDrawable(drawable);
            }
        });


        mImageDownloader.start();
        mImageDownloader.getLooper();
        Log.i(TAG, "Background Thread Started!");
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mImageDownloader.clearQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_flickr, container, false);
        mPictureView = (RecyclerView) view.findViewById(R.id.RecyclerViewPic);
        mPictureView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return  view;
    }
    private void setupAdapter(){
        if(isAdded()){
            mPictureView.setAdapter(new RecyclerAdaptor(mItems));
        }
    }
    private class PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView mItemImageView;
        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }
        public void bindDrawable(Drawable drawable){
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class RecyclerAdaptor extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;
        public RecyclerAdaptor(List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.list_item_gallery, parent, false);
            return new PhotoHolder(v);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeHolder = getResources().getDrawable(R.drawable.tree);
            holder.bindDrawable(placeHolder);
            mImageDownloader.queueImage(holder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchPictures extends AsyncTask<Void, Void, List<GalleryItem>>{
        ProgressDialog mDialog = new ProgressDialog(getContext());
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            FlickrPicker picker = new FlickrPicker();
            List<GalleryItem> someList = null;
            try {
                someList = picker.fetchItems();
            }catch (android.os.NetworkOnMainThreadException e){
                e.printStackTrace();
            }
            return someList;
        }
        protected void onPostExecute(List<GalleryItem> items){
            mDialog.dismiss();
            mItems = items;
            setupAdapter();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mDialog.setMessage("Getting Images");
        }
        @Override
        protected void onPreExecute() {
            mDialog.show();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mImageDownloader.quit();
        Log.i(TAG,"Background destroyed!");
    }
}
