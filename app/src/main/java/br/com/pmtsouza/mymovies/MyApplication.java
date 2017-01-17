package br.com.pmtsouza.mymovies;

import android.app.Application;

import br.com.pmtsouza.mymovies.models.MyMigration;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Pedro M. on 17/01/2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration
                .Builder(this)
                .name("default")
                .schemaVersion(2)
                .migration(new MyMigration())
                .build();

        Realm.setDefaultConfiguration(config);
    }
}
