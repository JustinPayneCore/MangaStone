package com.bcit.manga_stone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bcit.manga_stone.object_classes.Manga;
import com.bcit.manga_stone.adapters.MangaAdapter;
import com.bcit.manga_stone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Use the FavouritesFragment newInstance factory method to
 * create an instance of this fragment.
 */
public class FavouritesFragment extends Fragment {

    // the fragment initialization parameters
    FirebaseFirestore db;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment. This fragment requires no parameters.
     * @return A new instance of fragment FavouritesFragment.
     */
    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Checks the firebase accounts favourites collection and uses it to generate a list of
        //mangas. Uses that list of mangas to populate the favourites fragment. This display is
        //different depending on the user's favourites.
        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
        if (account != null) {
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
                            // Creates the recycler view for the favourites fragment
                            Manga[] favouriteMangas = mangas.toArray(new Manga[mangas.size()]);
                            RecyclerView recyclerView = view.findViewById(R.id.recyclerView_favourites);
                            MangaAdapter mangaAdapter = new MangaAdapter(favouriteMangas);
                            recyclerView.setAdapter(mangaAdapter);
                            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        }
                    });
        }
    }
}