package com.freethumbnailmaker.nowatermark.callApi;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getClient(String str) {
        OkHttpClient build = new OkHttpClient.Builder().connectTimeout(60000, TimeUnit.MILLISECONDS).build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().client(build).baseUrl(str).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
