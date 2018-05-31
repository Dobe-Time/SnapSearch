package com.app.snapsearch.snapsearch;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FlickrActivityFragment extends Fragment {
    private RecyclerView mPictureView;
    private List<GalleryItem> mItems = new ArrayList<>();
    public FlickrActivityFragment newInstance() {
        return  new FlickrActivityFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        FetchPictures fetcher = new FetchPictures();
        fetcher.execute();
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
        private TextView mTitleTextView;
        public PhotoHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }
        public void bindGalleryItem(GalleryItem item){
            mTitleTextView.setText(item.toString());
        }
    }

    private class RecyclerAdaptor extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;
        public RecyclerAdaptor(List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchPictures extends AsyncTask<Void, Void, List<GalleryItem>>{

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
            mItems = items;
            setupAdapter();
        }

       // @Override
//        protected void onProgressUpdate(Void... values) {
//           // ProgressDialog mDialog = new ProgressDialog(getContext());
//           // mDialog.setMessage((CharSequence) "loading");
//        }
//        @Override
//        protected void onPreExecute() {
//            ProgressDialog mDialog = new ProgressDialog(getContext());
//            mDialog.show();
//        }


    }
}
