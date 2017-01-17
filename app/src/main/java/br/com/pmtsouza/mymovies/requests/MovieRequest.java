package br.com.pmtsouza.mymovies.requests;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.com.pmtsouza.mymovies.Constants;
import br.com.pmtsouza.mymovies.utils.CookieHandler;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import br.com.pmtsouza.mymovies.R;

/**
 * Created by Pedro M. on 17/01/2017.
 */

public class MovieRequest {

    public static String search(Context context, String title, int page){
        try{
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new CookieHandler(context))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            HttpUrl url;
            if(page == 0) {
                url = new HttpUrl.Builder()
                        .scheme("https")
                        .host(Constants.API_URL)
                        .addQueryParameter("s", title)
                        .addQueryParameter("type", "movie")
                        .build();
            }else{
                url = new HttpUrl.Builder()
                        .scheme("https")
                        .host(Constants.API_URL)
                        .addQueryParameter("s", title)
                        .addQueryParameter("page", String.valueOf(page))
                        .addQueryParameter("type", "movie")
                        .build();
            }


            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if(response.code() == 200){
                return response.body().string();
            }else if(response.code() == 503) {
                return context.getResources().getString(R.string.snackbar_connectionfailure_error);
            }else{
                return context.getResources().getString(R.string.snackbar_serviceunavailable_error);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return context.getResources().getString(R.string.snackbar_connectionfailure_error);
        }
    }

    public static String get(Context context, String imdbId){
        try{
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new CookieHandler(context))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host(Constants.API_URL)
                    .addQueryParameter("i", imdbId)
                    .build();


            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if(response.code() == 200){
                return response.body().string();
            }else if(response.code() == 503) {
                return context.getResources().getString(R.string.snackbar_connectionfailure_error);
            }else{
                return context.getResources().getString(R.string.snackbar_serviceunavailable_error);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return context.getResources().getString(R.string.snackbar_connectionfailure_error);
        }
    }
}
