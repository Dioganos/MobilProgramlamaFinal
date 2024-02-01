package com.example.mobilprogramlamafinal.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mobilprogramlamafinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;


public class AddPictureFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private ImageView selectedPhotoImageView;
    private Bitmap selectedPhotoBitmap;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private ArrayList<String> labelsList;
    private ArrayList<String> labelsDescsList;
    private ArrayAdapter<String> labelsAdapter;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addpicture, container, false);


        // Firebase bağlantıları
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // XML elemanlarına erişim
        ImageView galleryPhoto = root.findViewById(R.id.galleryPhoto);
        ImageView cameraPhoto = root.findViewById(R.id.cameraPhoto);
        selectedPhotoImageView = root.findViewById(R.id.imageView3);
        ListView labelsListView = root.findViewById(R.id.labels);
        Button ekleButton = root.findViewById(R.id.button);

        // Gallery tuşuna tıklanınca galeri açılsın
        galleryPhoto.setOnClickListener(v -> openGallery());

        // Camera tuşuna tıklanınca kamera açılsın
        cameraPhoto.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());

        // Etiket listesi için ArrayAdapter ve ArrayList oluştur
        labelsList = new ArrayList<>();
        labelsDescsList = new ArrayList<>();
        labelsAdapter = new ArrayAdapter<>(requireContext(), R.layout.label_selection, R.id.labelTextView, labelsList);

        labelsListView.setAdapter(labelsAdapter);

        // Etiket verilerini Firestore'dan çek ve listeye ekle
        db.collection("Labels")
                .get()
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String labelTitle = document.getString("labelTitle");
                            String labelDesc = document.getString("labelDescription");
                            labelsList.add(labelTitle);
                            labelsDescsList.add(labelDesc);
                        }
                        labelsAdapter.notifyDataSetChanged();
                }
        });

        labelsListView.setOnItemClickListener((parent, view, position, id) -> {
            for (int i = 0; i < labelsListView.getChildCount(); i++) {
                View listItem = labelsListView.getChildAt(i);
                CheckBox checkBox = listItem.findViewById(R.id.checkBoxLabel);
                if (checkBox != null) {
                    // Do something with the CheckBox, for example, setChecked or perform other actions
                    checkBox.setChecked(!checkBox.isChecked());
                }
            }
        });

        // Ekle butonu tıklanınca Firestore'a kayıt ekle
        ekleButton.setOnClickListener(v ->
        {
            // Seçili etiketleri al
            ArrayList<String> selectedLabels = new ArrayList<>();
            ArrayList<String> selectedLabelsDescs = new ArrayList<>();
            Integer index = 0;
            for (int i = 0; i < labelsListView.getChildCount(); i++)
            {
                View listItem = labelsListView.getChildAt(i);
                CheckBox checkBox = listItem.findViewById(R.id.checkBoxLabel);
                if (checkBox.isChecked()) {
                    selectedLabels.add(labelsList.get(i));
                    selectedLabelsDescs.add(labelsDescsList.get(i));
                    index++;
                }
            }
            // Firestore'da "Gallery" koleksiyonuna resim ve kullanıcı adını ekle

            // PNG dosyasını oluştur
            String fileName = String.valueOf(System.currentTimeMillis()); // İstediğiniz dosya adını kullanabilirsiniz
            boolean saveSuccess = saveBitmapAsPNG(requireContext(), selectedPhotoBitmap, fileName);

            if (saveSuccess) {
                // PNG dosyası başarıyla oluşturulduysa Firebase Storage'a yükleme işlemi yap
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + fileName + ".png");
                File file = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName + ".png");

                // Bitmap'i byte dizisine çevir
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bitmapData = baos.toByteArray();

                // Byte dizisini Firebase Storage'a yükle
                storageRef.putBytes(bitmapData)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Başarılı yükleme
                            // İstediğiniz bir işlemi gerçekleştirebilirsiniz

                            // Dosyayı silebilirsiniz, çünkü artık Firebase Storage'da bulunuyor
                            file.delete();
                        })
                        .addOnFailureListener(e -> {
                            // Hata durumunda
                            // Hata mesajını e.getMessage() ile alabilirsiniz
                        });
            } else {
                // PNG dosyası oluşturulamadı
                fileName = "";
                // Hata mesajını veya kullanıcıya bilgi verebilirsiniz
            }

            // Firestore'da "Gallery" koleksiyonuna resim ve kullanıcı adını ekle
            db.collection("Gallery")
                    .add(new GalleryItem(selectedLabelsDescs, selectedLabels, fileName,currentUser.getEmail()))
                    .addOnSuccessListener(documentReference -> {
                        // Başarılı ekleme
                        // İstediğiniz bir işlemi gerçekleştirebilirsiniz
                    })
                    .addOnFailureListener(e -> {
                        // Hata durumunda
                        // Hata mesajını e.getMessage() ile alabilirsiniz
                    });
        });

        return root;
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }
    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Kamera izni yoksa izin iste
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Kamera izni varsa kamera aç
            openCamera();
        }
    }
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    selectedPhotoBitmap = (Bitmap) extras.get("data");
                    selectedPhotoImageView.setImageBitmap(selectedPhotoBitmap);
                }
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                    selectedPhotoBitmap = bitmap;
                    selectedPhotoImageView.setImageBitmap(selectedPhotoBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static boolean saveBitmapAsPNG(Context context, Bitmap bitmap, String fileName) {
        // Kontrolleri yapın
        if (bitmap == null) {
            return false;
        }

        // Dosya adını alın
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(directory, fileName + ".png");

        // Dosyayı oluşturun ve kaydetme işlemini gerçekleştirin
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Firestore'da "Gallery" koleksiyonu için bir model sınıfı
    private static class GalleryItem {
        private String username;
        private ArrayList<String> labels;

        private ArrayList<String> descriptions;
        private String image;

        public GalleryItem(ArrayList<String> descriptions,ArrayList<String> labels,String image,String username) {
            this.username = username;
            this.labels = labels;
            this.descriptions = descriptions;
            this.image = image;
        }
        public GalleryItem() {
        }

        // Gerekirse getter ve setter metotlarını ekleyebilirsiniz
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public ArrayList<String> getLabels() {
            return labels;
        }

        public void setLabels(ArrayList<String> labels) {
            this.labels = labels;
        }
        public ArrayList<String> getDescriptions() {
            return descriptions;
        }

        public void setDescriptions(ArrayList<String> descriptions) {
            this.descriptions = descriptions;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
