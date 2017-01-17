package br.com.pmtsouza.mymovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.pmtsouza.mymovies.models.Movie;
import io.realm.Realm;

import br.com.pmtsouza.mymovies.R;
/**
 * Created by Pedro M. on 17/01/2017.
 */

public class MovieAdapter extends RealmRecyclerViewAdapter<Movie>{

    private Context context;
    private Listener mListener;

    public interface Listener{
        void onClick(Movie movie);
    }


    public MovieAdapter(Context context, Activity activity){
        this.context = context;
        mListener = (Listener) activity;
    }

    private class MovieViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public ImageView posterView;
        public TextView titleView;

        public MovieViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            posterView = (ImageView) view.findViewById(R.id.movie_poster);
            titleView = (TextView) view.findViewById(R.id.movie_title);
        }

        @Override
        public void onClick(View v) {
            Movie movie = getItem(getLayoutPosition());
            mListener.onClick(movie);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(v);
    }

    public void clearRow(MovieViewHolder mvh) {
        mvh.posterView.setImageDrawable(context.getResources().getDrawable(R.drawable.broken_link));
        mvh.titleView.setText("");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        MovieViewHolder holder = (MovieViewHolder) viewHolder;
        clearRow(holder);

        Movie movie = getItem(i);

        if(movie.getPosterhref() != null && !movie.getPosterhref().equals("") && !movie.getPosterhref().equals("N/A"))
            Picasso.with(context).load(movie.getPosterhref()).placeholder(R.drawable.broken_link).resize(150,225).into(holder.posterView);
        else{
            Picasso.with(context).load(R.drawable.no_poster);
        }
        holder.titleView.setText(movie.getTitle());

    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }
}
