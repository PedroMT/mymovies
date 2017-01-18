package br.com.pmtsouza.mymovies.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.pmtsouza.mymovies.R;
import br.com.pmtsouza.mymovies.adapters.SearchMovieAdapter;
import br.com.pmtsouza.mymovies.models.Movie;
import br.com.pmtsouza.mymovies.requests.MovieRequest;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class SearchActivity extends AppCompatActivity implements  SearchMovieAdapter.Listener{

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.search_editText)
    EditText mSearchEditText;
    @Bind(R.id.search_recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.search_button)
    ImageButton mSearchButton;

    private Context mContext;
    private ProgressDialog progressDialog;
    private SearchMovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.search_title));
        }

        ButterKnife.bind(this);

        mContext = this;

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext,2);
        mRecyclerView.setLayoutManager(layoutManager);

        mSearchEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    onSearch(v);

                    return true;
                }

                return false;
            }
        });
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


    @OnClick(R.id.search_button)
    public void onSearch(View v){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        String title = mSearchEditText.getText().toString().replaceAll("\\s+$", "");
        if(!title.equals("")){
            new SearchTask(title).execute();
        }
    }

    @Override
    public void onClick(final Movie movie) {
        new MaterialDialog.Builder(mContext)
                .title(getResources().getString(R.string.general_warning))
                .content(getResources().getString(R.string.search_dialog_content))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new GetMovieTask(movie.getImdbID()).execute();
                    }
                })
                .positiveText(getResources().getString(R.string.general_yes))
                .negativeText(getResources().getString(R.string.general_no))
                .canceledOnTouchOutside(false)
                .build()
                .show();
    }

    private class Wrapper
    {
        public Boolean success = false;
        public String message = getResources().getString(R.string.snackbar_unknown_error);
        public ArrayList<Movie> mList = null;
    }

    public class SearchTask extends AsyncTask<Void, Void, Wrapper> {

        private String title;
        private int totalResults;

        SearchTask(String title) {
            this.title = title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(getResources().getString(R.string.general_searching));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Wrapper doInBackground(Void... params) {
            Wrapper data = new Wrapper();


            String response = MovieRequest.search(mContext, title, 0);

            if (response.equals(getResources().getString(R.string.snackbar_connectionfailure_error))
                    || response.equals(getResources().getString(R.string.snackbar_serviceunavailable_error))) {
                data.success = false;
                data.message = response;

                return data;
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("Response")) {

                        JSONArray objects = jsonObject.optJSONArray("Search");

                        if (objects.length() > 0) {
                            data.mList = new ArrayList<>();
                            for (int i = 0; i < objects.length(); i++) {
                                JSONObject movieJSON = objects.getJSONObject(i);

                                Realm realm = Realm.getDefaultInstance();
                                if(realm.where(Movie.class).equalTo("imdbID", movieJSON.getString("imdbID")).findFirst() == null) {
                                    Movie movie = new Movie();
                                    movie.setTitle(movieJSON.getString("Title"));
                                    movie.setPosterhref(movieJSON.getString("Poster"));
                                    movie.setImdbID(movieJSON.getString("imdbID"));

                                    data.mList.add(movie);
                                }
                                realm.close();
                            }

                            totalResults = jsonObject.getInt("totalResults");
                            if(totalResults > 10){

                                int totalPages;
                                if (totalResults > 50) {
                                    totalPages = 5;
                                    data.message = getString(R.string.snackbar_toomanyresults_message);
                                }else if (totalResults % 10 == 0)
                                    totalPages = totalResults / 10;
                                else
                                    totalPages = (totalResults / 10) + 1;

                                for (int p = 2; p <= totalPages; p++) {
                                    String pagination = MovieRequest.search(mContext, title, p);

                                    if (pagination.equals(getResources().getString(R.string.snackbar_connectionfailure_error))
                                        || response.equals(getResources().getString(R.string.snackbar_serviceunavailable_error)))  {

                                        data.success = false;
                                        data.message = pagination;
                                        return data;

                                    } else {

                                        try {
                                            jsonObject = new JSONObject(pagination);

                                            if (jsonObject.getBoolean("Response")) {

                                                objects = jsonObject.optJSONArray("Search");

                                                if (objects.length() > 0) {
                                                    for (int i = 0; i < objects.length(); i++) {
                                                        JSONObject movieJSON = objects.getJSONObject(i);

                                                        Realm realm = Realm.getDefaultInstance();
                                                        if(realm.where(Movie.class).equalTo("imdbID", movieJSON.getString("imdbID")).findFirst() == null) {
                                                            Movie movie = new Movie();
                                                            movie.setTitle(movieJSON.getString("Title"));
                                                            movie.setPosterhref(movieJSON.getString("Poster"));
                                                            movie.setImdbID(movieJSON.getString("imdbID"));

                                                            data.mList.add(movie);
                                                        }
                                                        realm.close();
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            return data;
                                        }
                                    }
                                }
                            }

                            data.success = true;
                            return data;
                        }

                    } else {
                        data.message = getResources().getString(R.string.snackbar_movienotfound_error);
                        return data;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    return data;
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(Wrapper data) {
            super.onPostExecute(data);

            progressDialog.dismiss();

            if(!data.success){
                mAdapter.clearList();
                mAdapter.notifyDataSetChanged();
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, data.message, BaseTransientBottomBar.LENGTH_LONG);
                snackbar.show();
            }else{
                mAdapter = new SearchMovieAdapter(getApplicationContext(), SearchActivity.this,data.mList);
                mRecyclerView.setAdapter(mAdapter);
                if(totalResults > 50){
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, data.message, BaseTransientBottomBar.LENGTH_LONG);
                    snackbar.show();
                }
            }

        }
    }

    public class GetMovieTask extends AsyncTask<Void, Void, Wrapper>{

        private String imdbID;

        GetMovieTask(String imdbID) {
            this.imdbID = imdbID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(getResources().getString(R.string.general_insertingmovie));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Wrapper doInBackground(Void... params) {
            Wrapper data = new Wrapper();


            String response = MovieRequest.get(mContext, imdbID);

            if (response.equals(getResources().getString(R.string.snackbar_connectionfailure_error))
                    || response.equals(getResources().getString(R.string.snackbar_serviceunavailable_error))){
                data.success = false;
                data.message = response;

                return data;
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("Response")) {



                        Movie.create(jsonObject.getString("Title"),
                                jsonObject.getString("imdbID"),
                                jsonObject.optInt("Year", 0),
                                jsonObject.getString("Rated"),
                                jsonObject.getString("Released"),
                                jsonObject.getString("Runtime"),
                                jsonObject.getString("Genre"),
                                jsonObject.getString("Director"),
                                jsonObject.getString("Writer"),
                                jsonObject.getString("Actors"),
                                jsonObject.getString("Plot"),
                                jsonObject.getString("Language"),
                                jsonObject.getString("Country"),
                                jsonObject.getString("Poster"),
                                jsonObject.optInt("Metascore", 0),
                                jsonObject.optDouble("imdbRating", 0),
                                jsonObject.getString("Type"));

                        data.success = true;
                        data.message =getResources().getString(R.string.general_addedsuccesfully);
                    }
                } catch (JSONException e) {
                    return data;
                }
            }
            return data;
        }


        @Override
        protected void onPostExecute(Wrapper data) {
            super.onPostExecute(data);

            progressDialog.dismiss();

            if(!data.success){
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, data.message, BaseTransientBottomBar.LENGTH_LONG);
                snackbar.show();
            }else{
                mAdapter.removeItem(imdbID);
                mAdapter.notifyDataSetChanged();
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, data.message, BaseTransientBottomBar.LENGTH_LONG);
                snackbar.show();
            }

        }
    }

}
