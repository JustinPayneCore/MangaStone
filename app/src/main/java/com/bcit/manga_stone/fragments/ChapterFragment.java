package com.bcit.manga_stone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bcit.manga_stone.adapters.ChapterAdapter;
import com.bcit.manga_stone.R;
import com.bcit.manga_stone.requests.MangadexApiRequester;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Use the ChapterFragment newInstance factory method to
 * create an instance of this fragment.
 */
public class ChapterFragment extends Fragment {

    // the fragment initialization parameters
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private int mParam2;

    public ChapterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ChapterFragment.
     */
    public static ChapterFragment newInstance(String param1, int param2) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chapter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MangadexApiRequester mangadexApi = new MangadexApiRequester();
        try {
            //Sends a request for the specified chapter and uses the response to populate the
            //Chapter Fragment with all the pages of the specified manga chapter.
            JSONObject mangaChapter = mangadexApi.GetMangaChapterList(mParam1);
            String[] imageUrlList = mangadexApi.GetChapterUrlList(mangaChapter, mParam2-1);
            RecyclerView recyclerView = view.findViewById(R.id.recyclerview_chapter);
            ChapterAdapter chapterAdapter = new ChapterAdapter(imageUrlList);
            recyclerView.setAdapter(chapterAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}