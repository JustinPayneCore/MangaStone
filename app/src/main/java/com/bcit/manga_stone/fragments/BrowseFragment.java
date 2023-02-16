package com.bcit.manga_stone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bcit.manga_stone.object_classes.Manga;
import com.bcit.manga_stone.adapters.MangaAdapter;
import com.bcit.manga_stone.R;

/**
 * Use the BrowseFragment newInstance factory method to
 * create an instance of this fragment.
 */
public class BrowseFragment extends Fragment {

    // the fragments initialization parameters
    private static final String ARG_PARAM1 = "param1";

    private Manga[] mParam1;

    public BrowseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AboutFragment.
     */
    public static BrowseFragment newInstance(Manga[] param1) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Manga[]) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Creates the recyclerview for the Browse fragment
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_browse);
        MangaAdapter mangaAdapter = new MangaAdapter(mParam1);
        recyclerView.setAdapter(mangaAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }
}