package com.flicker;

import android.util.Log;

import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by ner on 2/18/17.
 */

public class FlickerService {

    private static FlickerService instance;

    private Gson gson = new Gson();

    private FlickerService() {

    }

    public static FlickerService instance() {
        if (instance == null)
            instance = new FlickerService();

        return instance;
    }

    public Observable<List<Photo>> getImages(final String query) {
        return Observable.just(query)
                .subscribeOn(Schedulers.io())
                .map(new Function<Object, List<Photo>>() {
                    @Override
                    public List<Photo> apply(Object o) throws Exception {
                        List<Photo> photos = new ArrayList<>();

                        HashMap<String, String> parameters = new HashMap<>();

                        parameters.put("text", query);
                        parameters.put("extras", "url_m");

                        String urlString = buildUrl("flickr.photos.search", parameters);

                        HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(urlString).openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setConnectTimeout(10000);
                        urlConnection.setReadTimeout(10000);

                        try {
                            urlConnection.connect();

                            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                                throw new IllegalStateException("RequestFailed. Request code " + urlConnection.getResponseCode());

                            BufferedSource source = Okio.buffer(Okio.source(urlConnection.getInputStream()));

                            Buffer buffer = new Buffer();

                            while (true) {
                                long bytesRead = source.read(buffer, 1024);

                                if (bytesRead == -1) {
                                    break;
                                }
                            }

                            PhotoResponse photoResponse = parsePhotoData(buffer);

                            photos.addAll(photoResponse.photos.photo);
                        } finally {
                            urlConnection.disconnect();
                        }

                        return photos;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private PhotoResponse parsePhotoData(Buffer buffer) {
        String responseString = buffer.readString(StandardCharsets.UTF_8);

        responseString = responseString.substring(14, responseString.length() - 1);

        return gson.fromJson(responseString, PhotoResponse.class);
    }

    private String buildUrl(String method, HashMap<String, String> parameters) {
        StringBuilder stringBuilder = new StringBuilder("https://api.flickr.com/services/rest/?method=");

        stringBuilder.append(method);

        stringBuilder.append("&api_key=b3380a67070b4cb848414a17c9b58433&format=json&save_search=1");

        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            stringBuilder.append(String.format("&%s=", parameter.getKey()));
            stringBuilder.append(parameter.getValue());
        }

        return stringBuilder.toString();
    }
}
