package com.bcit.manga_stone.http_handlers;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Callable;

public class HttpHandler<T> implements Callable<T> {

    private static final String TAG = HttpHandler.class.getSimpleName();

    protected final String reqUrl;

    /**
     * Creates a new HttpHandler, requires the api url.
     * @param reqUrl - String
     */
    public HttpHandler(String reqUrl)
    {
        this.reqUrl = reqUrl;
    }

    // This code will be running on a separate thread
    @Override
    public T call() {
        T response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET"); //GET is default

            //read the response
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

            response = (T) convertStreamToString(inputStream);

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    protected String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
