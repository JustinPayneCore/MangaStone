package com.bcit.manga_stone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.bcit.manga_stone.fragments.BrowseFragment;
import com.bcit.manga_stone.fragments.FavouritesFragment;
import com.bcit.manga_stone.fragments.HistoryFragment;
import com.bcit.manga_stone.fragments.MoreFragment;
import com.bcit.manga_stone.object_classes.Manga;
import com.bcit.manga_stone.utils.AnonymousAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements MainPresenter.View{

    private ProgressBar loadingBar;

    MainModel md;
    MainPresenter mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingBar = (ProgressBar)findViewById(R.id.progressBar_main_loading);
        md = new MainModel(getResources().getStringArray(R.array.alertDialog_genre_ids));
        mp = new MainPresenter(md, this);

        // Setup the bottom navigation menu
        SetupBottomNavigation();

        // Get list of popular manga and update the browse recycler view
        mp.GetMangaArray("");

    }

    // Sign in anonymously on app startup
    @Override
    protected void onStart() {
        super.onStart();

        AnonymousAuth.SignInAnonymously();
    }

    // Top bar search bar functionality
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browse_action_bar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.item_browseActionBar_search).getActionView();

        // Setup search view for searching up manga
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mp.GetMangaArray(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s == null || s.isEmpty()) {
                    searchView.setQuery("\u00A0", false);
                }

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mp.GetMangaArray(searchView.getQuery().toString());

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // Top bar filters
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        System.out.println(item.getTitle());

        switch(item.getItemId()) {
            case R.id.item_browseActionBar_sort:
                // Configure filters for searching
                mp.GetFilteredItem();
                break;
            case R.id.item_browseActionBar_filter:
                // Configure genres for searching
                mp.GetCheckedGenres();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Bottom bar functionality
    void SetupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_main_bottomNav);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                ft.setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.fade_out  // popExit
                );

                item.setChecked(true);
                System.out.println(item.getTitle());
                switch(item.getItemId()) {
                    case R.id.menuItem_bottomNav_browse:
                        mp.GetMangaArray("");
                        break;
                    case R.id.menuItem_bottomNav_favourites:
                        ft.replace(R.id.fragmentContainerView_main_mainPages, FavouritesFragment.newInstance());
                        ft.commit();
                        break;
                    case R.id.menuItem_bottomNav_history:
                        ft.replace(R.id.fragmentContainerView_main_mainPages, HistoryFragment.newInstance());
                        ft.commit();
                        break;
                    case R.id.menuItem_bottomNav_more:
                        ft.replace(R.id.fragmentContainerView_main_mainPages, MoreFragment.newInstance());
                        ft.commit();
                        break;
                }
                return false;
            }
        });
    }

    // Update the browse fragment with manga
    @Override
    public void UpdateBrowseFragmentRecycler(Manga[] mangas) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainerView_main_mainPages, BrowseFragment.newInstance(mangas));
        ft.commit();
    }

    @Override
    public void ToggleLoadingBar(boolean hide) {
        if(hide) {
            loadingBar.setVisibility(View.INVISIBLE);
        } else {
            loadingBar.setVisibility(View.VISIBLE);
        }
    }

    // Functionality for filters
    @Override
    public void showFilterAlertDialog(int checkedItem, String currentSearchTerm) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.alertDialog_sortBy);
        alertDialog.setSingleChoiceItems(R.array.alertDialog_filter_options, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mp.SetFilter(0, "followedCount");
                        break;
                    case 1:
                        mp.SetFilter(1, "latestUploadedChapter");
                        break;
                    case 2:
                        mp.SetFilter(2,  "year");
                        break;
                    case 3:
                        mp.SetFilter(3,  "relevance");
                        break;
                }
            }
        });
        alertDialog.setPositiveButton(R.string.alertDialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                mp.GetMangaArray(currentSearchTerm);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    // Functionality for genres
    @Override
    public void showGenreAlertDialog(boolean[] checkedItems, String currentSearchTerm) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.alertDialog_filter);
        alertDialog.setMultiChoiceItems(R.array.alertDialog_genre_options, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                mp.SetGenre(i, b);
            }
        });
        alertDialog.setPositiveButton(R.string.alertDialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                mp.GetMangaArray(currentSearchTerm);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}