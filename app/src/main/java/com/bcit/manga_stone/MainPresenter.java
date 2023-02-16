package com.bcit.manga_stone;

import com.bcit.manga_stone.object_classes.Manga;
import com.bcit.manga_stone.requests.AnilistApiRequester;
import com.bcit.manga_stone.requests.MangadexApiRequester;

public class MainPresenter {

    private final MainModel mainModel;
    private final View view;
    private final MangadexApiRequester mangadexApi;
    private final AnilistApiRequester anilistApi;

    /**
     *  Create a presenter class
     * @param mainModel
     * @param view
     */
    public MainPresenter(MainModel mainModel, View view) {
        this.mainModel = mainModel;
        this.view = view;
        this.mangadexApi = new MangadexApiRequester();
        this.anilistApi = new AnilistApiRequester();
    }

    public interface View{
        void UpdateBrowseFragmentRecycler(Manga[] mangas);
        void ToggleLoadingBar(boolean hide);
        void showFilterAlertDialog(int checkedItem, String currentSearchTerm);
        void showGenreAlertDialog(boolean[] checkedItems, String currentSearchTerm);
    }

    // Gets manga data using the model class and update the view with the manga
    public void GetMangaArray(String searchTerm) {
        this.view.ToggleLoadingBar(false);
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                MainPresenter.this.view.ToggleLoadingBar(true);
                MainPresenter.this.view.UpdateBrowseFragmentRecycler(MainPresenter.this.mainModel.GetMangaArray());
            }
        };
        this.mainModel.SearchForManga(searchTerm, mangadexApi, anilistApi, callback);
    }

    // Getters and setters to modify stored data from the model

    public void GetFilteredItem() {
        int item = this.mainModel.GetFilterItemSelected();
        String searchTerm = this.mainModel.GetCurrentSearchTerm();
        this.view.showFilterAlertDialog(item, searchTerm);
    }

    public void GetCheckedGenres() {
        boolean[] checkedGenres = this.mainModel.GetCheckedGenres();
        String searchTerm = this.mainModel.GetCurrentSearchTerm();
        this.view.showGenreAlertDialog(checkedGenres, searchTerm);
    }

    public void SetFilter(int item, String option) {
        this.mainModel.SetFilterOption(option);
        this.mainModel.SetFilterItemSelected(item);
    }

    public void SetGenre(int index, boolean b) {
        this.mainModel.SetCheckedGenres(index, b);
    }
}
