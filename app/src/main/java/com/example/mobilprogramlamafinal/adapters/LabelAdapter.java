package com.example.mobilprogramlamafinal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobilprogramlamafinal.R;
import com.example.mobilprogramlamafinal.ui.AddLabelFragment;

import java.util.ArrayList;
public class LabelAdapter extends ArrayAdapter<String> {
    AddLabelFragment fragment;
    public LabelAdapter(Context context,ArrayList<String> labels,AddLabelFragment fragment){
        super(context,R.layout.label_description,labels);
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View convertView , ViewGroup parent){
        String label = getItem(position);

        if(convertView  == null){
            convertView  = LayoutInflater.from(getContext()).inflate(R.layout.label_description,parent,false);
        }

        TextView labelTextView = convertView.findViewById(R.id.labelTextView);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase'den silme işlemi
                String label = getItem(position);
                fragment.deleteLabelFromFirebase(label, position);
            }
        });
        labelTextView.setText(label);
        return convertView;
    }

}
