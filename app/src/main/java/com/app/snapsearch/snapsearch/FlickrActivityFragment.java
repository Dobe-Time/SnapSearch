package com.app.snapsearch.snapsearch;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flickr, container, false);
        mPictureView = (RecyclerView) view.findViewById(R.id.RecyclerViewPic);
        mPictureView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return  view;
    }
}
