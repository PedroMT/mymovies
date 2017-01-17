package br.com.pmtsouza.mymovies.activities;

import android.content.Context;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.pmtsouza.mymovies.R;
import br.com.pmtsouza.mymovies.models.Movie;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MovieDetailsActivity extends AppCompatActivity {


    @Bind(R.id.movie_title)
    TextView mTitleView;
    @Bind(R.id.movie_year_value)
    TextView mYearview;
    @Bind(R.id.movie_rated_value)
    TextView mRatedView;
    @Bind(R.id.movie_released_value)
    TextView mReleasedView;
    @Bind(R.id.movie_runtime_value)
    TextView mRuntimeView;
    @Bind(R.id.movie_genre_value)
    TextView mGenreView;
    @Bind(R.id.movie_director_value)
    TextView mDirectorView;
    @Bind(R.id.movie_writer_value)
    TextView mWriterView;
    @Bind(R.id.movie_actors_value)
    TextView mActorsView;
    @Bind(R.id.movie_plot_value)
    TextView mPlotView;
    @Bind(R.id.movie_language_value)
    TextView mLanguageView;
    @Bind(R.id.movie_country_value)
    TextView mCountryView;
    @Bind(R.id.movie_metascore_value)
    TextView mMetascoreView;
    @Bind(R.id.movie_imdbrating_value)
    TextView mImdbratingView;
    @Bind(R.id.movie_poster)
    ImageView mPosterView;


    private Context mContext;
    private Realm realm;
    Movie mMovie;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moviedetails);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("Detalhes");
        }

        ButterKnife.bind(this);
        mContext = this;
        realm = Realm.getDefaultInstance();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String imdbID = extras.getString("imdbID");

            mMovie = realm.where(Movie.class).equalTo("imdbID", imdbID).findFirst();

            populateDetails();
        }
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

    private void populateDetails(){
        if(mMovie != null){
            mTitleView.setText(mMovie.getTitle());
            Picasso.with(mContext).load(mMovie.getPosterhref()).placeholder(R.drawable.broken_link).into(mPosterView);
            mYearview.setText(String.valueOf(mMovie.getYear()));
            mRatedView.setText(mMovie.getRated());
            mReleasedView.setText(mMovie.getReleased());
            mRuntimeView.setText(mMovie.getRuntime());
            mGenreView.setText(mMovie.getGenre());
            mDirectorView.setText(mMovie.getDirector());
            mWriterView.setText(mMovie.getWriter());
            mActorsView.setText(mMovie.getActors());
            mLanguageView.setText(mMovie.getLanguage());
            mCountryView.setText(mMovie.getCountry());
            mMetascoreView.setText(String.valueOf(mMovie.getMetascore()));
            mImdbratingView.setText(String.valueOf(mMovie.getImdbRating()));
            mPlotView.setText(mMovie.getPlot());
        }
    }
}
