package br.com.pmtsouza.mymovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.pmtsouza.mymovies.R;
import br.com.pmtsouza.mymovies.models.Movie;

/**
 * Created by Pedro M. on 17/01/2017.
 */

public class SearchMovieAdapter extends RecyclerView.Adapter<SearchMovieAdapter.SearchMovieViewHolder> {

    private Context context;
    private Listener mListener;
    private ArrayList<Movie> mAdapter;

    public interface Listener{
        void onClick(Movie movie);
    }

    public SearchMovieAdapter(Context context,Activity activity, ArrayList<Movie> list){
        this.context = context;
        mListener = (Listener) activity;
        mAdapter = list;
    }

    public class SearchMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView posterView;
        private TextView titleView;

        public SearchMovieViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            titleView = (TextView)view.findViewById(R.id.movie_title);
            posterView = (ImageView) view.findViewById(R.id.movie_poster);
        }

        @Override
        public void onClick(View v) {
            Movie movie = mAdapter.get(getLayoutPosition());
            mListener.onClick(movie);
        }
    }

    @Override
    public SearchMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new SearchMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchMovieViewHolder holder, int position) {
        clearRow(holder);

        Movie movie = mAdapter.get(position);

        if(movie.getPosterhref() != null && !movie.getPosterhref().equals("") && !movie.getPosterhref().equals("N/A"))
            Picasso.with(context).load(movie.getPosterhref()).placeholder(R.drawable.broken_link).resize(150,225).into(holder.posterView);
        else{
            Picasso.with(context).load(R.drawable.no_poster);
        }
        holder.titleView.setText(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        return mAdapter.size();
    }


    public void clearRow(SearchMovieViewHolder smvh) {
        smvh.posterView.setImageDrawable(context.getResources().getDrawable(R.drawable.no_poster));
        smvh.titleView.setText("");
    }

    public void removeItem(String imdbId){
        for(int i = 0; i < mAdapter.size(); i++){
            if(mAdapter.get(i).getImdbID() == imdbId)
                mAdapter.remove(i);
        }
    }

    public void clearList(){
        mAdapter.clear();
    }

}
