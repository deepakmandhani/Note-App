package com.sa.mynotes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sa.mynotes.R;
import com.sa.mynotes.models.Note;
import com.sa.mynotes.repo.NotesRepoWrapper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NotesListActivity extends AppCompatActivity {

    @BindView(R.id.notes_list)
    RecyclerView mNotesListView;

    @BindView(R.id.no_notes_avaiable_text)
    TextView mNoNotesAvailableMessage;

    @BindView(R.id.add_new_note_view)
    FloatingActionButton mAddNewNoteButton;

    private NotesListAdapter mNotesListAdapter;
    private Unbinder mUnbinder;

    @OnClick(R.id.add_new_note_view)
    public void onAddNotesButtonClicked() {
        Intent noteAddOrEditScreenIntent = new Intent(this, NotesAddOrEditActivity.class);
        noteAddOrEditScreenIntent.putExtra(NotesAddOrEditActivity.LAUNCH_MODE, NotesAddOrEditActivity.LAUNCH_MODE_ADD);
        startActivity(noteAddOrEditScreenIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        mUnbinder = ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        mNotesListAdapter = new NotesListAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mNotesListView.setLayoutManager(linearLayoutManager);
        mNotesListView.setAdapter(mNotesListAdapter);

        /*
             hide and show FAB onscroll of list
         */
        mNotesListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && mAddNewNoteButton.isShown()) {
                    mAddNewNoteButton.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAddNewNoteButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    /*
        fetches notes list from repo if available
     */
    @Override
    protected void onResume() {
        super.onResume();
        fetchNotesList();
    }

    private void fetchNotesList() {
        List<Note> noteList = NotesRepoWrapper.getInstance().getNotesList();
        if (noteList.size() > 0) {
            mNotesListAdapter.updateDataSet(noteList);
            mNoNotesAvailableMessage.setVisibility(View.GONE);
            mNotesListView.setVisibility(View.VISIBLE);
        } else {
            mNoNotesAvailableMessage.setVisibility(View.VISIBLE);
            mNotesListView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotesRepoWrapper.getInstance().closeRealm();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
