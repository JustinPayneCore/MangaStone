package com.bcit.manga_stone.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bcit.manga_stone.MainActivity;
import com.bcit.manga_stone.R;
import com.bcit.manga_stone.fragments.ChapterFragment;


public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ViewHolder> {

    private int[] localDataSet;
    private String mangaID;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button button;

        public ViewHolder(View view) {
            super(view);

            button = view.findViewById(R.id.button_button_chapters);
        }

        public Button getButton() {
            return button;
        }
    }

    public ButtonAdapter(int[] dataSet, String id) {
        localDataSet = dataSet;
        mangaID = id;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_button, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        //Creates the button text string
        String buttonText = "Chapter " + localDataSet[position];

        //set the text and onclick method for button to send you to the specified chapter.
        viewHolder.getButton().setText(buttonText);
        viewHolder.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = ((MainActivity)view.getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentContainerView_main_mainPages, ChapterFragment.newInstance(mangaID, localDataSet[position]));
                ft.commit();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}