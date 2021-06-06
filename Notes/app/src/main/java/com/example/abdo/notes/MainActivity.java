package com.example.abdo.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    NoteDetails noteDetails;
    DateBaseHelper dateBaseHelper;
    ArrayList<NoteDetails> detailsArrayList;
    NoteAdapter noteAdapter;
    private SearchView searchNote;
    private ListView recyclerNote;
    private FloatingActionButton addNote;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private EditText noteTitle, noteContent;
    private Button saveNote;
    private String title, content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchNote = findViewById(R.id.search_note);
        recyclerNote = findViewById(R.id.note_recycler);
        addNote = findViewById(R.id.flo_add_note);

        dateBaseHelper = new DateBaseHelper(MainActivity.this);
        detailsArrayList = new ArrayList<>();
        noteAdapter = new NoteAdapter(this, detailsArrayList);
        recyclerNote.setAdapter(noteAdapter);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialogNote();
            }
        });

        recyclerNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                noteDetails = (NoteDetails) recyclerNote.getAdapter().getItem(position);
                editDialogNote(noteDetails);


            }
        });

        recyclerNote.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                noteDetails = (NoteDetails) recyclerNote.getAdapter().getItem(position);
                deleteDialogNote(noteDetails);

                return false;
            }
        });

        searchNote.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noteAdapter.getFilter().filter(query);
              //  recyclerNote.setAdapter(noteAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getFilter().filter(newText);
               // recyclerNote.setAdapter(noteAdapter);
                return false;
            }
        });



    }

    private void addDialogNote() {
        View view = getLayoutInflater().inflate(R.layout.addnote_menu, null);
        noteTitle = view.findViewById(R.id.title_add_note);
        noteContent = view.findViewById(R.id.content_add_note);
        saveNote = view.findViewById(R.id.save_note);

        builder = new AlertDialog.Builder(this);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = noteTitle.getText().toString();
                content = noteContent.getText().toString();
                if (title.isEmpty() || content.isEmpty())
                    Toast.makeText(MainActivity.this, R.string.empty_erro, Toast.LENGTH_SHORT).show();
                else {
                    noteDetails = new NoteDetails(title, content);
                    dateBaseHelper.addNote(noteDetails);
                    alertDialog.dismiss();
                    onResume();
                }
            }
        });
    }

    private void deleteDialogNote(NoteDetails noteDetails) {

        builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.title_delete)
                .setMessage(R.string.ask_delete)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int id = noteDetails.getId();
                        dateBaseHelper.deleteNote(id);
                        onResume();
                    }
                }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        alertDialog.dismiss();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void editDialogNote(NoteDetails noteDetails) {

        View view = getLayoutInflater().inflate(R.layout.addnote_menu, null);
        EditText noteTitle = view.findViewById(R.id.title_add_note);
        EditText noteContent = view.findViewById(R.id.content_add_note);
        Button editNote = view.findViewById(R.id.save_note);

        editNote.setText(R.string.edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        title = noteDetails.getNoteTitle();
        content = noteDetails.getNoteContent();
        int id = noteDetails.getId();
        noteTitle.setText(title);
        noteContent.setText(content);


        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = noteTitle.getText().toString();
                content = noteContent.getText().toString();

                dateBaseHelper.updateNote(new NoteDetails(title, content, id));
                alertDialog.dismiss();
                onResume();
            }
        });
    }

    protected void onResume() {

        super.onResume();
        detailsArrayList = dateBaseHelper.getAllNotesDetails();
        NoteAdapter noteAdapter = new NoteAdapter(MainActivity.this, detailsArrayList);
        recyclerNote.setAdapter(noteAdapter);

    }
}
