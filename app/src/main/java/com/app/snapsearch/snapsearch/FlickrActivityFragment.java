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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    String query;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button backButton = getActivity().findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        setRetainInstance(true);
        setHasOptionsMenu(true);
        //call to fetch pictures async talsk to get picturs loaded from flickr
        FetchPictures fetcher = new FetchPictures();
        fetcher.execute();
        //gets pictures from flickr and binds them to photo holder to be displayed.
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
    //clears downloaded pictures
    public void onDestroyView(){
        super.onDestroyView();
        mImageDownloader.clearQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //gets the query from the bundle made in mainActivity
        query = (String) getArguments().get("query");
        View view = inflater.inflate(R.layout.fragment_flickr, container, false);
        mPictureView = (RecyclerView) view.findViewById(R.id.RecyclerViewPic);
        //sets up the recycler view for holding pictures.
        mPictureView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return  view;
    }
    private void setupAdapter(){
        if(isAdded()){
            mPictureView.setAdapter(new RecyclerAdaptor(mItems));
        }
    }
    //holds the photos to be displayed
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
    //puts images in recycler view for display
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
    //flickr async task for getting information from flickr
    private class FetchPictures extends AsyncTask<Void, Void, List<GalleryItem>>{
        ProgressDialog mDialog = new ProgressDialog(getContext());
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            FlickrPicker picker = new FlickrPicker();
            List<GalleryItem> someList = null;
            try {
                someList = picker.searchPhotots(query);
            }catch (android.os.NetworkOnMainThreadException e){
                e.printStackTrace();
            }
            return someList;
        }
        //makes mItems equal the List created in the async task
        // and sets calls setup adapter to setup recycler view.
        protected void onPostExecute(List<GalleryItem> items){
            mItems = items;
            setupAdapter();
        }
    }
    @Override
    //closes image downloader
    public void onDestroy(){
        super.onDestroy();
        mImageDownloader.quit();
        Log.i(TAG,"Background destroyed!");
    }
}