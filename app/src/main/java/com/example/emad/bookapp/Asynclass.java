package com.example.emad.bookapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by EMAD on 12/2/2017.
 */
public class Asynclass extends AsyncTaskLoader<List<result_object>> {

    String hurl;

    public Asynclass(Context context, String url) {
        super(context);
        hurl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<result_object> loadInBackground() {
        if (hurl == null) {
            return null;
        }
        List<result_object> books = Generalclass.fetch(hurl);
        return books;
    }
}

