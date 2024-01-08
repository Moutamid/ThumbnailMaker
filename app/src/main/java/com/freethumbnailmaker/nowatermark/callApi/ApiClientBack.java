package com.freethumbnailmaker.nowatermark.callApi;

import com.freethumbnailmaker.nowatermark.main.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientBack {
    public static final String BASE_URL = Constants.BASE_URL_BG;
    private static Retrofit retrofit1 = null;

    public static Retrofit getClient() {
        if (retrofit1 == null) {
            retrofit1 = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit1;
    }
}
