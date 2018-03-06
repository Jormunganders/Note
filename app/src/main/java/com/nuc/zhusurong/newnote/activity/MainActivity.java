package com.nuc.zhusurong.newnote.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.nuc.zhusurong.newnote.R;
import com.nuc.zhusurong.newnote.adapter.NoteAdapter;
import com.nuc.zhusurong.newnote.db.DBManager;
import com.nuc.zhusurong.newnote.model.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FloatingActionButton addBtn;
    private DBManager dm;
    private List<Note> noteDataList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private TextView emptyListTextView;
    long waitTime = 2000;
    long touchTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //初始化
    private void init() {
        dm = new DBManager(this);
        dm.readFromDB(noteDataList);
        mAdapter = new NoteAdapter(noteDataList);
        mRecyclerView = findViewById(R.id.list);
        addBtn = findViewById(R.id.add);
        emptyListTextView = findViewById(R.id.empty);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        updateView();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditNoteActivity.class);
                startActivityForResult(i, 0x123);
            }
        });
        mAdapter.setAction(new NoteAdapter.Action() {
            @Override
            public void onClick(Note note) {
                String noteId = note.getId() + "";
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("id", Integer.parseInt(noteId));
                startActivityForResult(intent, 0x111);
            }

            @Override
            public void onLongClick(Note note, final int position) {
                final int id = note.getId();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.are_you_sure)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBManager.getInstance(MainActivity.this).deleteNote(id);
                                mAdapter.removeItem(position);
                                updateView();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();

            }
        });
    }

    /**
     * 刷新数据
     */
    private void refresh() {
        noteDataList.clear();
        dm.readFromDB(noteDataList);
        mAdapter.setNotes(noteDataList);
        updateView();
    }

    //空数据更新
    private void updateView() {
        if (mAdapter.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    //listView单击事件
    private class NoteClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            /*MyAdapter.ViewHolder viewHolder = (MyAdapter.ViewHolder) view.getTag();
            */
        }
    }

    /*//listView长按事件
    private class NoteLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
            final Note note = ((MyAdapter) adapterView.getAdapter()).getItem(i);
            if (note == null) {
                return true;
            }
            final int id = note.getId();
            new AlertDialog.Builder(MainActivity.this)
                    .setView(R.string.are_you_sure)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBManager.getInstance(MainActivity.this).deleteNote(id);
                            adapter.removeItem(i);
                            updateView();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();

            return true;
        }
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.about)
                        .setView(R.layout.dialog_webview)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                WebView webView = dialog.findViewById(R.id.webview);
                webView.loadUrl("file:///android_asset/webview.html");
                dialog.show();
                break;
            case R.id.action_clean:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.are_you_sure)
                        .setPositiveButton(R.string.clean, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int id = 0; id < 100; id++)
                                    DBManager.getInstance(MainActivity.this).deleteNote(id);
                                mAdapter.removeAllItem();
                                updateView();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();

                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refresh();
    }
}
