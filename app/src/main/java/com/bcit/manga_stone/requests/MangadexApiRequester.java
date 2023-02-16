package com.bcit.manga_stone.requests;

import com.bcit.manga_stone.http_handlers.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MangadexApiRequester {

    /**
     * Returns the manga's cover image url from mangadex.
     * @param mangaId - String
     * @param index - int
     * @return - String
     * @throws JSONException
     */
    public String GetMangaCoverImageUrl(String mangaId, int index) throws JSONException {
        String baseUrl = "https://api.mangadex.org/cover?manga[]=";
        String coverBaseUrl = "https://uploads.mangadex.org/covers";

        Future<String> f = CreateHttpRequestForString(
                (baseUrl + mangaId)
        );

        String str = null;
        try {
            str = f.get(); // blocker Method
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = ParseJson(str);
        String fileName = jsonObject.getJSONArray("data").getJSONObject(index).getJSONObject("attributes").getString("fileName");
        String url = coverBaseUrl + "/" + mangaId + "/" + fileName;

        return url;
    }

    /**
     * Returns ten manga entries that match the search term. Is based off three different searching
     * parameters, the general search term, filter options, and tags.
     * @param searchTerm - String
     * @param filterOption - String
     * @param includedTags - String
     * @return - JSONArray
     * @throws JSONException
     */
    public JSONArray SearchManga(String searchTerm, String filterOption, String includedTags) throws JSONException {
        String baseUrl = "https://api.mangadex.org/manga?contentRating[]=safe&order[" + filterOption + "]=desc" + includedTags;

        // Used when query an empty string and it needs to return the most popular titles
        String term = searchTerm.replace("\u00A0", "");

        Future<String> f;
        if(term.trim().isEmpty()) {
            f = CreateHttpRequestForString(
                    (baseUrl)
            );
        } else {
            f = CreateHttpRequestForString(
                    (baseUrl + "&title=" + term)
            );
        }

        String str = null;
        try {
            str = f.get(); // blocker Method
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = ParseJson(str);
        return jsonObject.getJSONArray("data");

    }

    public String[] GetChapterUrlList(JSONObject chapterData, int chapterNum) throws JSONException {
        JSONObject hostUrl = GetChapterHostURL();

        String baseURL = hostUrl.getString("baseUrl");
        JSONArray chapterDataArray = chapterData.getJSONArray("data");
        JSONObject chapterInfo = chapterDataArray.getJSONObject(chapterNum);

        JSONObject chapterAttributes = chapterInfo.getJSONObject("attributes");
        String chapterHash = chapterAttributes.getString("hash");


        // create all pages for a single chapter
        JSONArray pages = chapterAttributes.getJSONArray("dataSaver");
        String[] allPages = new String[pages.length()];

        for(int i = 0; i < pages.length(); i++){
            String singlePage = pages.getString(i);

            allPages[i] = baseURL + "/data-saver/" + chapterHash + "/" + singlePage;
        }
        return allPages;
    }

    /**
     * Returns the manga's Id.
     * @param mangaDataArray - JSONArray
     * @param index - int
     * @return - String
     * @throws JSONException
     */
    public String getMangaId(JSONArray mangaDataArray, int index) throws JSONException {
        JSONObject mangaAttributes = mangaDataArray.getJSONObject(index);
        return mangaAttributes.getString("id");
    }

    /**
     * Returns the manga's unique Anilist Id.
     * @param mangaDataArray - JSONArray
     * @param index - int
     * @return - String
     * @throws JSONException
     */
    public String GetAnilistMangaId(JSONArray mangaDataArray, int index) throws JSONException {
        JSONObject mangaAttributes = mangaDataArray.getJSONObject(index).getJSONObject("attributes");
        JSONObject mangaLinks = mangaAttributes.optJSONObject("links");
        if(mangaLinks != null) {
            return mangaLinks.optString("al");
        } else{
            return null;
        }

    }

    /**
     * Returns the manga's chapter host url.
     * @return - JSONObject
     * @throws JSONException
     */
    private JSONObject GetChapterHostURL() throws JSONException {
        String baseUrl = "https://api.mangadex.org/at-home/server/";
        String chapterId = "5d089bf9-64ef-48c7-883d-845cda90b416";

        Future<String> f = CreateHttpRequestForString(
                (baseUrl + chapterId)
        );

        String str = "";
        try {
            str = f.get(); // blocker Method
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ParseJson(str);
    }

    /**
     * Gets a list of manga's chapters, only pulls 10 per api call. Uses a manga's id to
     * pull the chapter data.
     * @param mangaId - String
     * @return - JSONObject
     * @throws JSONException
     */
    public JSONObject GetMangaChapterList(String mangaId) throws JSONException{
        // Get List of a manga's chapters
        String baseUrl = "https://api.mangadex.org/chapter?manga=";
        Future<String> f = CreateHttpRequestForString(
                (baseUrl + mangaId + "&translatedLanguage[]=en")
        );

        String str = null;
        try {
            str = f.get(); // blocker Method
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ParseJson(str);
    }

    private Future<String> CreateHttpRequestForString(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        HttpHandler<String> httpHandler = new HttpHandler<String>(url);
        return executor.submit(httpHandler);
    }

    private JSONObject ParseJson(String str) throws JSONException {
        JSONObject mainObject = new JSONObject(str);
        return mainObject;
    }

    /**
     * Grabs the 10 most popular mangas from the api.
     * @return - JSONArray
     * @throws JSONException
     */
    public JSONArray GetPopularManga() throws JSONException {
        String baseUrl = "https://api.mangadex.org/manga?order[followedCount]=desc";

        Future<String> f = CreateHttpRequestForString((baseUrl));

        String str = null;
        try {
            str = f.get(); // blocker Method
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = ParseJson(str);
        return jsonObject.getJSONArray("data");
    }

    /**
     * Returns the manga's title in english or japanese depending on what is available. If
     * no title is available returns an empty string.
     * @param mangaDataArray - JSONArray
     * @param index - int
     * @return - String
     * @throws JSONException
     */
    public String getMangaTitle(JSONArray mangaDataArray, int index) throws JSONException {
        JSONObject mangaAttributes = mangaDataArray.getJSONObject(index).getJSONObject("attributes").getJSONObject("title");
        if (mangaAttributes.has("en")){
            return mangaAttributes.getString("en");
        } else if (mangaAttributes.has("jp")) {
            return mangaAttributes.getString("jp");
        } else {
            return "Title not Available";
        }
    }

    /**
     * Returns the manga's description in english or japanese depending on what is available. If
     * no description is available returns an empty string.
     * @param mangaDataArray - JSONArray
     * @param index - int
     * @return - String
     * @throws JSONException
     */
    public String getMangaDescription(JSONArray mangaDataArray, int index) throws JSONException {
        try{
            JSONObject mangaAttributes = mangaDataArray.getJSONObject(index).getJSONObject("attributes").getJSONObject("description");
            if (mangaAttributes.has("en")){
                return mangaAttributes.getString("en");
            } else if (mangaAttributes.has("jp")){
                return mangaAttributes.getString("jp");
            } else {
                return "";
            }
        } catch(JSONException e) {
            return "";
        }
    }

    /**
     * Returns the manga's publishing status from the JSONArray.
     * @param mangaDataArray - JSONArray
     * @param index - int
     * @return - String
     * @throws JSONException
     */
    public String getMangaStatus(JSONArray mangaDataArray, int index) throws JSONException {
        JSONObject mangaAttributes = mangaDataArray.getJSONObject(index).getJSONObject("attributes");
        return mangaAttributes.getString("status");
    }
}
