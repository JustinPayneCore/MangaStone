package com.bcit.manga_stone.requests;

import com.bcit.manga_stone.http_handlers.AnilistHttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AnilistApiRequester {

    /**
     * Get rankings of the manga
     * @param mangaId
     * @return
     * @throws JSONException
     */
    public JSONObject GetRankings(String mangaId) throws JSONException {
        String baseUrl = "https://graphql.anilist.co";
        String query = "{Media (id:"+mangaId+", type: MANGA) {averageScore meanScore rankings {id rank type format year context}}}";

        Future<String> f = CreateHttpRequestForString(
                baseUrl, query
        );

        String str = null;
        try {
            str = f.get(); // blocker Method
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ParseJson((str));
    }

    /**
     * Get anilist data
     * @param url
     * @param query
     * @return
     */
    private Future<String> CreateHttpRequestForString(String url, String query) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AnilistHttpHandler<String> httpHandler = new AnilistHttpHandler<String>(url, query);
        return executor.submit(httpHandler);
    }

    /**
     * Parse string to json objects
     * @param str
     * @return
     * @throws JSONException
     */
    private JSONObject ParseJson(String str) throws JSONException {
        JSONObject mainObject = new JSONObject(str);
        return mainObject;
    }
}
