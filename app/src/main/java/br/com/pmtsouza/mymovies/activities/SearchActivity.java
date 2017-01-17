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
            actionBar.setTitle("Buscar Filmes");
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

        String title = mSearchEditText.getText().toString();
        if(!title.equals("")){
            new SearchTask(title).execute();
        }
    }

    @Override
    public void onClick(final Movie movie) {
        //Toast.makeText(mContext, "Filme/Serie: "+movie.getTitle()+" adicionado à sua lista!", Toast.LENGTH_SHORT).show();
        new MaterialDialog.Builder(mContext)
                .title("Aviso")
                .content("Tem certeza que deseja adicionar o filme à sua lista?")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new GetMovieTask(movie.getImdbID()).execute();
                    }
                })
                .positiveText("Sim")
                .negativeText("Não")
                .canceledOnTouchOutside(false)
                .build()
                .show();
    }

    private class Wrapper
    {
        public Boolean success = false;
        public String message = "Erro desconhecido.";
        public ArrayList<Movie> mList = null;
    }

    public class SearchTask extends AsyncTask<Void, Void, Wrapper> {

        private String title;

        SearchTask(String title) {
            this.title = title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Pesquisando, aguarde...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Wrapper doInBackground(Void... params) {
            Wrapper data = new Wrapper();


            String response = MovieRequest.search(mContext, title, 0);

            if (response.equals("Falha na conexão.")) {
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

                                Movie movie = new Movie();
                                movie.setTitle(movieJSON.getString("Title"));
                                movie.setPosterhref(movieJSON.getString("Poster"));
                                movie.setImdbID(movieJSON.getString("imdbID"));

                                data.mList.add(movie);
                            }

                            int totalResults = jsonObject.getInt("totalResults");
                            if (totalResults > 150) {

                                data.success = false;
                                data.message = "A busca não suporta o número de resultados, por favor seja mais específico.";

                                return data;

                            }else if(totalResults > 10){

                                int totalPages;
                                if (totalResults % 10 == 0)
                                    totalPages = totalResults / 10;
                                else
                                    totalPages = (totalResults / 10) + 1;

                                for (int p = 2; p <= totalPages; p++) {
                                    String pagination = MovieRequest.search(mContext, title, p);

                                    if (pagination.equals("Falha na conexão.")) {
                                        data.success = false;
                                        data.message = response;
                                        return data;
                                    } else {
                                        try {
                                            jsonObject = new JSONObject(pagination);

                                            if (jsonObject.getBoolean("Response")) {

                                                objects = jsonObject.optJSONArray("Search");

                                                if (objects.length() > 0) {
                                                    for (int i = 0; i < objects.length(); i++) {
                                                        JSONObject movieJSON = objects.getJSONObject(i);

                                                        Movie movie = new Movie();
                                                        movie.setTitle(movieJSON.getString("Title"));
                                                        movie.setPosterhref(movieJSON.getString("Poster"));
                                                        movie.setImdbID(movieJSON.getString("imdbID"));

                                                        data.mList.add(movie);
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
                        data.message = "Filme não encontrado.";
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
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, data.message, BaseTransientBottomBar.LENGTH_LONG);
                snackbar.show();
            }else{
                mAdapter = new SearchMovieAdapter(getApplicationContext(), SearchActivity.this,data.mList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
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
            progressDialog.setMessage("Adicionando filme à lista...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Wrapper doInBackground(Void... params) {
            Wrapper data = new Wrapper();


            String response = MovieRequest.get(mContext, imdbID);

            if (response.equals("Falha na conexão.")) {
                data.success = false;
                data.message = response;

                return data;
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("Response")) {

                        Movie.create(jsonObject.getString("Title"),
                                jsonObject.getString("imdbID"),
                                jsonObject.getInt("Year"),
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
                                jsonObject.getInt("Metascore"),
                                jsonObject.getDouble("imdbRating"),
                                jsonObject.getString("Type"));

                        data.success = true;
                        data.message = "Filme adicionado com sucesso!";
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
                mAdapter.notifyDataSetChanged();
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, data.message, BaseTransientBottomBar.LENGTH_LONG);
                snackbar.show();
            }

        }
    }

}
