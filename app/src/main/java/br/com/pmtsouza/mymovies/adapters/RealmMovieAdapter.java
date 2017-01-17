package br.com.pmtsouza.mymovies.adapters;

import android.content.Context;

import br.com.pmtsouza.mymovies.models.Movie;
import io.realm.RealmResults;

/**
 * Created by Pedro M. on 17/01/2017.
 */

public class RealmMovieAdapter extends RealmModelAdapter<Movie> {
    public RealmMovieAdapter(Context context, RealmResults<Movie> realmResults) {
        super(context, realmResults);
    }
}