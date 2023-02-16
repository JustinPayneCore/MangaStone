package com.bcit.manga_stone;

import android.os.Handler;
import android.os.Looper;

import com.bcit.manga_stone.object_classes.Manga;
import com.bcit.manga_stone.requests.AnilistApiRequester;
import com.bcit.manga_stone.requests.MangadexApiRequester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainModel {

    private final String[] genreIdArray;
    private String filterOption = "followedCount";
    private int filterItemSelected = 0;
    private int genreItemSelected = 0;
    private String currentSearchTerm;
    private boolean[] checkedGenres;
    private Manga[] mangaArray;

    public MainModel(String[] ids) {
        this.genreIdArray = ids;
        this.checkedGenres = new boolean[ids.length];
    }

    // Search for manga using the mangadex api and retrieve extra information using the anilist api
    public void SearchForManga(String searchTerm, MangadexApiRequester mangadexApi, AnilistApiRequester anilistApi, Runnable callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        // Get manga asynchronously with no blocking
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray mangaList;
                    if(searchTerm == null) {
                        mangaList = mangadexApi.SearchManga("", filterOption, getTags());
                    } else {
                        mangaList = mangadexApi.SearchManga(searchTerm, filterOption, getTags());
                    }

                    int lengthOfMangaList = mangaList.length();
                    mangaArray = new Manga[lengthOfMangaList];

                    //Creates a list of manga objects to pass into the the fragments
                    for(int i = 0; i < lengthOfMangaList; i++){
                        String id = mangadexApi.getMangaId(mangaList, i);
                        String title = mangadexApi.getMangaTitle(mangaList, i);
                        String description = mangadexApi.getMangaDescription(mangaList, i);
                        String coverImageUrl = mangadexApi.GetMangaCoverImageUrl(id, 0);
                        String status = mangadexApi.getMangaStatus(mangaList, i);

                        // Get the anilist id and information
                        String anilistId = mangadexApi.GetAnilistMangaId(mangaList, i);
                        // highest rated & most popular rankings
                        int averageScoreData = 0;
                        String highestRatedRakings = "Not Available";
                        String mostPopularRatings = "Not Available";
                        if(anilistId != null) {
                            if (anilistId.isEmpty() == false){
                                JSONObject rankingData = anilistApi.GetRankings(anilistId);
                                if(rankingData.getJSONObject("data").getJSONObject("Media").getInt("averageScore") != 0){
                                    averageScoreData = rankingData.getJSONObject("data").getJSONObject("Media").getInt("averageScore");
                                }
                                JSONArray rankingCategories = rankingData.getJSONObject("data").getJSONObject("Media").getJSONArray("rankings");
                                if(rankingData.getJSONObject("data").getJSONObject("Media").getJSONArray("rankings").length() != 0) {
                                    if (rankingCategories.getJSONObject(0).getString("type").equals("RATED")) {
                                        highestRatedRakings = rankingCategories.getJSONObject(0).getString("rank");
                                        mostPopularRatings = rankingCategories.getJSONObject(1).getString("rank");
                                    } else if (rankingCategories.getJSONObject(0).getString("type").equals("POPULAR")) {
                                        mostPopularRatings = rankingCategories.getJSONObject(0).getString("rank");
                                        highestRatedRakings = "Not Available";
                                    } else {
                                        highestRatedRakings = "Not Available";
                                        mostPopularRatings = "Not Available";
                                    }
                                }
                            }
                        }

                        Manga manga = new Manga(id, title, description, coverImageUrl, status, highestRatedRakings, mostPopularRatings, averageScoreData);
                        mangaArray[i] = manga;
                    }

                    currentSearchTerm = searchTerm;

                } catch(JSONException e) {
                    e.printStackTrace();
                }

                handler.post(callback);
            }
        });
    }

    private String getTags() {
        String tags = "";
        for(int i = 0; i < genreIdArray.length; i++) {
            if(checkedGenres[i]) {
                tags += "&includedTags[]=" + genreIdArray[i];
            }
        }

        return tags;
    }

    // Getters and setters to retrieve and store information

    public void SetFilterItemSelected(int item) {
        filterItemSelected = item;
    }

    public int GetFilterItemSelected() {
        return filterItemSelected;
    }

    public void SetCheckedGenres(int index, boolean bool) {
        this.checkedGenres[index] = bool;
    }

    public boolean[] GetCheckedGenres() {
        return this.checkedGenres;
    }

    public void SetFilterOption(String newOption) {
        this.filterOption = newOption;
    }

    public String GetCurrentSearchTerm() {
        return this.currentSearchTerm;
    }

    public Manga[] GetMangaArray() {
        return this.mangaArray;
    }
}
