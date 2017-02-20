package com.flicker;

import com.google.gson.Gson;

import java.lang.reflect.Type;
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
                        parameters.put("extras", "url_m,url_c");
                        parameters.put("safe_search", "1");

                        String urlString = buildUrl("flickr.photos.search", parameters);

                        Buffer buffer = makeRequest("GET", urlString);

                        PhotoResponse photoResponse = parseResponse(buffer, PhotoResponse.class);

                        photos.addAll(photoResponse.photos.photo);

                        return photos;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PhotoInfoResponse> getPhotoInfo(final Long photoId) {
        return Observable.just(photoId)
                .subscribeOn(Schedulers.io())
                .map(new Function<Long, PhotoInfoResponse>() {
                    @Override
                    public PhotoInfoResponse apply(Long aLong) throws Exception {
                        HashMap<String, String> parameters = new HashMap<>();

                        parameters.put("photo_id", photoId.toString());

                        String urlString = buildUrl("flickr.photos.getInfo", parameters);

                        Buffer buffer = makeRequest("GET", urlString);

                        return parseResponse(buffer, PhotoInfoResponse.class);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Buffer makeRequest(String method, String url) throws Exception {
        HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(url).openConnection();

        urlConnection.setRequestMethod(method);

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

            return buffer;
        } finally {
            urlConnection.disconnect();
        }
    }

    private <T> T parseResponse(Buffer buffer, Type type) {
        String responseString = buffer.readString(StandardCharsets.UTF_8);

        responseString = responseString.substring(14, responseString.length() - 1);

        return gson.fromJson(responseString, type);
    }

    private String buildUrl(String method, HashMap<String, String> parameters) {
        StringBuilder stringBuilder = new StringBuilder("https://api.flickr.com/services/rest/?method=");

        stringBuilder.append(method);

        stringBuilder.append("&api_key=b3380a67070b4cb848414a17c9b58433&format=json");

        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            stringBuilder.append(String.format("&%s=", parameter.getKey()));
            stringBuilder.append(parameter.getValue());
        }

        return stringBuilder.toString();
    }
}
