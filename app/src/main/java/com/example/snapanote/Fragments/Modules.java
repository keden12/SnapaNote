package com.example.snapanote.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.snapanote.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Modules extends Fragment {

    CardView note;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.modulelist, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        note = (CardView) view.findViewById(R.id.moduleNote);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("modules");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("test")) {
                     note.setVisibility(View.VISIBLE);
                }
                else{
                    List<String> modulelist = new ArrayList<String>();
                    Log.e("Count " ,""+snapshot.getChildrenCount());
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        modulelist.add(String.valueOf(postSnapshot.getKey()));
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;

    }





    }
