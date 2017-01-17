package br.com.pmtsouza.mymovies.models;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Created by Pedro M. on 17/01/2017.
 */

public class MyMigration implements RealmMigration{

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        //ToDo: in case some migration is needed
    }
}
