package com.bcit.manga_stone.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bcit.manga_stone.utils.ImageLoader;
import com.bcit.manga_stone.MainActivity;
import com.bcit.manga_stone.object_classes.Manga;
import com.bcit.manga_stone.R;
import com.bcit.manga_stone.fragments.SummaryFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.NetworkPolicy;

public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.ViewHolder> {

    private Manga[] localDataSet;
    private FirebaseFirestore db;
    private FirebaseUser account;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewAverageScore;
        private final ImageButton deleteItem;
        private final ImageButton viewItem;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textView_historyItem_title);
            textViewAverageScore = view.findViewById(R.id.textView_historyItem_averageScore);
            deleteItem = view.findViewById(R.id.imageButton_historyItem_delete);
            viewItem = view.findViewById(R.id.imageButton_historyItem_open);
            imageView = view.findViewById(R.id.imageView_history_cover);
        }

        public TextView getTextViewAverageScore() {
            return textViewAverageScore;
        }

        public TextView getTextViewTitle() {
            return textViewTitle;
        }

        public ImageButton getDeleteItem() {
            return deleteItem;
        }

        public ImageButton getViewItem() {
            return viewItem;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    public HistoryItemAdapter(Manga[] dataSet, FirebaseUser account) {
        localDataSet = dataSet;
        this.account = account;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_history, viewGroup, false); //error here should be expected, this is a template

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        db = FirebaseFirestore.getInstance();
        viewHolder.getTextViewTitle().setText((CharSequence) localDataSet[position].title);
        viewHolder.getTextViewAverageScore().setText("Average Score: " + (int) localDataSet[position].averageScore);
        //Sets the cover image of the history item
        String url = localDataSet[position].getCoverImage();
        ImageLoader.LoadImageFitCenter(url, viewHolder.getImageView(), NetworkPolicy.OFFLINE);
        //Sets the history delete button to delete this specific manga from history.
        viewHolder.getDeleteItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users/"+account.getUid()+"/history")
                .whereEqualTo("title", localDataSet[position].title)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot:task.getResult()) {
                                db.collection("users/"+account.getUid()+"/history")
                                .document(documentSnapshot.getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        System.out.println("Deleted item!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Failed to delete item!");
                                    }
                                });
                            }
                        } else {
                            Log.d("Firebase", "Error getting documents: ", task.getException());
                        }
                    }
                });

            }
        });
        //Sets the history view button to go to the summary page for this specific manga.
        viewHolder.getViewItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();

                List<Manga> mangas = new ArrayList<>();

                db.collection("users/"+account.getUid()+"/favourites")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document: task.getResult()){
                                        mangas.add(
                                                new Manga(
                                                        document.getData().get("id").toString(),
                                                        document.getData().get("title").toString(),
                                                        document.getData().get("description").toString(),
                                                        document.getData().get("coverImage").toString(),
                                                        document.getData().get("status").toString(),
                                                        document.getData().get("highestRatedRankings").toString(),
                                                        document.getData().get("mostPopularRankings").toString(),
                                                        (long) document.getData().get("averageScore")
                                                )
                                        );
                                    }
                                } else {
                                    Log.w("Debug", "Error getting documents.", task.getException());
                                }
                                FragmentTransaction ft = ((MainActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragmentContainerView_main_mainPages, SummaryFragment.newInstance(localDataSet[position]));
                                ft.commit();
                            }
                        });
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

}