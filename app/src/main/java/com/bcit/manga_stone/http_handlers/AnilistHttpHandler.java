package com.bcit.manga_stone.http_handlers;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Callable;

public class AnilistHttpHandler<T> extends HttpHandler<T> implements Callable<T>  {

    private static final String TAG = HttpHandler.class.getSimpleName();

    private final String query;

    /**
     * Creates an AnilistHttpHandler, requires the api's url and the query.
     * @param reqUrl - String
     * @param query - String
     */
    public AnilistHttpHandler(String reqUrl, String query) {
        super(reqUrl);
        this.query = query;
    }

    // This code will be running on a separate thread
    @Override
    public T call() {
        T response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");

            // POST request to GraphQL

            JSONObject paramObject = new JSONObject();
            paramObject.put("query", query);

            OutputStream os = urlConnection.getOutputStream();
            os.write(paramObject.toString().getBytes("UTF-8"));
            os.close();

            InputStream _is;
            if (urlConnection.getResponseCode() / 100 == 2) { // 2xx code means success
                _is = urlConnection.getInputStream();
            } else {

                _is = urlConnection.getErrorStream();
            }

            // read the response
            response = (T) convertStreamToString(_is);

            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }
}
