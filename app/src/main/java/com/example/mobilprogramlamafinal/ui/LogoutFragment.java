package com.example.mobilprogramlamafinal.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilprogramlamafinal.MainActivity;
import com.example.mobilprogramlamafinal.R;
import com.example.mobilprogramlamafinal.activities.SplashScren;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {

    FirebaseAuth fbase;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_logout,container,false);
        fbase = FirebaseAuth.getInstance();
        if(fbase != null && fbase.getCurrentUser() != null)
        {
            fbase.signOut();
            startActivity(new Intent(getActivity(), SplashScren.class));
        }
        return root;
    }
}