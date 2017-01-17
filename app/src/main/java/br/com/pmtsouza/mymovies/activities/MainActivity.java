package br.com.pmtsouza.mymovies.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.pmtsouza.mymovies.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.myList_button)
    Button mMyListButton;
    @Bind(R.id.searchMovies_button)
    Button mSearchMoviesButton;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mContext = this;
    }

    @OnClick(R.id.myList_button)
    public void onMyListClick(View v){
        Intent intent = new Intent(mContext, MyListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.searchMovies_button)
    public void onSearchMoviesClick(View v){
        Intent intent = new Intent(mContext, SearchActivity.class);
        startActivity(intent);
    }

}
