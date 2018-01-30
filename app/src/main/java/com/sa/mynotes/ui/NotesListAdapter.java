package com.sa.mynotes.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sa.mynotes.R;
import com.sa.mynotes.models.Note;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by deepak on 26/11/17.
 */

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesItemViewHolder> {

    private List<Note> mNotesList;

    public void updateDataSet(List<Note> mNotesList) {
        this.mNotesList = mNotesList;
        notifyDataSetChanged();
    }

    @Override
    public NotesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.notes_item_view, parent, false);
        return new NotesItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesItemViewHolder holder, int position) {
        Note note = mNotesList.get(position);
        holder.dateCreatedLabel.setText(note.getDateAdded());
        holder.descriptionLabel.setText(note.getDescription());
        holder.titleLabel.setText(note.getTitle());
        holder.cardItemView.setTag(note);
    }

    @Override
    public int getItemCount() {
        return mNotesList.size();
    }

    public class NotesItemViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.date_created)
        TextView dateCreatedLabel;

        @BindView(R.id.description)
        TextView descriptionLabel;

        @BindView(R.id.card_note)
        CardView cardItemView;

        @BindView(R.id.notes_title)
        TextView titleLabel;

        private Context context;

        @OnClick(R.id.card_note)
        public void onNoteClicked(View view) {
            Note note = (Note) view.getTag();
            Intent noteAddOrEditScreenIntent = new Intent(context, NotesAddOrEditActivity.class);
            noteAddOrEditScreenIntent.putExtra(NotesAddOrEditActivity.LAUNCH_MODE, NotesAddOrEditActivity.LAUNCH_MODE_EDIT);
            noteAddOrEditScreenIntent.putExtra(NotesAddOrEditActivity.NOTES_DATA, Parcels.wrap(note));
            context.startActivity(noteAddOrEditScreenIntent);
        }

        public NotesItemViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }
    }
}
