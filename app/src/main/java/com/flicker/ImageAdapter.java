package com.flicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ner on 2/18/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Photo> images = new ArrayList<>();

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.bind(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void replaceImages(List<Photo> newImageUrls) {
        images.clear();

        images.addAll(newImageUrls);

        notifyDataSetChanged();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }

        public void bind(final Photo photo) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    App.getPicasso()
                            .load(photo.url_m)
                            .resize(imageView.getWidth(), imageView.getHeight())
                            .centerCrop()
                            .into(imageView);
                }
            });
        }
    }
}
