package com.advantal.shieldcrypt.sip.rest;

import java.util.concurrent.TimeUnit;
import com.advantal.shieldcrypt.BuildConfig;
import com.grapesnberries.curllogger.CurlLoggerInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    public static boolean isMultipartFormApi = false;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                                .addInterceptor(new CurlLoggerInterceptor());

            httpClient =
            httpClient.addInterceptor(logging); // <-- this is the important line!
            httpClient.readTimeout(90, TimeUnit.SECONDS)
                    .connectTimeout(90, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(
            Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

}