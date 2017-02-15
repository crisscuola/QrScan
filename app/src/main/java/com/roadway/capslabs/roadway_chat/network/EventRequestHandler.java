package com.roadway.capslabs.roadway_chat.network;

import android.app.Activity;
import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.roadway.capslabs.roadway_chat.url.UrlFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.url.UrlType.CHECK;

/**
 * Created by kirill on 05.10.16
 */
public class EventRequestHandler {

    public String getCheck(Activity context, String link) {
        HttpUrl url = UrlFactory.getUrl(CHECK).newBuilder()
                .addPathSegment(link).addPathSegment("").build();
        Log.d("check_url", String.valueOf(url));
        Request request = buildRequest(url);
        return getResponse(context, request);
    }


    private Request buildRequest(HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    private String getResponse(Activity context, Request request) {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String resp = response.body().string();


            Log.d("response_create_handler", resp);
            return resp;
        } catch (SocketTimeoutException e) {
            return "Timeout";
            //throw new RuntimeException("Could not load event due to timeout exception" + request.url(), e);
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + request.url(), e);
        }

    }
}
