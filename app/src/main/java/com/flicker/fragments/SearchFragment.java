package com.flicker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flicker.FlickerActivity;
import com.flicker.ImageAdapter;
import com.flicker.Photo;
import com.flicker.R;
import com.flicker.details.DetailFragment;

import java.util.List;

/**
 * Created by ner on 2/20/17.
 */

public class SearchFragment extends Fragment {

    private RecyclerView imageRecycler;
    private ImageAdapter imageAdapter;

    private FlickerActivity flickerActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        imageRecycler = (RecyclerView) view.findViewById(R.id.image_recycler);

        imageAdapter = new ImageAdapter(new ImageAdapter.OnItemSelected() {
            @Override
            public void onItemSelected(Photo photo) {
                flickerActivity.showDetailsFragment(DetailFragment.instance(photo));
            }
        });

        imageRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        imageRecycler.setAdapter(imageAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FlickerActivity)
            flickerActivity = (FlickerActivity) context;
    }

    public void displayImages(List<Photo> photos) {
        imageAdapter.replaceImages(photos);
    }
}
