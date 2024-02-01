package com.example.mobilprogramlamafinal.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import android.widget.ListView;


import com.example.mobilprogramlamafinal.R;
import com.example.mobilprogramlamafinal.adapters.LabelAdapter;
import com.example.mobilprogramlamafinal.classes.Labels;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.local.QueryResult;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;
import android.widget.ArrayAdapter;
public class AddLabelFragment extends Fragment {

    private EditText labelT,labelDesc;
    private Button submitBtn;
    private ListView labels;
    private ArrayList<String> labelList;
    private ArrayAdapter<String> labelAdapter;
    private CollectionReference databaseReferance;
    private FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_addlabel,container,false);
        databaseReferance = FirebaseFirestore.getInstance().collection("Labels");
        labelT = view.findViewById(R.id.aL_label);
        labelDesc = view.findViewById(R.id.aL_desc);
        submitBtn = view.findViewById(R.id.aL_submit);
        labels = view.findViewById(R.id.aL_labels);
        labelList = new ArrayList<>();
        labelAdapter = new LabelAdapter(getContext(),labelList,this);
        labels.setAdapter(labelAdapter);
        checkLabels();
        user = FirebaseAuth.getInstance().getCurrentUser();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLabel();
            }
        });
        return view;
    }
    private void checkLabels(){
        databaseReferance.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    labelList.clear(); // Mevcut verileri temizle
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Firebase'den belgeyi al, belgedeki veriyi labelList'e ekle
                        databaseReferance.document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                labelList.add(task.getResult().getString("labelTitle"));
                                labelAdapter.notifyDataSetChanged(); // Listenin güncellendiğini bildir
                            }
                        });
                    }
                }
            }
        });
    }
    private void addLabel(){
        String labelTitle = labelT.getText().toString().trim();
        String labelDescription = labelDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(labelTitle) && !TextUtils.isEmpty(labelDescription)){
            Labels labelDb = new Labels(labelTitle,labelDescription);
            String labelId = databaseReferance.getId();
            databaseReferance.add(labelDb);
            labelList.add(labelTitle);
            labelT.setText("");
            labelDesc.setText("");
            checkLabels();
        }
        else{
            Toast.makeText(getView().getContext(), "Etiket ve açıklama kısımları boş bırakılamaz.", Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteLabelFromFirebase(String label, int position) {
        databaseReferance.whereEqualTo("labelTitle", label)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                databaseReferance.document(document.getId()).delete();
                            }
                            // Firebase'den ve ListView'dan silme işlemleri
                            labelList.remove(position);
                            labelAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


}