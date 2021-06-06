package com.example.abdo.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<NoteDetails> {
    ArrayList<NoteDetails> detailsArrayList;

    public NoteAdapter(@NonNull Context context, @NonNull List<NoteDetails> objects) {
        super(context, 0, objects);
        this.detailsArrayList = (ArrayList<NoteDetails>) objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_note, parent, false);

        TextView textView1 = convertView.findViewById(R.id.title_note_card);

        NoteDetails noteDetails = getItem(position);

        textView1.setText(noteDetails.getNoteTitle());


        return convertView;
    }


    public void filter(String text) {

        if (text.isEmpty()) {
            detailsArrayList.clear();
            detailsArrayList.addAll(detailsArrayList);
        } else {
            ArrayList<NoteDetails> result = new ArrayList<>();
            text = text.toLowerCase();
            for (NoteDetails item : detailsArrayList) {
                //match by name or phone
                if (item.getNoteTitle().toLowerCase().contains(text)) {
                    result.add(item);
                }
            }
            detailsArrayList.clear();
            detailsArrayList.addAll(result);
        }
        notifyDataSetChanged();
    }
}
