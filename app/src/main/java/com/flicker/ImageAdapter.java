package com.flicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ner on 2/18/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private OnItemSelected onItemSelected;

    private List<Photo> images = new ArrayList<>();

    public ImageAdapter(OnItemSelected onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

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

    public void replaceImages(List<Photo> newPhotos) {
        images.clear();

        images.addAll(newPhotos);

        notifyDataSetChanged();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView imageTitle;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            imageTitle = (TextView) itemView.findViewById(R.id.image_title);
        }

        public void bind(final Photo photo) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelected.onItemSelected(photo);
                }
            });

            imageTitle.setText(photo.title);

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

    public interface OnItemSelected {
        void onItemSelected(Photo photo);
    }
}
