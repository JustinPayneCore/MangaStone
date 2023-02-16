package com.bcit.manga_stone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bcit.manga_stone.utils.ImageLoader;
import com.bcit.manga_stone.R;
import com.squareup.picasso.NetworkPolicy;


public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {

    private String[] localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView_chapter);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    public ChapterAdapter(String[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_chapter, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        //Sets the image for a single page in a chapter
        //this adapter is repeated until the whole manga chapter has been set
        String url = localDataSet[position];
        ImageLoader.LoadImage(url, viewHolder.getImageView(), NetworkPolicy.OFFLINE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}