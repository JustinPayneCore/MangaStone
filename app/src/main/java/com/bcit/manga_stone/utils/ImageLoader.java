package com.bcit.manga_stone.utils;

import android.util.Log;
import android.widget.ImageView;

import com.bcit.manga_stone.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ImageLoader {

    /**
     * Loads an image using picasso and places it into the specified image view. This method uses
     * the fit and centerInside image scaling options.
     * @param url - String
     * @param imageView - ImageView
     * @param policy - NetworkPolicy
     */
    public static void LoadImageFitCenter(String url, ImageView imageView, NetworkPolicy policy) {
        Picasso.get().load(url).fit().centerInside()
                .placeholder(R.drawable.ic_outline_image_24)
                .error(R.drawable.ic_baseline_error_outline_24)
                // Tells picasso to load image from cache, if it failed to load it retrieve from the network
                .networkPolicy(policy)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //Log.d("Picasso", "Image loaded using." + policy.name() + " " + url);
                    }

                    @Override
                    public void onError(Exception e) {
                        ImageLoader.LoadImageFitCenter(url, imageView, NetworkPolicy.NO_CACHE);
                    }
                });
    }

    /**
     * Loads an image using picasso and places it into the specified image view. This method uses
     * the fit image scaling option.
     * @param url - String
     * @param imageView - ImageView
     * @param policy - NetworkPolicy
     */
    public static void LoadImageFit(String url, ImageView imageView, NetworkPolicy policy) {
        Picasso.get().load(url).fit()
                .placeholder(R.drawable.ic_outline_image_24)
                .error(R.drawable.ic_baseline_error_outline_24)
                // Tells picasso to load image from cache, if it failed to load it retrieve from the network
                .networkPolicy(policy)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //Log.d("Picasso", "Image loaded using." + policy.name() + " " + url);
                    }

                    @Override
                    public void onError(Exception e) {
                        ImageLoader.LoadImageFit(url, imageView, NetworkPolicy.NO_CACHE);
                    }
                });
    }

    /**
     * Loads an image using picasso and places it into the specified image view. This method uses
     * no additional image scaling.
     * @param url - String
     * @param imageView - ImageView
     * @param policy - NetworkPolicy
     */
    public static void LoadImage(String url, ImageView imageView, NetworkPolicy policy) {
        Picasso.get().load(url)
                .placeholder(R.drawable.ic_outline_image_24)
                .error(R.drawable.ic_baseline_error_outline_24)
                // Tells picasso to load image from cache, if it failed to load it retrieve from the network
                .networkPolicy(policy)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //Log.d("Picasso", "Image loaded using." + policy.name() + " " + url);
                    }

                    @Override
                    public void onError(Exception e) {
                        ImageLoader.LoadImage(url, imageView, NetworkPolicy.NO_CACHE);
                    }
                });
    }
}
