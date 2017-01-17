package br.com.pmtsouza.mymovies.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import br.com.pmtsouza.mymovies.R;
import br.com.pmtsouza.mymovies.adapters.MovieAdapter;
import br.com.pmtsouza.mymovies.adapters.RealmMovieAdapter;
import br.com.pmtsouza.mymovies.models.Movie;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MyListActivity extends AppCompatActivity implements MovieAdapter.Listener{

    @Bind(R.id.mylist_recyclerView)
    RecyclerView mRecyclerView;

    private Context mContext;
    private Realm realm;
    private MovieAdapter mAdapter;
    private RealmResults<Movie> movies;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.mylist_title));
        }

        ButterKnife.bind(this);
        mContext = this;
        realm = Realm.getDefaultInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SearchActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext,2);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MovieAdapter(mContext, this);
        mRecyclerView.setAdapter(mAdapter);

        movies = realm.where(Movie.class)
                .findAllSortedAsync("title", Sort.DESCENDING);

        RealmMovieAdapter realmAdapter = new RealmMovieAdapter(mContext, movies);
        mAdapter.setRealmAdapter(realmAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(mContext, MovieDetailsActivity.class);
        intent.putExtra("imdbID", movie.getImdbID());
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1010 && requestCode == 1000){
            mAdapter.notifyDataSetChanged();
        }
    }
}
