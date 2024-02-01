package com.example.mobilprogramlamafinal.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilprogramlamafinal.R;
import com.example.mobilprogramlamafinal.adapters.GalleryAdapter;
import com.example.mobilprogramlamafinal.classes.Gallery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private static final String TAG = "card";

    ArrayList<Gallery> carditems;

    GalleryAdapter cardItemAdapter;

    RecyclerView cardRV;
    Spinner labelsSpinner;

    FirebaseFirestore ff = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        cardRV = root.findViewById(R.id.cardRV);
        labelsSpinner = root.findViewById(R.id.labelsSpinner);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        cardRV.setLayoutManager(llm);



        carditems = new ArrayList<>();
        cardItemAdapter = new GalleryAdapter(getActivity(), carditems);
        cardRV.setAdapter(cardItemAdapter);

        getLabels();

        labelsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carditems.clear();
                getCarditems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return root;

    }
    private void getLabels() {
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        ff.collection("Labels").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> labelList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String labelTitle = document.getString("labelTitle");
                        labelList.add(labelTitle);
                    }

                    // Spinner'a verileri eklemek için ArrayAdapter kullan
                    ArrayAdapter<String> labelAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, labelList);
                    labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    labelsSpinner.setAdapter(labelAdapter);
                } else {
                    // Hata durumunda log
                    Log.d(TAG, "Error getting labels: ", task.getException());
                }
            }
        });
    }
    private void getCarditems() {
        ff.collection("Gallery").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){
                    Integer index = 0;

                    for (QueryDocumentSnapshot eleman: task.getResult()){
                        Log.d(TAG, "veri çekildi");
                        Gallery gm = eleman.toObject(Gallery.class);
                        List<String> labels = (List<String>) eleman.get("labels");
                        List<String> descriptions = (List<String>) eleman.get("descriptions");
                        String labelTo = "";
                        String descsTo = "";
                        for(String label : labels){
                            if(labelTo != "")
                                labelTo = labelTo + "," +label;
                            else
                                labelTo = label;
                        }
                        for(String description : descriptions){
                            if(descsTo != "")
                                descsTo = descsTo + "," +description;
                            else
                                descsTo = description;
                        }
                        gm.setLabel(labelTo);
                        gm.setDescription(descsTo);
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + gm.getImage() + ".png");

                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                gm.setImage(uri.toString());
                                if(gm.getLabel().contains(labelsSpinner.getSelectedItem().toString()))
                                {
                                    carditems.add(gm);
                                    cardItemAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        index++;
                    }
                }else{
                    Log.d(TAG, task.getException().toString());
                }
            }
        });

    }
}