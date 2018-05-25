package com.app.snapsearch.snapsearch;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class FlickrActivityFragment extends Fragment {
    private RecyclerView mPictureView;

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
        return  view;
    }
    private class FetchPictures extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String reslt = new FlikrPicker().getUrlString("http://www.bignerdranch.com");
            }catch (IOException ioe){
                return null;
            }
            return null;
        }
    }
}
