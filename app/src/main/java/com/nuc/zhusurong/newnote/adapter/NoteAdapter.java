package com.nuc.zhusurong.newnote.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nuc.zhusurong.newnote.R;
import com.nuc.zhusurong.newnote.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private static final String TAG = "NoteAdapter";

    private Action mAction;

    private List<Note> notes = new ArrayList<>();

    public NoteAdapter(List<Note> notes) {
        if (notes != null) {
            this.notes.clear();
            this.notes.addAll(notes);
        }
    }

    public void setNotes(List<Note> list) {
        if (list != null) {
            notes.clear();
            notes.addAll(list);
            Log.i(TAG, "setNotes: " + notes);
            notifyDataSetChanged();
        }
    }

    public void setAction(Action action) {
        this.mAction = action;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row, null);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, final int position) {
        final Note note = notes.get(position);
        holder.tvId.setText(note.getId() + "");
        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());
        holder.tvTime.setText(note.getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null) {
                    mAction.onClick(note);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mAction != null) {
                    mAction.onLongClick(note, position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void removeAllItem() {
        notes.clear();
        notifyDataSetChanged();
    }

    //从List移除对象
    public void removeItem(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }

    public boolean isEmpty() {
        return notes.isEmpty();
    }

    static class NoteHolder extends RecyclerView.ViewHolder {

        TextView tvId;
        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;

        NoteHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.note_id);
            tvTitle = itemView.findViewById(R.id.note_title);
            tvContent = itemView.findViewById(R.id.note_content);
            tvTime = itemView.findViewById(R.id.note_time);
        }
    }

    public interface Action {

        void onClick(Note note);

        void onLongClick(Note note, int position);
    }

}
