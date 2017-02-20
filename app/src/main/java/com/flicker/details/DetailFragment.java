package com.flicker.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flicker.App;
import com.flicker.Photo;
import com.flicker.PhotoInfo;
import com.flicker.PhotoInfoResponse;
import com.flicker.PhotoResponse;
import com.flicker.R;
import com.flicker.SquareImageView;

/**
 * Created by ner on 2/20/17.
 */

public class DetailFragment extends Fragment implements DetailView {

    private TextView title;
    private SquareImageView imageView;
    private TextView username;
    private TextView location;
    private TextView description;

    private Photo photo;

    private DetailPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_details, container, false);

        presenter = new DetailPresenter();

        title = (TextView) view.findViewById(R.id.detail_title);
        imageView = (SquareImageView) view.findViewById(R.id.full_sized_image);
        username = (TextView) view.findViewById(R.id.detail_username);
        location = (TextView) view.findViewById(R.id.detail_location);
        description = (TextView) view.findViewById(R.id.detail_description);

        imageView.post(new Runnable() {
            @Override
            public void run() {
                App.getPicasso().load(photo.url_c).resize(imageView.getWidth(), imageView.getHeight()).centerInside().into(imageView);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        presenter.unSubscribe();
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.subscribe(this);

        presenter.getDetails(photo.id);
    }

    public static DetailFragment instance(Photo photo) {
        DetailFragment detailFragment = new DetailFragment();

        detailFragment.photo = photo;

        return detailFragment;
    }

    @Override
    public void displayPhotoInfo(PhotoInfoResponse photoInfoResponse) {
        if (photoInfoResponse.title != null)
            title.setText(photoInfoResponse.title);
        else if (photo.title != null)
            title.setText(photo.title);

        if (photoInfoResponse.photo.owner.username != null)
            username.setText(photoInfoResponse.photo.owner.username);

        if (photoInfoResponse.photo.owner.location != null)
            location.setText(photoInfoResponse.photo.owner.location);

        if (photoInfoResponse.description != null)
            description.setText(photoInfoResponse.description);
    }

    @Override
    public void displayErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
