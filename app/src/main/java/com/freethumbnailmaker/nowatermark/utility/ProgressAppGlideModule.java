package com.freethumbnailmaker.nowatermark.utility;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

@GlideModule
public class ProgressAppGlideModule extends AppGlideModule {

    private interface ResponseProgressListener {
        void update(HttpUrl httpUrl, long j, long j2);
    }

    public interface UIonProgressListener {
        float getGranualityPercentage();

        void onProgress(long j, long j2);
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient.Builder().addNetworkInterceptor(new Interceptor() {
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request request = chain.request();
                Response proceed = chain.proceed(request);
                return proceed.newBuilder().body(new OkHttpProgressResponseBody(request.url(), proceed.body(), new DispatchingProgressListener())).build();
            }
        }).build()));
    }

    public static void forget(String str) {
        DispatchingProgressListener.forget(str);
    }

    public static void expect(String str, UIonProgressListener uIonProgressListener) {
        DispatchingProgressListener.expect(str, uIonProgressListener);
    }

    private static class DispatchingProgressListener implements ResponseProgressListener {
        private static final Map<String, UIonProgressListener> LISTENERS = new HashMap();
        private static final Map<String, Long> PROGRESSES = new HashMap();
        private final Handler handler = new Handler(Looper.getMainLooper());

        DispatchingProgressListener() {
        }

        static void forget(String str) {
            LISTENERS.remove(str);
            PROGRESSES.remove(str);
        }

        static void expect(String str, UIonProgressListener uIonProgressListener) {
            LISTENERS.put(str, uIonProgressListener);
        }

        public void update(HttpUrl httpUrl, long j, long j2) {
            String httpUrl2 = httpUrl.toString();
            UIonProgressListener uIonProgressListener = LISTENERS.get(httpUrl2);
            if (uIonProgressListener != null) {
                if (j2 <= j) {
                    forget(httpUrl2);
                }
                if (needsDispatch(httpUrl2, j, j2, uIonProgressListener.getGranualityPercentage())) {
                    final UIonProgressListener uIonProgressListener2 = uIonProgressListener;
                    final long j3 = j;
                    final long j4 = j2;
                    this.handler.post(new Runnable() {
                        public void run() {
                            uIonProgressListener2.onProgress(j3, j4);
                        }
                    });
                }
            }
        }

        private boolean needsDispatch(String str, long j, long j2, float f) {
            if (!(f == 0.0f || j == 0 || j2 == j)) {
                long j3 = (long) (((((float) j) * 100.0f) / ((float) j2)) / f);
                Long l = PROGRESSES.get(str);
                if (l != null && j3 == l.longValue()) {
                    return false;
                }
                PROGRESSES.put(str, Long.valueOf(j3));
            }
            return true;
        }
    }

    private static class OkHttpProgressResponseBody extends ResponseBody {
        private BufferedSource bufferedSource;

        public final ResponseProgressListener progressListener;

        public final ResponseBody responseBody;

        public final HttpUrl url;

        OkHttpProgressResponseBody(HttpUrl httpUrl, ResponseBody responseBody2, ResponseProgressListener responseProgressListener) {
            this.url = httpUrl;
            this.responseBody = responseBody2;
            this.progressListener = responseProgressListener;
        }

        public MediaType contentType() {
            return this.responseBody.contentType();
        }

        public long contentLength() {
            return this.responseBody.contentLength();
        }

        public BufferedSource source() {
            if (this.bufferedSource == null) {
                this.bufferedSource = Okio.buffer(source(this.responseBody.source()));
            }
            return this.bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0;
                @Override
                public long read(Buffer buffer, long j) throws IOException {
                    long read = super.read(buffer, j);
                    long contentLength = OkHttpProgressResponseBody.this.responseBody.contentLength();
                    if (read == -1) {
                        this.totalBytesRead = contentLength;
                    } else {
                        this.totalBytesRead += read;
                    }
                    OkHttpProgressResponseBody.this.progressListener.update(OkHttpProgressResponseBody.this.url, this.totalBytesRead, contentLength);
                    return read;
                }
            };
        }
    }
}
