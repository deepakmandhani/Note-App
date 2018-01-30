package com.sa.mynotes.repo;

import com.sa.mynotes.models.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by deepak on 26/11/17.
 */

public class NotesRepoWrapper {

    private static NotesRepoWrapper instance;
    private final Realm mRealm;

    private NotesRepoWrapper() {
        mRealm = Realm.getDefaultInstance();
    }

    public static NotesRepoWrapper getInstance() {
        instance = new NotesRepoWrapper();
        return instance;
    }

    /*
        Fetch all saved notes
     */
    public List<Note> getNotesList() {
        List<Note> list = new ArrayList<>();
        mRealm.beginTransaction();
        try {
            RealmResults<Note> results = mRealm
                    .where(Note.class)
                    .findAll();
            list.addAll(mRealm.copyFromRealm(results));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRealm.commitTransaction();
        return list;
    }

    /*
        Add a new note
     */
    public void addNotes(Note note) {
        if (note.getId() == 0) {
            int randomNo = new Random().nextInt(50) + 1;
            note.setId(randomNo);
        }
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(note);
        mRealm.commitTransaction();
    }


    /*
    Delete a new note
    */
    public void deleteNote(final Note note) {

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Note> result = realm.where(Note.class).
                        equalTo("id", note.getId()).findAll();
                result.deleteAllFromRealm();
            }
        });
    }

    public void closeRealm() {
        mRealm.close();
    }

}
