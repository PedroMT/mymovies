package br.com.pmtsouza.mymovies.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by indb on 7/11/16.
 */
public class CookieHandler implements CookieJar {

    private static String separator = "#####";
    private Context mContext;

    public CookieHandler(Context context){
        this.mContext = context;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        SharedPreferences sp = mContext.getSharedPreferences("Login", 0);
        SharedPreferences.Editor Ed = sp.edit();
        String cookiesString = "";
        for (Cookie cookie : cookies){
            cookiesString += separator + cookie.toString();
        }
        Ed.putString("cookies", cookiesString.substring(separator.length()));
        Ed.commit();
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List cookies = new ArrayList<>();
        SharedPreferences sp = mContext.getSharedPreferences("Login", 0);
        String cookiesString = sp.getString("cookies", "");
        if (!cookiesString.isEmpty()){
            List<String> cookieStringList = new ArrayList<>(Arrays.asList(cookiesString.split(separator)));
            for (String cookieString : cookieStringList){
                cookies.add(Cookie.parse(url, cookieString));
            }
        }

        return cookies;
    }
}