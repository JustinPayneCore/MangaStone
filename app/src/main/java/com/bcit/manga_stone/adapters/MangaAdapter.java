package com.bcit.manga_stone.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bcit.manga_stone.MainActivity;
import com.bcit.manga_stone.object_classes.Manga;
import com.bcit.manga_stone.R;
import com.bcit.manga_stone.fragments.SummaryFragment;
import com.bcit.manga_stone.utils.ImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.ViewHolder> {

    private Manga[] localDataSet;
    FirebaseFirestore db;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.imageView_manga_browse);
            textView = view.findViewById(R.id.textView_manga_browsetitle);
        }

        public ImageView getImageView() {
            return imageView;
        }
        public TextView getTextView() {
            return textView;
        }
    }

    public MangaAdapter(Manga[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_manga, viewGroup, false); //error here should be expected, this is a template

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        //Sets the manga's in the Browse Fragment and gives the imageview an onclick method to
        //send the user to the requested anime's summary page.
        //Checks if the manga array is not null and sets the manga cover image appropriately.
        if(localDataSet != null){
            //Checks if the manga in the index is not null
            if(localDataSet[position] != null){
                String url = localDataSet[position].getCoverImage();
                ImageLoader.LoadImageFitCenter(url, viewHolder.getImageView(), NetworkPolicy.OFFLINE);
            } else {
                //Default image if manga object is null
                viewHolder.getImageView().setImageResource(R.drawable.ic_outline_image_24);
            }

            //Checks if the manga is null, sets the mangas title appropriately
            if(localDataSet[position] != null){
                viewHolder.getTextView().setText(localDataSet[position].getTitle());
            } else {
                viewHolder.getTextView().setText("Title not Available");
            }
            //sets the onclick function for the manga to send the user to the mangas summary page.
            viewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction ft = ((MainActivity)view.getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragmentContainerView_main_mainPages, SummaryFragment.newInstance(localDataSet[position]));
                    ft.commit();
                    addHistory(localDataSet[position]);
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(localDataSet != null){
            return localDataSet.length;
        } else {
            return 0;
        }
    }

    //Add History function that is called whenever any manga is selected. Adds the
    //Manga to the user's history collection in the database.
    public void addHistory(Manga manga){
        if(manga != null) {
            FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
            db = FirebaseFirestore.getInstance();
            if (account != null){
                db.collection("users/"+account.getUid()+"/history")
                        .add(manga)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Debug", "Error adding document", e);
                            }
                        });
            }
        }
    }
}