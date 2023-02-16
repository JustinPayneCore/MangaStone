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


import com.bcit.manga_stone.adapters.HistoryItemAdapter;
import com.bcit.manga_stone.object_classes.Manga;
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
 * Use the HistoryFragment newInstance factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // the fragment initialization parameters
    private FirebaseFirestore db;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment. This fragment requires no parameters.
     * @return A new instance of fragment HistoryFragment.
     */
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Checks the firebase accounts history collection and uses it to generate a list of
        //history items. Uses that list of history items to populate the history fragment.
        //This display is different depending on the user's history.
        db = FirebaseFirestore.getInstance();
        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
        if (account != null) {
            List<Manga> mangas = new ArrayList<>();
            db.collection("users/"+account.getUid()+"/history")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
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
                            RecyclerView rv = view.findViewById(R.id.recyclerView_history);
                            Manga[] sampleArr = mangas.toArray(new Manga[mangas.size()]);
                            HistoryItemAdapter adapter = new HistoryItemAdapter(sampleArr, account);
                            rv.setAdapter(adapter);
                            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }

                    });
        }
    }
}