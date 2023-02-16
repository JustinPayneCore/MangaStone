package com.bcit.manga_stone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bcit.manga_stone.adapters.ButtonAdapter;
import com.bcit.manga_stone.utils.ImageLoader;
import com.bcit.manga_stone.object_classes.Manga;
import com.bcit.manga_stone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.NetworkPolicy;

/**
 * Use the SummaryFragment newInstance factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {

    // the fragment initialization parameters
    private static final String ARG_PARAM1 = "param1";

    private Manga mParam1;
    FirebaseFirestore db;

    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SummaryFragment.
     */
    public static SummaryFragment newInstance(Manga param1) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Manga) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Grabs reference to the views in the summary fragment
        TextView title = view.findViewById(R.id.textView_summary_title);
        ImageView cover = view.findViewById(R.id.imageView_summary_cover);
        TextView description = view.findViewById(R.id.textView_summary_description);
        TextView status = view.findViewById(R.id.textView_summary_status);
        TextView highestRated = view.findViewById(R.id.textView_summary_highestRated);
        TextView mostPopular = view.findViewById(R.id.textView_summary_mostPopular);
        TextView averageScore = view.findViewById(R.id.textView_summary_averageScore);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar_summary_rating);
        Button favourite = view.findViewById(R.id.button_summary_favourite);

        //Checks if the manga is not null and sets the data appropriately. If the manga is null
        //sets the summary page up with default data.
        if(mParam1 != null) {
            title.setText(mParam1.getTitle());
            description.setText(mParam1.getDescription());
            status.setText("Status " + mParam1.getStatus());
            highestRated.setText("Ratings Rank: " + mParam1.getHighestRatedRankings());
            mostPopular.setText("Popularity Rank: " + mParam1.getMostPopularRankings());
            averageScore.setText("Average Score " + mParam1.getAverageScore() + "/100");
            ratingBar.setRating((mParam1.getAverageScore() / 100f) * 5);
            String url = mParam1.getCoverImage();

            ImageLoader.LoadImage(url, cover, NetworkPolicy.OFFLINE);
            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFavourite(mParam1);
                }
            });
            //Sets up the chapters buttons at the bottom of the summary view
            //The api call only pulls 10 chapters so this chapter array is just a quick
            //way to pass in that number of chapters.
            int[] chapters = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
            RecyclerView recyclerView = view.findViewById(R.id.recyclerview_summary_chapters);
            ButtonAdapter buttonAdapter = new ButtonAdapter(chapters, mParam1.id);
            recyclerView.setAdapter(buttonAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            title.setText("Title Not available in English or Japanese");
            description.setText("This Manga is not available in English or Japanese");
            status.setText("Status not Available");
            highestRated.setText("Ratings Rank: Not Available");
            mostPopular.setText("Popularity Rank: Not Available");
            cover.setImageResource(R.drawable.ic_outline_image_24);
            averageScore.setText("Average Score Not Available");

        }
    }

    public void addFavourite(Manga manga){
        //function to add a manga to a user's favourites collection.
        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();

        if (account != null){
            db.collection("users/"+account.getUid()+"/favourites")
                    .add(manga)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("Debug", "DocumentSnapshot added with ID: " + documentReference.getId());
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