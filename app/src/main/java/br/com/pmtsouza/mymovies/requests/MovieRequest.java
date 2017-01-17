package br.com.pmtsouza.mymovies.requests;

import android.content.Context;

import java.io.IOException;

import br.com.pmtsouza.mymovies.Constants;
import br.com.pmtsouza.mymovies.utils.CookieHandler;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Pedro M. on 17/01/2017.
 */

public class MovieRequest {

    public static String search(Context context, String title, int page){
        try{
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new CookieHandler(context))
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
            }else{
                return "Falha na conexão";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Falha na conexão.";
        }
    }

    public static String get(Context context, String imdbId){
        try{
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new CookieHandler(context))
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
            }else{
                return "Falha na conexão";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Falha na conexão.";
        }
    }
}
