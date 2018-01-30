package com.sa.mynotes;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by deepak on 26/11/17.
 */

public class NotesApplication extends Application {

    public static final String DB_NAME = "Notes.Realm";
    public static final int REALM_SCHEMA = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        initRealm();
    }

    /*
        Initializing realm
     */
    private void initRealm() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().
                name(DB_NAME).schemaVersion(REALM_SCHEMA).
                deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
