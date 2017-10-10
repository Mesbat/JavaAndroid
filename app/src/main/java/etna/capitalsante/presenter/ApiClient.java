package etna.capitalsante.presenter;

import android.content.Context;

import etna.capitalsante.Interceptors.HeaderInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Nouri on 21/04/2017.
 */

public class ApiClient {
    private static Retrofit retrofit = null;
    public static int unique_id;

    public static Retrofit getClient(String baseUrl, Context context) {
        if (retrofit==null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new HeaderInterceptor(context));
            OkHttpClient client = httpClient.build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
