package com.example.snapanote.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.snapanote.Controllers.Base;
import com.example.snapanote.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class Logged extends Fragment {

    FloatingActionButton camera;
    ImageView scannedImageView;
    CardView modules;
    private StorageReference mRef;
    private FirebaseAuth auth;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.logged, container, false);


        camera = (FloatingActionButton) view.findViewById(R.id.photo);
        modules = (CardView) view.findViewById(R.id.moduleCard);

        mRef = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(view);

            }
        });

        modules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Base)getActivity()).setViewPager(3);
            }
        });

        return view;

    }

    public void openCamera(View v){
        int REQUEST_CODE = 99;
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);



    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                FirebaseUser user = auth.getCurrentUser();
                String userID = user.getUid();
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                StorageReference storageReference = mRef.child("users/"+userID+"/modules");
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String upload = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                        Log.d(TAG,"Upload Success");
                    }
                });
                // getActivity().getContentResolver().delete(uri, null, null);
                // scannedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }








}
